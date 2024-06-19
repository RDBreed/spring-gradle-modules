package eu.phaf.news;

import eu.phaf.stateman.PostgresConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class TestContainerConfiguration {
    @Bean
    public PostgreSQLContainer<?> container() {
        PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres"))
                .withReuse(true);
        postgres.start();
        return postgres;
    }

    @Bean
    @Primary
    public PostgresConnection postgresConnection(PostgreSQLContainer<?> container) {
        return new PostgresConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
    }
}
