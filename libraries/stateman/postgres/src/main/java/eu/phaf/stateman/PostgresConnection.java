package eu.phaf.stateman;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public record PostgresConnection(String jdbcURL, String username, String password) {

    public Connection connect() {
        try {
            return DriverManager.getConnection(jdbcURL, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
