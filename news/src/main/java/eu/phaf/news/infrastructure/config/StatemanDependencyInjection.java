package eu.phaf.news.infrastructure.config;

import eu.phaf.stateman.RetryTaskActionRepository;
import eu.phaf.stateman.RetryTaskRepository;
import eu.phaf.stateman.TaskActionRepository;
import eu.phaf.stateman.TaskManager;
import eu.phaf.stateman.TaskRepository;
import eu.phaf.stateman.spring.SimpleRetryJobHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatemanDependencyInjection {
    @Bean
    public TaskRepository taskRepository() {
        return new TaskRepository.InMemoryTaskRepository();
    }

    @Bean
    public TaskManager taskManager(ApplicationContext applicationContext) {
        return new TaskManager(
                taskRepository(),
                taskActionRepository(),
                retryTaskRepository(),
                retryTaskActionRepository(),
                retryJobHandler(applicationContext)
        );
    }

    @Bean
    public SimpleRetryJobHandler retryJobHandler(ApplicationContext applicationContext) {
        return new SimpleRetryJobHandler(retryTaskActionRepository(), applicationContext);
    }

    @Bean
    public TaskActionRepository taskActionRepository() {
        return new TaskActionRepository.InMemoryTaskActionRepository();
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
