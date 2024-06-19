package eu.phaf.stateman;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgresTaskActionRepository implements TaskActionRepository {

    private final PostgresConnection postgresConnection;

    public PostgresTaskActionRepository(PostgresConnection postgresConnection) {
        this.postgresConnection = postgresConnection;
    }

    public void generateTable() {
        try (Connection connection = postgresConnection.connect()) {
            String generateSql = """
                    CREATE TABLE IF NOT EXISTS TASK_ACTIONS(
                    THE_CLASS VARCHAR(255),
                    METHOD_NAME VARCHAR(255),
                    PARAMETER_VALUES JSONB,
                    EVENT_DATE_TIME TIMESTAMP,
                    EVENT_TYPE VARCHAR(255),
                    PRIMARY KEY(THE_CLASS, METHOD_NAME, PARAMETER_VALUES, EVENT_DATE_TIME)
                    );
                    """;
            Statement statement = connection.createStatement();
            statement.execute(generateSql);
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    @Override
    public void save(TaskAction taskAction) {
        try (Connection connection = postgresConnection.connect()) {
            Task task = taskAction.task();
            String sql = "INSERT INTO TASK_ACTIONS\n" +
                    "(THE_CLASS, METHOD_NAME, PARAMETER_VALUES, EVENT_DATE_TIME, EVENT_TYPE)\n" +
                    StringFormatter.format("VALUES ('{}','{}','{}','{}','{}')\n",
                            task.theClass().getName(),
                            task.methodName(),
                            JsonDeserializer.deserialize(taskAction.parameterValues()),
                            taskAction.offsetDateTime(),
                            taskAction.taskType());
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
