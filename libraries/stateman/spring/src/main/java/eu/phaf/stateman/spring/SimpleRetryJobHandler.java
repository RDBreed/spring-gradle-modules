package eu.phaf.stateman.spring;

import eu.phaf.stateman.RetryJob;
import eu.phaf.stateman.RetryJobHandler;
import eu.phaf.stateman.RetryTask;
import eu.phaf.stateman.RetryTaskAction;
import eu.phaf.stateman.RetryTaskActionRepository;
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

    private static Consumer<RetryTaskAction> runSpringBean(RetryTask retryTask, ApplicationContext applicationContext) {
        return retryTaskAction -> {
            var task = retryTask.task();
            Class<?> theClass = task.theClass();
            Object bean = applicationContext.getBean(theClass);
            try {
                Method method = bean.getClass().getMethod(retryTask.retryMethod(), task.parameters().toArray(new Class<?>[0]));
                method.invoke(bean, retryTaskAction.taskAction().parameterValues().values().toArray());
            } catch (Exception e) {
                LOG.debug("Error occurred during call of {}", retryTask.task().methodName(), e);
            }
        };
    }
}
