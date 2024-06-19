package eu.phaf.stateman.retry;

import eu.phaf.stateman.PostgresConnection;
import eu.phaf.stateman.PostgresTaskRepository;
import eu.phaf.stateman.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class PostgresRetryTaskRepositoryTest {
    @Container
    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres"))
            .withReuse(true);

    @BeforeAll
    public static void clean() throws SQLException {
        PostgresConnection postgresConnection = new PostgresConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        try (Connection connection = postgresConnection.connect()) {
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS RETRY_TASKS");
        }
    }

    @Test
    public void shouldCreateTable() throws SQLException {
        PostgresConnection postgresConnection = new PostgresConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        PostgresRetryTaskRepository postgresTaskRepository = new PostgresRetryTaskRepository(postgresConnection);
        postgresTaskRepository.generateTable();
        try (Connection connection = postgresConnection.connect()) {
            Statement statement = connection.createStatement();
            boolean execute = statement.execute("SELECT * FROM RETRY_TASKS");
            assertTrue(execute);
        }
    }

    @Test
    public void shouldCreateTask() throws SQLException {
        PostgresConnection postgresConnection = new PostgresConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        PostgresRetryTaskRepository postgresTaskRepository = new PostgresRetryTaskRepository(postgresConnection);
        postgresTaskRepository.generateTable();
        String methodName = "getTask" + UUID.randomUUID();
        postgresTaskRepository.save(new RetryTask(new Task(PostgresTaskRepository.class, methodName, List.of(String.class, Class.class)), Duration.ofSeconds(3), 3, methodName));
        try (Connection connection = postgresConnection.connect()) {
            Statement statement = connection.createStatement();
            ResultSet execute = statement.executeQuery("SELECT * FROM RETRY_TASKS WHERE METHOD_NAME = '" + methodName + "'");
            int count = 0;
            while (execute.next()) {
                count++;
            }
            assertTrue(count > 0);
        }
    }

    @Test
    public void shouldGetTask() {
        PostgresConnection postgresConnection = new PostgresConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        PostgresRetryTaskRepository postgresTaskRepository = new PostgresRetryTaskRepository(postgresConnection);
        postgresTaskRepository.generateTable();
        String methodName = "getTask" + UUID.randomUUID();
        Task task = new Task(PostgresTaskRepository.class, methodName, List.of(String.class, Class.class));
        postgresTaskRepository.save(new RetryTask(task, Duration.ofSeconds(3), 3, methodName));
        Optional<RetryTask> getTask = postgresTaskRepository.getByTask(task);
        assertTrue(getTask.isPresent());
    }

}
