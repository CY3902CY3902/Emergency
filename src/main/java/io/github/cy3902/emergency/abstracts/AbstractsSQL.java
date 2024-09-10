package io.github.cy3902.emergency.abstracts;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractsSQL {
    protected Connection connection;


    public abstract void connect();

    public abstract void createTableIfNotExists();

    public boolean isConnectionValid() {
        try {
            if (connection == null || connection.isClosed()) {
                return false;
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Connection getConnection() {
        return connection;
    }


    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void clearTables(String table) {
    }
}
