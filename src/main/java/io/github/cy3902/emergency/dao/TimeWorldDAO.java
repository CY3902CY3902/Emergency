package io.github.cy3902.emergency.dao;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsSQL;
import io.github.cy3902.emergency.emergency.TimeEmergency;
import io.github.cy3902.emergency.manager.EmergencyManager;
import io.github.cy3902.emergency.utils.Utils;
import io.github.cy3902.emergency.world.TimeWorld;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * TimeWorldDAO 類別負責與資料庫交互，保存和加載 TimeWorld 物件的緊急事件數據。
 * 主要功能包括將 TimeWorld 的緊急事件信息保存到資料庫中，
 * 以及從資料庫中加載這些數據並重新排程事件。
 */
public class TimeWorldDAO {

    private static AbstractsSQL abstractsSQL;

    /**
     * 構造方法，初始化資料庫連接。
     *
     * @param abstractsSQL 資料庫連接實例
     */
    public TimeWorldDAO(AbstractsSQL abstractsSQL) {
        TimeWorldDAO.abstractsSQL = abstractsSQL;
    }

    /**
     * 將 TimeWorld 的緊急事件信息保存到資料庫中。
     * 會批量插入緊急事件的組別、名稱、世界和開始時間。
     *
     * @param timeWorld 包含緊急事件信息的 TimeWorld 物件
     * @param conn      資料庫連接
     * @throws SQLException 當資料庫操作失敗時拋出
     */
    public static void saveTimeWorld(TimeWorld timeWorld, Connection conn) throws SQLException {
        String insertSql = "INSERT INTO emergency_time_world (`group`, name, world, start_time) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            for (String group : timeWorld.getWorldEmergency().keySet()) {
                AbstractsEmergency emergency = timeWorld.getWorldEmergency().get(group);
                insertStmt.setString(1, group);
                insertStmt.setString(2, emergency.getName());
                insertStmt.setString(3, timeWorld.getWorld().getName());
                insertStmt.setTimestamp(4, Timestamp.valueOf(
                        timeWorld.getGroupTimeEnd().get(group).plusSeconds(-1 * emergency.getDuration())));
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        }
    }

    /**
     * 從資料庫中加載緊急事件信息，並重新排程 TimeWorld 的緊急事件。
     * 根據世界名稱查詢事件，並根據之前的關閉時間計算剩餘的持續時間。
     *
     * @param timeWorld 包含需要加載的緊急事件的 TimeWorld 物件
     * @throws SQLException 當資料庫操作失敗時拋出
     */
    public void loadEmergency(TimeWorld timeWorld) throws SQLException {
        String selectSql = "SELECT * FROM emergency_time_world WHERE world = ?";
        Emergency emergency = Emergency.getInstance();
        Emergency.getSql().connect();
        try (Connection conn = Emergency.getSql().getConnection();
             PreparedStatement prepareStatement = conn.prepareStatement(selectSql)) {

            prepareStatement.setString(1, timeWorld.getWorld().getName());
            ResultSet rs = prepareStatement.executeQuery();

            while (rs.next()) {
                String group = rs.getString("group");
                String name = rs.getString("name");
                Timestamp timestamp = rs.getTimestamp("start_time");
                LocalDateTime startTime = timestamp.toLocalDateTime();

                AbstractsEmergency abstractsEmergency = EmergencyManager.findEmergencyByName(name);
                if (abstractsEmergency != null) {
                    long second = abstractsEmergency.getDuration() -
                            Utils.calculateSecondsBetween(startTime, Emergency.getLastShutdownTime());
                    LocalDateTime newTime = LocalDateTime.now().plusSeconds(second);
                    timeWorld.addOrUpdateEvent(group, newTime);
                    timeWorld.getGroupTimeEnd().put(group, newTime);
                    EmergencyManager.earlyStop(timeWorld, group);
                    timeWorld.getWorldEmergency().put(group, abstractsEmergency);
                    ((TimeEmergency) abstractsEmergency).start(timeWorld, group, second);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
