package eu.phaf.stateman.retry;

import eu.phaf.stateman.JsonDeserializer;
import eu.phaf.stateman.PostgresConnection;
import eu.phaf.stateman.StringFormatter;
import eu.phaf.stateman.Task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Optional;

public class PostgresRetryTaskRepository implements RetryTaskRepository {

    private final PostgresConnection postgresConnection;

    public PostgresRetryTaskRepository(PostgresConnection postgresConnection) {
        this.postgresConnection = postgresConnection;
    }

    public void generateTable() {
        try (Connection connection = postgresConnection.connect()) {
            String generateSql = """
                    CREATE TABLE IF NOT EXISTS RETRY_TASKS(
                    THE_CLASS VARCHAR(255),
                    METHOD_NAME VARCHAR(255),
                    PARAMETERS JSONB,
                    RETRY_AFTER VARCHAR(255),
                    MAX_ATTEMPTS INTEGER,
                    RETRY_METHOD VARCHAR(255),
                    PRIMARY KEY(THE_CLASS, METHOD_NAME, PARAMETERS)
                    );
                    """;
            Statement statement = connection.createStatement();
            statement.execute(generateSql);
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    @Override
    public void save(RetryTask retryTask) {
        try (Connection connection = postgresConnection.connect()) {
            var task = retryTask.task();
            String sql = "INSERT INTO RETRY_TASKS\n" +
                         """
                                 (
                                 THE_CLASS,
                                  METHOD_NAME,
                                  PARAMETERS,
                                  RETRY_AFTER,
                                  MAX_ATTEMPTS,
                                  RETRY_METHOD
                                  )
                                 """
                         +
                         StringFormatter.format("VALUES ('{}','{}','{}', '{}', {}, '{}')\n",
                                 task.theClass().getName(),
                                 task.methodName(),
                                 JsonDeserializer.deserialize(task.parameters()),
                                 retryTask.retryAfter(),
                                 retryTask.maxAttempts(),
                                 retryTask.retryMethod()
                         );
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<RetryTask> getByTask(Task task) {
        try (Connection connection = postgresConnection.connect()) {
            String sql = "SELECT\n" +
                         """
                                 RETRY_AFTER,
                                 MAX_ATTEMPTS,
                                 RETRY_METHOD
                                                             
                                 FROM RETRY_TASKS
                                 """
                         +
                         StringFormatter.format("WHERE METHOD_NAME = '{}' AND THE_CLASS = '{}' AND PARAMETERS = '{}' \n",
                                 task.methodName(),
                                 JsonDeserializer.deserialize(task.parameters()),
                                 task.theClass().getName());
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                var duration = Duration.parse(resultSet.getString("RETRY_AFTER"));
                var maxAttempts = resultSet.getInt("MAX_ATTEMPTS");
                var retryMethod = resultSet.getString("RETRY_METHOD");
                return Optional.of(new RetryTask(task, duration, maxAttempts, retryMethod));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}
