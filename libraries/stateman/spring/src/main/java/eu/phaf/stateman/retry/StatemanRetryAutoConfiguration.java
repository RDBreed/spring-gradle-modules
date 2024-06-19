package eu.phaf.stateman.retry;

import eu.phaf.stateman.SimpleRetryJobHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
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
    public RetryTaskRepository retryTaskRepository() {
        return new RetryTaskRepository.InMemoryRetryTaskRepository();
    }

    @Bean
    public RetryTaskActionRepository retryTaskActionRepository() {
        return new RetryTaskActionRepository.InMemoryRetryTaskActionRepository();
    }
}
