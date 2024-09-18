package io.github.cy3902.emergency.dao;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsSQL;
import io.github.cy3902.emergency.emergency.DayEmergency;
import io.github.cy3902.emergency.world.DayWorld;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * DayWorldDAO 負責處理與 DayWorld 相關的資料庫操作。
 * 這個類別提供將 DayWorld 資料保存到資料庫以及從資料庫中載入緊急事件的功能。
 */
public class DayWorldDAO {

    // 用於資料庫連線的抽象 SQL 類別
    private static AbstractsSQL abstractsSQL;

    /**
     * 建構子，使用指定的 abstractsSQL 物件進行初始化。
     *
     * @param abstractsSQL 資料庫連線操作的 SQL 類別
     */
    public DayWorldDAO(AbstractsSQL abstractsSQL) {
        this.abstractsSQL = abstractsSQL;
    }

    /**
     * 將 DayWorld 物件的資料保存到資料庫。
     * 該方法將每個緊急事件及其剩餘天數插入到 emergency_day_world 資料表中。
     *
     * @param dayWorld 包含緊急事件和剩餘天數的 DayWorld 物件
     * @param conn 資料庫連線物件，用於執行 SQL 語句
     * @throws SQLException 如果資料庫操作過程中發生錯誤，則拋出該異常
     */
    public static void saveDayWorld(DayWorld dayWorld, Connection conn) throws SQLException {
        // SQL 插入語句，插入群組名稱、事件名稱、世界名稱及剩餘天數
        String insertSql = "INSERT INTO emergency_day_world (`group`, name, world, day) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            for (String group : dayWorld.getWorldEmergency().keySet()) {
                AbstractsEmergency emergency = dayWorld.getWorldEmergency().get(group);
                int daysRemaining = dayWorld.getGroupDayEnd().get(group) - dayWorld.getDay();

                insertStmt.setString(1, group);
                insertStmt.setString(2, emergency.getName());
                insertStmt.setString(3, dayWorld.getWorld().getName());
                insertStmt.setInt(4, daysRemaining);

                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        }
    }

    /**
     * 從資料庫載入指定 DayWorld 的緊急事件。
     * 該方法從 emergency_day_world 資料表中取得資料，並更新 DayWorld 物件的緊急事件資訊。
     *
     * @param dayWorld 要載入緊急事件的 DayWorld 物件
     * @throws SQLException 如果資料庫查詢過程中發生錯誤，則拋出該異常
     */
    public void loadEmergency(DayWorld dayWorld) throws SQLException {
        String selectSql = "SELECT * FROM emergency_day_world WHERE world = ?";
        Emergency emergency = Emergency.getInstance();
        try (Connection conn = abstractsSQL.getConnection();
             PreparedStatement pstmtSelect = conn.prepareStatement(selectSql);) {

            pstmtSelect.setString(1, dayWorld.getWorld().getName());
            ResultSet rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                String group = rs.getString("group");
                String name = rs.getString("name");
                int dayRemaining = rs.getInt("day");

                AbstractsEmergency abstractsEmergency = emergency.getEmergencyManager().findEmergencyByName(name);
                if (abstractsEmergency != null) {
                    dayWorld.addOrUpdateEvent(group, dayWorld.getDay() + dayRemaining);
                    emergency.getEmergencyManager().earlyStop(dayWorld, group);
                    dayWorld.getGroupDayEnd().put(group, dayWorld.getDay() + dayRemaining);
                    dayWorld.getWorldEmergency().put(group, abstractsEmergency);

                    DayEmergency dayEmergency = (DayEmergency) abstractsEmergency;
                    dayEmergency.start(dayWorld, group);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
