package eu.phaf.stateman;

import eu.phaf.stateman.retry.RetryJob;
import eu.phaf.stateman.retry.RetryJobHandler;
import eu.phaf.stateman.retry.RetryTask;
import eu.phaf.stateman.retry.RetryTaskAction;
import eu.phaf.stateman.retry.RetryTaskActionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SimpleRetryJobHandler implements RetryJobHandler {
    List<RetryJob> retryJobs = new ArrayList<>();

    private final RetryTaskActionRepository retryTaskActionRepository;
    private final ApplicationContext applicationContext;
    private final static Logger LOG = LoggerFactory.getLogger(RetryJobHandler.class);

    public SimpleRetryJobHandler(RetryTaskActionRepository retryTaskActionRepository, ApplicationContext applicationContext) {
        this.retryTaskActionRepository = retryTaskActionRepository;
        this.applicationContext = applicationContext;
    }

    public void addRetryJob(RetryTask retryTask) {
        retryJobs.add(new RetryJob.SimpleRetryJob(retryTaskActionRepository, retryTask, runSpringBean(retryTask, applicationContext)));
    }

    private Consumer<RetryTaskAction> runSpringBean(RetryTask retryTask, ApplicationContext applicationContext) {
        return retryTaskAction -> {
            var task = retryTask.task();
            Class<?> theClass = task.theClass();
            Object bean = applicationContext.getBean(theClass);
            try {
                Method method = bean.getClass().getMethod(retryTask.retryMethod(), task.parameters().toArray(new Class<?>[0]));
                method.invoke(bean, retryTaskAction.parameterValues().stream().map(ParameterClassAndValue::theValue).toArray());
            } catch (Exception e) {
                LOG.debug("Error occurred during call of {}", retryTask.retryMethod(), e);
                retryTaskAction.removeFirstOffsetDateTime();
                if (!retryTaskAction.offsetDateTimes().isEmpty()) {
                    retryTaskActionRepository.save(retryTaskAction);
                } else {
                    LOG.debug("Retry task action {} does not have any retries left.", retryTask.retryMethod(), e);
                }
            }
        };
    }
}
