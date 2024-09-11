package io.github.cy3902.emergency.sql;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsSQL;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite extends AbstractsSQL {

    private static String DATABASE_URL;

    public SQLite(String filepath) {
        DATABASE_URL = filepath;
        connect();
        createTableIfNotExists();
    }

    @Override
    public void connect(){
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:"+DATABASE_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createTableIfNotExists() {
        String createShutdownsTableSQL = "CREATE TABLE IF NOT EXISTS emergency_server_shutdowns ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "shutdown_time TEXT NOT NULL"
                + ");";

        String createTimeWorldSQL = "CREATE TABLE IF NOT EXISTS emergency_time_world ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "'group' TEXT NOT NULL, "
                + "'Name' TEXT NOT NULL, "
                + "world TEXT NOT NULL, "
                + "start_time TIMESTAMP NOT NULL"
                + ");";

        String createDayWorldSQL = "CREATE TABLE IF NOT EXISTS emergency_day_world ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "'group' TEXT NOT NULL, "
                + "'Name' TEXT NOT NULL, "
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
        String clearTable = "DELETE FROM `" + table + "`";
        connect();
        try (Connection conn = Emergency.getInstance().getSql().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(clearTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}