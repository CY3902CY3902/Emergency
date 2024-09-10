package io.github.cy3902.emergency.sql;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsSQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL extends AbstractsSQL {

    private static String databaseUrl;
    private static String username;
    private static String password;

    // 使用參數的構造函數
    public MySQL(String host, int port, String dbName, String username, String password) {
        this.databaseUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
        this.username = username;
        this.password = password;
        connect();
        createTableIfNotExists();
    }

    @Override
    public void connect() {
        try {
            this.connection = DriverManager.getConnection(databaseUrl, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createTableIfNotExists() {
        String createShutdownsTableSQL = "CREATE TABLE IF NOT EXISTS server_shutdowns ("
                + "id INTEGER PRIMARY KEY AUTO_INCREMENT, "
                + "shutdown_time TEXT NOT NULL"
                + ");";

        String createTimeWorldSQL = "CREATE TABLE IF NOT EXISTS time_world ("
                + "id INTEGER PRIMARY KEY AUTO_INCREMENT, "
                + "`group` TEXT NOT NULL, "
                + "`name` TEXT NOT NULL, "
                + "world TEXT NOT NULL, "
                + "start_time TIMESTAMP NOT NULL"
                + ");";

        String createDayWorldSQL = "CREATE TABLE IF NOT EXISTS day_world ("
                + "id INTEGER PRIMARY KEY AUTO_INCREMENT, "
                + "`group` TEXT NOT NULL, "
                + "`name` TEXT NOT NULL, "
                + "world TEXT NOT NULL, "
                + "day INT NOT NULL"
                + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createShutdownsTableSQL);
            stmt.execute(createTimeWorldSQL);
            stmt.execute(createDayWorldSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 檢查連接狀態
    @Override
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

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void clearTables(String table) {
        String clearTable = "TRUNCATE TABLE `" + table + "`";
        connect();
        try (Connection conn = Emergency.getInstance().getSql().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(clearTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
