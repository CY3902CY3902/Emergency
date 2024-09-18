package io.github.cy3902.emergency.dao;

import io.github.cy3902.emergency.abstracts.AbstractsSQL;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ShutdownDAO 類別負責處理伺服器關機時間的資料庫操作。
 * 提供保存伺服器關機時間並讀取最後一次關機時間的功能。
 */
public class ShutdownDAO {

    // 用於資料庫連線的抽象 SQL 類別
    private static AbstractsSQL abstractsSQL;

    /**
     * 建構子，使用指定的 abstractsSQL 物件進行初始化。
     *
     * @param abstractsSQL 資料庫操作的抽象 SQL 類別
     */
    public ShutdownDAO(AbstractsSQL abstractsSQL) {
        ShutdownDAO.abstractsSQL = abstractsSQL;
    }

    /**
     * 保存伺服器的關機時間到資料庫中。
     * 先刪除 emergency_server_shutdowns 表中的所有舊資料，然後插入新的關機時間。
     *
     * @param shutdownTime 伺服器的關機時間，格式為 LocalDateTime
     */
    public void saveShutdownTime(LocalDateTime shutdownTime) {
        abstractsSQL.connect();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = shutdownTime.format(formatter);

        try (Connection conn = abstractsSQL.getConnection();
             Statement stmt = conn.createStatement()) {

            // 刪除 emergency_server_shutdowns 資料表中的所有記錄
            String deleteSql = "DELETE FROM emergency_server_shutdowns";
            stmt.executeUpdate(deleteSql);

            // 插入新的關機時間
            String insertSql = "INSERT INTO emergency_server_shutdowns (shutdown_time) VALUES (?)";
            try (PreparedStatement insertStatement = conn.prepareStatement(insertSql)) {
                insertStatement.setString(1, formattedTime);
                insertStatement.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 從資料庫中讀取最後一次伺服器的關機時間。
     * 如果資料表中無記錄，則返回當前時間。
     *
     * @return LocalDateTime 最後一次伺服器的關機時間，如果無記錄則返回當前時間
     */
    public LocalDateTime readLastShutdownTime() {
        abstractsSQL.connect();
        LocalDateTime lastShutdownTime = null;
        String sql = "SELECT shutdown_time FROM emergency_server_shutdowns ORDER BY id DESC LIMIT 1";
        try (PreparedStatement preparedStatement = abstractsSQL.getConnection().prepareStatement(sql);
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
