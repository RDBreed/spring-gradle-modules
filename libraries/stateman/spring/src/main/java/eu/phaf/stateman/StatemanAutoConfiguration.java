package eu.phaf.stateman;

import eu.phaf.stateman.retry.RetryTaskManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatemanAutoConfiguration {
    @Bean
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
    public TaskActionRepository taskActionRepository() {
        return new TaskActionRepository.InMemoryTaskActionRepository();
    }
}
