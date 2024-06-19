package eu.phaf.stateman.retry;

import eu.phaf.stateman.SimpleRetryJobHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatemanRetryAutoConfiguration {
    @Bean
    public RetryTaskManager retryTaskManager(ApplicationContext applicationContext) {
        return new RetryTaskManagerImplementation(retryTaskRepository(),
                retryTaskActionRepository(),
                retryJobHandler(applicationContext));
    }

    @Bean
    public SimpleRetryJobHandler retryJobHandler(ApplicationContext applicationContext) {
        return new SimpleRetryJobHandler(retryTaskActionRepository(), applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean(RetryTaskRepository.class)
    public RetryTaskRepository retryTaskRepository() {
        return new RetryTaskRepository.InMemoryRetryTaskRepository();
    }

    @Bean
    @ConditionalOnMissingBean(RetryTaskActionRepository.class)
    public RetryTaskActionRepository retryTaskActionRepository() {
        return new RetryTaskActionRepository.InMemoryRetryTaskActionRepository();
    }
}
