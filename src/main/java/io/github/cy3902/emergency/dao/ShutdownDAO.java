package io.github.cy3902.emergency.dao;

import io.github.cy3902.emergency.abstracts.AbstractsSQL;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ShutdownDAO {
    private static AbstractsSQL abstractsSQL;

    public ShutdownDAO(AbstractsSQL abstractsSQL) {
        this.abstractsSQL = abstractsSQL;
    }

    public void saveShutdownTime(LocalDateTime shutdownTime) {
        abstractsSQL.connect();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = shutdownTime.format(formatter);

        try (Connection conn = abstractsSQL.getConnection();
             Statement stmt = conn.createStatement()) {

            String deleteSql = "DELETE FROM server_shutdowns";
            stmt.executeUpdate(deleteSql);

            String insertSql = "INSERT INTO server_shutdowns (shutdown_time) VALUES (?)";
            try (PreparedStatement insertStatement = conn.prepareStatement(insertSql)) {
                insertStatement.setString(1, formattedTime);
                insertStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public LocalDateTime readLastShutdownTime() {
        abstractsSQL.connect();
        LocalDateTime lastShutdownTime = null;
        String sql = "SELECT shutdown_time FROM server_shutdowns ORDER BY id DESC LIMIT 1";
        try (PreparedStatement preparedStatement = this.abstractsSQL.getConnection().prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                String shutdownTimeStr = resultSet.getString("shutdown_time");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                lastShutdownTime = LocalDateTime.parse(shutdownTimeStr, formatter);
            } else {
                lastShutdownTime = LocalDateTime.now();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lastShutdownTime;
    }
}
