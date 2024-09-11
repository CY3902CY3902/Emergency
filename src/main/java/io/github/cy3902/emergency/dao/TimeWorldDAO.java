package io.github.cy3902.emergency.dao;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsSQL;
import io.github.cy3902.emergency.emergency.TimeEmergency;
import io.github.cy3902.emergency.utils.Utils;
import io.github.cy3902.emergency.world.DayWorld;
import io.github.cy3902.emergency.world.TimeWorld;

import java.sql.*;
import java.time.LocalDateTime;

public class TimeWorldDAO {
    private static AbstractsSQL abstractsSQL;
    public TimeWorldDAO(AbstractsSQL abstractsSQL) {
        this.abstractsSQL = abstractsSQL;
    }

    public static void saveTimeWorld(TimeWorld timeWorld, Connection conn) throws SQLException {
        String insertSql = "INSERT INTO emergency_time_world (`group`, name, world, start_time) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            for (String group : timeWorld.getWorldEmergency().keySet()) {
                AbstractsEmergency emergency = timeWorld.getWorldEmergency().get(group);
                insertStmt.setString(1, group);
                insertStmt.setString(2, emergency.getName());
                insertStmt.setString(3, timeWorld.getWorld().getName());
                insertStmt.setTimestamp(4, Timestamp.valueOf(timeWorld.getGroupTimeEnd().get(group).plusSeconds(-1*emergency.getDuration())));
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        }
    }


    public void loadEmergency(TimeWorld timeWorld) throws SQLException {
        String selectSql = "SELECT * FROM emergency_time_world WHERE world = ?";
        Emergency emergency = Emergency.getInstance();
        emergency.getSql().connect();
        try (Connection conn = emergency.getSql().getConnection();
             PreparedStatement prepareStatement = conn.prepareStatement(selectSql);) {

            prepareStatement.setString(1, timeWorld.getWorld().getName());
            ResultSet rs = prepareStatement.executeQuery();

            while (rs.next()) {
                String group = rs.getString("group");
                String name = rs.getString("name");
                Timestamp timestamp = rs.getTimestamp("start_time");
                LocalDateTime startTime = timestamp.toLocalDateTime();

                AbstractsEmergency abstractsEmergency = emergency.getEmergencyManager().findEmergencyByName(name);
                if (abstractsEmergency != null) {
                    LocalDateTime now = LocalDateTime.now();
                    TimeEmergency timeEmergency = (TimeEmergency) abstractsEmergency;
                    long second = abstractsEmergency.getDuration() - Utils.calculateSecondsBetween(startTime,emergency.getLastShutdownTime());
                    LocalDateTime newTime = now.plusSeconds(second);
                    timeWorld.addOrUpdateEvent(group, newTime);
                    timeWorld.getGroupTimeEnd().put(group, newTime);
                    emergency.getEmergencyManager().earlyStop(timeWorld, group);
                    timeWorld.getWorldEmergency().put(group,abstractsEmergency);
                    timeEmergency.start(timeWorld, group, second);
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
