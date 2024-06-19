package eu.phaf.stateman;

import com.fasterxml.jackson.core.type.TypeReference;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class PostgresTaskRepository implements TaskRepository {

    private final PostgresConnection postgresConnection;

    public PostgresTaskRepository(PostgresConnection postgresConnection) {
        this.postgresConnection = postgresConnection;
    }

    public void generateTable() {
        try (Connection connection = postgresConnection.connect()) {
            String generateSql = """
                    CREATE TABLE IF NOT EXISTS TASKS(
                    THE_CLASS VARCHAR(255),
                    METHOD_NAME VARCHAR(255),
                    PARAMETERS JSONB,
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
    public void save(Task task) {
        try (Connection connection = postgresConnection.connect()) {
            String sql = "INSERT INTO TASKS\n" +
                    "(THE_CLASS, METHOD_NAME, PARAMETERS)\n" +
                    StringFormatter.format("VALUES ('{}','{}','{}')\n", task.theClass().getName(), task.methodName(), JsonDeserializer.deserialize(task.parameters()));
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Task> getTask(String methodName, Class<?> theClass) {
        try (Connection connection = postgresConnection.connect()) {
            String sql = "SELECT THE_CLASS, METHOD_NAME, PARAMETERS FROM TASKS\n" +
                    StringFormatter.format("WHERE METHOD_NAME = '{}' AND THE_CLASS = '{}' \n", methodName, theClass.getName());
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String parameters = resultSet.getString("PARAMETERS");
                Class<?> theClasss = Class.forName(resultSet.getString("THE_CLASS"));
                return Optional.of(new Task(theClasss, methodName, JsonDeserializer.serialize(parameters, new TypeReference<>() {
                })));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
}
