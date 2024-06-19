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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class PostgresRetryTaskActionRepositoryTest {
    @Container
    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres"))
            .withReuse(true);

    @BeforeAll
    public static void clean() throws SQLException {
        PostgresConnection postgresConnection = new PostgresConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        try (Connection connection = postgresConnection.connect()) {
            Statement statement = connection.createStatement();
            statement.execute("DROP INDEX IF EXISTS IDX_FIRST_RETRY_TIME");
            statement.execute("DROP TABLE IF EXISTS RETRY_TASK_ACTIONS");
        }
    }

    @Test
    public void shouldCreateTable() throws SQLException {
        PostgresConnection postgresConnection = new PostgresConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        PostgresRetryTaskActionRepository postgresTaskRepository = new PostgresRetryTaskActionRepository(postgresConnection);
        postgresTaskRepository.generateTable();
        try (Connection connection = postgresConnection.connect()) {
            Statement statement = connection.createStatement();
            boolean execute = statement.execute("SELECT * FROM RETRY_TASK_ACTIONS");
            assertTrue(execute);
        }
    }

    @Test
    public void shouldCreateTask() throws SQLException {
        PostgresConnection postgresConnection = new PostgresConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        PostgresRetryTaskActionRepository postgresTaskRepository = new PostgresRetryTaskActionRepository(postgresConnection);
        postgresTaskRepository.generateTable();
        String methodName = "getTask" + UUID.randomUUID();
        String retryMethodName = "getTask" + UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        postgresTaskRepository.save(new RetryTaskAction(new Task(PostgresTaskRepository.class, methodName, List.of(String.class, Class.class)),
                retryMethodName,
                Map.of("methodName", methodName, "theClass", PostgresTaskRepository.class.getName()),
                now,
                List.of(now, now.plusDays(3), now.plusDays(6))
        ));
        try (Connection connection = postgresConnection.connect()) {
            Statement statement = connection.createStatement();
            ResultSet execute = statement.executeQuery("SELECT * FROM RETRY_TASK_ACTIONS WHERE RETRY_METHOD_NAME = '" + retryMethodName + "'");
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
        PostgresRetryTaskActionRepository postgresTaskRepository = new PostgresRetryTaskActionRepository(postgresConnection);
        postgresTaskRepository.generateTable();
        String methodName = "getTask" + UUID.randomUUID();
        String retryMethodName = "getTask" + UUID.randomUUID();
        Task task = new Task(PostgresTaskRepository.class, methodName, List.of(String.class, Class.class));
        OffsetDateTime now = OffsetDateTime.now();
        postgresTaskRepository.save(new RetryTaskAction(new Task(PostgresTaskRepository.class, methodName, List.of(String.class, Class.class)),
                retryMethodName,
                Map.of("methodName", methodName, "theClass", PostgresTaskRepository.class.getName()),
                now,
                List.of(now, now.plusDays(3), now.plusDays(6))
        ));
        Optional<RetryTaskAction> getTask = postgresTaskRepository.getFirstRetryTaskAction(task, retryMethodName, now.plusSeconds(1));
        assertTrue(getTask.isPresent());
        assertEquals(getTask.get().offsetDateTimes().getFirst(), now);
    }

    @Test
    public void shouldGetTaskAndRemove() throws SQLException {
        PostgresConnection postgresConnection = new PostgresConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        PostgresRetryTaskActionRepository postgresTaskRepository = new PostgresRetryTaskActionRepository(postgresConnection);
        postgresTaskRepository.generateTable();
        String methodName = "getTask" + UUID.randomUUID();
        String retryMethodName = "getTask" + UUID.randomUUID();
        Task task = new Task(PostgresTaskRepository.class, methodName, List.of(String.class, Class.class));
        OffsetDateTime now = OffsetDateTime.now();
        postgresTaskRepository.save(new RetryTaskAction(new Task(PostgresTaskRepository.class, methodName, List.of(String.class, Class.class)),
                retryMethodName,
                Map.of("methodName", methodName, "theClass", PostgresTaskRepository.class.getName()),
                now,
                List.of(now, now.plusDays(3), now.plusDays(6))
        ));
        Optional<RetryTaskAction> getTask = postgresTaskRepository.getAndRemoveFirstRetryTaskAction(task, retryMethodName, now.plusSeconds(1));
        assertTrue(getTask.isPresent());
        assertEquals(getTask.get().offsetDateTimes().getFirst(), now);
        try (Connection connection = postgresConnection.connect()) {
            Statement statement = connection.createStatement();
            ResultSet execute = statement.executeQuery("SELECT * FROM RETRY_TASK_ACTIONS WHERE RETRY_METHOD_NAME = '" + retryMethodName + "'");
            assertEquals(0, execute.getFetchSize());
        }
    }

}
