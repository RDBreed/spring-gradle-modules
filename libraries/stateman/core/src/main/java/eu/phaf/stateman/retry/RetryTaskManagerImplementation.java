package eu.phaf.stateman.retry;

import eu.phaf.stateman.Task;
import eu.phaf.stateman.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RetryTaskManagerImplementation implements RetryTaskManager {
    private final RetryTaskRepository retryTaskRepository;
    private final RetryTaskActionRepository retryTaskActionRepository;
    private final RetryJobHandler retryJobHandler;
    private final static Logger LOG = LoggerFactory.getLogger(RetryTaskManager.class);

    public RetryTaskManagerImplementation(RetryTaskRepository retryTaskRepository, RetryTaskActionRepository retryTaskActionRepository, RetryJobHandler retryJobHandler) {
        this.retryTaskRepository = retryTaskRepository;
        this.retryTaskActionRepository = retryTaskActionRepository;
        this.retryJobHandler = retryJobHandler;
    }

    public void createRetryTask(Task task, Duration retryDuration, Integer maxAttempts, String retryMethod) {
        RetryTask retryTask = new RetryTask(task, retryDuration, maxAttempts, retryMethod);
        retryTaskRepository.save(retryTask);
        retryJobHandler.addRetryJob(retryTask);
    }

    public void createRetryTaskActions(Task task, TaskAction taskAction, OffsetDateTime now) {
        Optional<RetryTask> optionalRetryTask = retryTaskRepository.getByTask(task);
        optionalRetryTask.ifPresent(retryTask -> {
            List<OffsetDateTime> retryTimes = new ArrayList<>();
            for (int i = 1; i <= retryTask.maxAttempts(); i++) {
                OffsetDateTime retryTime = now.plus(retryTask.retryAfter().multipliedBy(i));
                retryTimes.add(retryTime);
                LOG.info("Creating retry task actions for {} with retrytime {}", task.methodName(), retryTime);
            }
            retryTaskActionRepository.save(new RetryTaskAction(
                    task,
                    retryTask.retryMethod(),
                    taskAction.parameterValues(),
                    taskAction.offsetDateTime(),
                    retryTimes));
        });
    }
}
