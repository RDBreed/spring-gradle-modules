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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
public class PostgresTaskRepositoryTest {
    @Container
    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres"))
            .withReuse(true);

    @BeforeAll
    public static void clean() throws SQLException {
        PostgresConnection postgresConnection = new PostgresConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        try (Connection connection = postgresConnection.connect()) {
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE IF EXISTS TASKS");
        }
    }

    @Test
    public void shouldCreateTable() throws SQLException {
        PostgresConnection postgresConnection = new PostgresConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        PostgresTaskRepository postgresTaskRepository = new PostgresTaskRepository(postgresConnection);
        postgresTaskRepository.generateTable();
        try (Connection connection = postgresConnection.connect()) {
            Statement statement = connection.createStatement();
            boolean execute = statement.execute("SELECT * FROM TASKS");
            assertTrue(execute);
        }
    }

    @Test
    public void shouldCreateTask() throws SQLException {
        PostgresConnection postgresConnection = new PostgresConnection(container.getJdbcUrl(), container.getUsername(), container.getPassword());
        PostgresTaskRepository postgresTaskRepository = new PostgresTaskRepository(postgresConnection);
        postgresTaskRepository.generateTable();
        String methodName = "getTask" + UUID.randomUUID();
        postgresTaskRepository.save(new Task(PostgresTaskRepository.class, methodName, List.of(String.class, Class.class)));
        try (Connection connection = postgresConnection.connect()) {
            Statement statement = connection.createStatement();
            ResultSet execute = statement.executeQuery("SELECT * FROM TASKS WHERE METHOD_NAME = '" + methodName + "'");
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
        PostgresTaskRepository postgresTaskRepository = new PostgresTaskRepository(postgresConnection);
        postgresTaskRepository.generateTable();
        String methodName = "getTask" + UUID.randomUUID();
        postgresTaskRepository.save(new Task(PostgresTaskRepository.class, methodName, List.of(String.class, Class.class)));
        Optional<Task> getTask = postgresTaskRepository.getTask(methodName, PostgresTaskRepository.class);
        assertTrue(getTask.isPresent());
    }

}
