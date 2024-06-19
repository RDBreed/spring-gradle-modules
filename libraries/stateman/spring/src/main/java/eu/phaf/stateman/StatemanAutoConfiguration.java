package eu.phaf.stateman;

import eu.phaf.stateman.retry.RetryTaskManager;
import eu.phaf.stateman.retry.StoredRetry;
import eu.phaf.stateman.retry.StoredRetryAspect;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;

import static eu.phaf.stateman.ReflectionUtils.getParameterNames;

@Configuration
public class StatemanAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(TaskRepository.class)
    public TaskRepository taskRepository() {
        return new TaskRepository.InMemoryTaskRepository();
    }

    @Bean
    public TaskManager taskManager(RetryTaskManager retryTaskManager) {
        return new TaskManager(
                taskRepository(),
                taskActionRepository(),
                retryTaskManager
        );
    }

    @Bean
    @ConditionalOnMissingBean(TaskActionRepository.class)
    public TaskActionRepository taskActionRepository() {
        return new TaskActionRepository.InMemoryTaskActionRepository();
    }

    @Bean
    public StoredRetryAspect storedRetryAspect(TaskManager taskManager) {
        return new StoredRetryAspect(taskManager);
    }

    @Configuration
    public class StoredRetryScanner implements ApplicationContextAware {
        private final TaskManager taskManager;

        public StoredRetryScanner(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            for (String beanDefinitionName : applicationContext.getBeanDefinitionNames()) {
                if (!beanDefinitionName.equals("eu.phaf.stateman.StatemanAutoConfiguration$StoredRetryScanner")) {
                    Object bean = applicationContext.getBean(beanDefinitionName);
                    List<Method> methods = MethodUtils.getMethodsListWithAnnotation(AopUtils.getTargetClass(bean), StoredRetry.class);
                    for (Method method : methods) {
                        StoredRetry annotation = method.getAnnotation(StoredRetry.class);
                        taskManager.registerTask(
                                method.getName(),
                                AopUtils.getTargetClass(bean),
                                getParameterNames(method),
                                Duration.parse(annotation.duration()),
                                annotation.maxAttempts(),
                                annotation.retryMethod()
                        );
                    }
                }
            }
        }
    }
}
