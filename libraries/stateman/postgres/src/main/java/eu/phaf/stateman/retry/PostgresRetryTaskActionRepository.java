package eu.phaf.stateman.retry;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.phaf.stateman.JsonDeserializer;
import eu.phaf.stateman.PostgresConnection;
import eu.phaf.stateman.StringFormatter;
import eu.phaf.stateman.Task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PostgresRetryTaskActionRepository implements RetryTaskActionRepository {

    private final PostgresConnection postgresConnection;

    public PostgresRetryTaskActionRepository(PostgresConnection postgresConnection) {
        this.postgresConnection = postgresConnection;
    }

    public void generateTable() {
        try (Connection connection = postgresConnection.connect()) {
            String generateSql = """                                      
                    CREATE TABLE IF NOT EXISTS RETRY_TASK_ACTIONS(
                    THE_CLASS VARCHAR(255),
                    RETRY_METHOD_NAME VARCHAR(255),
                    PARAMETERS JSONB,
                    PARAMETER_VALUES JSONB,
                    EVENT_DATE_TIME TIMESTAMP with time zone,
                    FIRST_RETRY_TIME TIMESTAMP with time zone,
                    RETRY_TIMES TIMESTAMP with time zone[],
                    PRIMARY KEY(THE_CLASS, RETRY_METHOD_NAME, PARAMETER_VALUES, EVENT_DATE_TIME)
                    );
                    CREATE INDEX IF NOT EXISTS IDX_FIRST_RETRY_TIME ON RETRY_TASK_ACTIONS (THE_CLASS, RETRY_METHOD_NAME, PARAMETERS, FIRST_RETRY_TIME);
                    """;
            Statement statement = connection.createStatement();
            statement.execute(generateSql);
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    @Override
    public void save(RetryTaskAction retryTaskAction) {
        try (Connection connection = postgresConnection.connect()) {
            var task = retryTaskAction.task();
            var times = retryTaskAction.offsetDateTimes().stream()
                    .map(OffsetDateTime::toString)
                    .collect(Collectors.joining(",", "{", "}"));
            String sql = "INSERT INTO RETRY_TASK_ACTIONS\n" +
                         """
                                 (
                                 THE_CLASS,
                                 RETRY_METHOD_NAME,
                                 PARAMETERS,
                                 PARAMETER_VALUES,
                                 EVENT_DATE_TIME,
                                 FIRST_RETRY_TIME,
                                 RETRY_TIMES
                                 )
                                         """
                         +
                         StringFormatter.format("VALUES ('{}','{}','{}', '{}', '{}', '{}', '{}')\n",
                                 task.theClass().getName(),
                                 retryTaskAction.retryMethod(),
                                 JsonDeserializer.deserialize(task.parameters()),
                                 JsonDeserializer.deserialize(retryTaskAction.parameterValues()),
                                 retryTaskAction.originalEventTime(),
                                 retryTaskAction.getFirstOffsetDateTime(),
                                 times
                         );
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(RetryTaskAction retryTaskAction) {
        try (Connection connection = postgresConnection.connect()) {
            String sql = "DELETE FROM RETRY_TASK_ACTIONS\n" +
                         StringFormatter.format(
                                 "WHERE RETRY_METHOD_NAME = '{}' AND THE_CLASS = '{}' AND PARAMETER_VALUES = '{}' AND EVENT_DATE_TIME = '{}'; \n",
                                 retryTaskAction.retryMethod(),
                                 retryTaskAction.task().theClass().getName(),
                                 JsonDeserializer.deserialize(retryTaskAction.parameterValues()),
                                 retryTaskAction.originalEventTime());
            Statement statement = connection.createStatement();
            int result = statement.executeUpdate(sql);
            if (result == 0) {
                throw new RuntimeException("Could not delete retry task action");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<RetryTaskAction> getAndRemoveFirstRetryTaskAction(Task task, String retryMethod, OffsetDateTime now) {
        Optional<RetryTaskAction> firstRetryTaskAction = getFirstRetryTaskAction(task, retryMethod, now);
        firstRetryTaskAction.ifPresent(this::remove);
        return firstRetryTaskAction;
    }

    @Override
    public Optional<RetryTaskAction> getFirstRetryTaskAction(Task task, String retryMethod, OffsetDateTime now) {
        try (Connection connection = postgresConnection.connect()) {
            String sql = "SELECT\n" +
                         """
                                 PARAMETER_VALUES,
                                 EVENT_DATE_TIME,
                                 RETRY_TIMES
                                                             
                                 FROM RETRY_TASK_ACTIONS
                                 """
                         +
                         StringFormatter.format(
                                 "WHERE RETRY_METHOD_NAME = '{}' AND THE_CLASS = '{}' AND PARAMETERS = '{}' AND FIRST_RETRY_TIME < '{}' \n",
                                 retryMethod,
                                 task.theClass().getName(),
                                 JsonDeserializer.deserialize(task.parameters()),
                                 now);
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                var originalEventTime = OffsetDateTime.parse(resultSet.getString("EVENT_DATE_TIME").replace(" ", "T"));
                var arrayResult = resultSet.getArray("RETRY_TIMES").getResultSet();
                List<OffsetDateTime> offsetDateTimes = new ArrayList<>(arrayResult.getFetchSize());
                while (arrayResult.next()) {
                    offsetDateTimes.add(OffsetDateTime.parse(arrayResult.getString(2).replace(' ', 'T')));
                }
                return Optional.of(new RetryTaskAction(
                        task,
                        retryMethod,
                        JsonDeserializer.serialize(resultSet.getString("PARAMETER_VALUES"),
                                new TypeReference<>() {
                                }),
                        originalEventTime,
                        offsetDateTimes));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public int count(Class<?> theClass) {
        try (Connection connection = postgresConnection.connect()) {
            String sql = "SELECT\n" +
                         """
                                 *
                                 FROM RETRY_TASK_ACTIONS
                                 """
                         +
                         StringFormatter.format(
                                 "WHERE THE_CLASS = '{}'; \n",
                                 theClass);
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(sql);
            return resultSet.getFetchSize();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
