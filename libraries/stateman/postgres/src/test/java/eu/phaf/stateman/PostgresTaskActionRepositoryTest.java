package eu.phaf.stateman;

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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class PostgresTaskActionRepositoryTest {
    @Container
    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres"))
            .withReuse(true);

    @BeforeAll
    public static void clean() throws SQLException {
        PostgresConnection postgresConnection = new PostgresConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        try (Connection connection = postgresConnection.connect()) {
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS TASK_ACTIONS");
        }
    }

    @Test
    public void shouldCreateTable() throws SQLException {
        PostgresConnection postgresConnection = new PostgresConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        PostgresTaskActionRepository postgresTaskActionRepository = new PostgresTaskActionRepository(postgresConnection);
        postgresTaskActionRepository.generateTable();
        try (Connection connection = postgresConnection.connect()) {
            Statement statement = connection.createStatement();
            boolean execute = statement.execute("SELECT * FROM TASK_ACTIONS");
            assertTrue(execute);
        }
    }

    @Test
    public void shouldCreateTaskAction() throws SQLException {
        PostgresConnection postgresConnection = new PostgresConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        PostgresTaskActionRepository postgresTaskActionRepository = new PostgresTaskActionRepository(postgresConnection);
        postgresTaskActionRepository.generateTable();
        String methodName = "getTask" + UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();
        postgresTaskActionRepository.save(new TaskAction(new Task(PostgresTaskRepository.class, methodName,
                List.of(String.class, Class.class)),
                List.of(new ParameterClassAndValue<>(String.class, methodName), new ParameterClassAndValue<>(String.class, PostgresTaskRepository.class.getName())),
                now,
                TaskAction.TaskType.ENDED));
        try (Connection connection = postgresConnection.connect()) {
            Statement statement = connection.createStatement();
            ResultSet execute = statement.executeQuery("SELECT * FROM TASK_ACTIONS WHERE METHOD_NAME = '" + methodName + "'");
            int count = 0;
            while (execute.next()) {
                count++;
            }
            assertEquals(1, count);
        }
    }

}
