package eu.phaf.news.infrastructure.config;

import eu.phaf.stateman.PostgresConnection;
import eu.phaf.stateman.PostgresTaskActionRepository;
import eu.phaf.stateman.PostgresTaskRepository;
import eu.phaf.stateman.TaskActionRepository;
import eu.phaf.stateman.TaskRepository;
import eu.phaf.stateman.retry.PostgresRetryTaskActionRepository;
import eu.phaf.stateman.retry.PostgresRetryTaskRepository;
import eu.phaf.stateman.retry.RetryTaskActionRepository;
import eu.phaf.stateman.retry.RetryTaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class StatemanDependencyInjection {

    @Bean
    public PostgresConnection postgresConnection() {
        // TODO!
        return new PostgresConnection("", "", "");
    }

    @Bean
    @Primary
    public TaskActionRepository taskActionRepository(PostgresConnection postgresConnection) {
        return new PostgresTaskActionRepository(postgresConnection);
    }

    @Bean
    @Primary
    public TaskRepository taskRepository(PostgresConnection postgresConnection) {
        return new PostgresTaskRepository(postgresConnection);
    }

    @Bean
    @Primary
    public RetryTaskRepository retryTaskRepository(PostgresConnection postgresConnection) {
        return new PostgresRetryTaskRepository(postgresConnection);
    }

    @Bean
    @Primary
    public RetryTaskActionRepository retryTaskActionRepository(PostgresConnection postgresConnection) {
        return new PostgresRetryTaskActionRepository(postgresConnection);
    }
}
