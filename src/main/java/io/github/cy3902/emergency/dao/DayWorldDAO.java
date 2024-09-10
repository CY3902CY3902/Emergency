package io.github.cy3902.emergency.dao;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsSQL;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.emergency.DayEmergency;
import io.github.cy3902.emergency.world.DayWorld;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public class DayWorldDAO {
    private static AbstractsSQL abstractsSQL;
    public DayWorldDAO(AbstractsSQL abstractsSQL) {
        this.abstractsSQL = abstractsSQL;
    }

    public static void saveDayWorld(DayWorld dayWorld, Connection conn) throws SQLException {
        String insertSql = "INSERT INTO day_world (`group`, name, world, day) VALUES (?, ?, ?, ?)";
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

    public void loadEmergency(DayWorld dayWorld) throws SQLException {
        String selectSql = "SELECT * FROM day_world WHERE world = ?";
        Emergency emergency = Emergency.getInstance();
        try (Connection conn = abstractsSQL.getConnection();
             PreparedStatement pstmtSelect = conn.prepareStatement(selectSql);) {

            pstmtSelect.setString(1, dayWorld.getWorld().getName());
            ResultSet rs = pstmtSelect.executeQuery();

            // 處理結果集
            while (rs.next()) {
                String group = rs.getString("group");
                String name = rs.getString("name");
                int dayRemaining = rs.getInt("day");

                AbstractsEmergency abstractsEmergency = emergency.getEmergencyManager().findEmergencyByName(name);
                if (abstractsEmergency != null) {
                    dayWorld.addOrUpdateEvent(group, dayWorld.getDay() + dayRemaining);
                    emergency.getEmergencyManager().earlyStop(dayWorld, group);
                    dayWorld.getGroupDayEnd().put(group, dayWorld.getDay() + dayRemaining);
                    dayWorld.getWorldEmergency().put(group,abstractsEmergency);
                    DayEmergency dayEmergency = (DayEmergency) abstractsEmergency;
                    dayEmergency.start(dayWorld, group);
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
