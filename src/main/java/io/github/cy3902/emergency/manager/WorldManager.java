package io.github.cy3902.emergency.manager;
import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.dao.DayWorldDAO;
import io.github.cy3902.emergency.dao.TimeWorldDAO;
import io.github.cy3902.emergency.world.DayWorld;
import io.github.cy3902.emergency.world.TimeWorld;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 管理所有世界的操作，包括緊急事件的啟動、暫停和停止，以及世界數據的保存和清除。
 */
public class WorldManager {
    private static final Emergency emergency = Emergency.getInstance();

    // 存儲所有世界物件的列表
    private static List<AbstractsWorld> worldList = new ArrayList<>();

    /**
     * 停止所有世界中的所有緊急事件。
     */
    public static void allEmergencyStop() {
        for (AbstractsWorld abstractsWorld : worldList) {
            for (String group : EmergencyManager.getAllGroups()) {
                EmergencyManager.earlyStop(abstractsWorld, group);
            }
        }
    }

    /**
     * 保存所有正在運行的緊急事件的狀態到數據庫。
     */
    public static void allRunningEmergencySave() {
        Emergency.getSql().clearTables("emergency_day_world");
        Emergency.getSql().clearTables("emergency_time_world");
        Emergency.getSql().connect();
        try (Connection conn = Emergency.getSql().getConnection()) {
            conn.setAutoCommit(false);
            for (AbstractsWorld world : worldList) {
                if (world instanceof DayWorld) {
                    DayWorldDAO.saveDayWorld((DayWorld) world, conn);
                } else if (world instanceof TimeWorld) {
                    TimeWorldDAO.saveTimeWorld((TimeWorld) world, conn);
                }
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暫停所有世界中的所有緊急事件。
     */
    public static void allEmergencyPause() {
        for (AbstractsWorld abstractsWorld : worldList) {
            for (String group : EmergencyManager.getAllGroups()) {
                EmergencyManager.emergencyPause(abstractsWorld, group);
            }
        }
    }

    /**
     * 將指定的世界物件添加到管理列表中。
     *
     * @param abstractsWorld 要添加的世界物件
     */
    public void add(AbstractsWorld abstractsWorld) {
        worldList.add(abstractsWorld);
    }

    /**
     * 清除所有已添加的世界物件。
     */
    public void clear() {
        worldList = new ArrayList<>();
    }

    /**
     * 獲取所有世界的名稱列表。
     *
     * @return 世界名稱的列表
     */
    public static List<String> getWorldNameList() {
        Set<String> nameSet = worldList.stream()
                .map(abstractWorld -> abstractWorld.getWorld().getName())
                .collect(Collectors.toSet());
        return new ArrayList<>(nameSet);
    }

    /**
     * 停止所有世界中的世界任務。
     */
    public static void allWorldStop() {
        for (AbstractsWorld world : worldList) {
            if (world instanceof DayWorld) {
                Emergency.getTaskManager().cancelTask("DayChangeChecker-" + world.getWorld().getName());
            }
            if (world instanceof TimeWorld) {
                Emergency.getTaskManager().cancelTask("TimeChangeChecker-" + world.getWorld().getName());
            }
        }
    }

    /**
     * 暫停所有世界中的世界任務。
     */
    public static void allWorldPause() {
        for (AbstractsWorld world : worldList) {
            if (world instanceof DayWorld) {
                Emergency.getTaskManager().pauseTask("DayChangeChecker-" + world.getWorld().getName());
            }
            if (world instanceof TimeWorld) {
                Emergency.getTaskManager().pauseTask("TimeChangeChecker-" + world.getWorld().getName());
            }
        }
    }

    /**
     * 根據指定的組名獲取所有符合條件的世界列表。
     *
     * @param group 組名
     * @return 符合條件的世界列表
     */
    public static List<AbstractsWorld> getAllWorldByGroup(String group) {
        return worldList.stream()
                .filter(abstractsWorld -> abstractsWorld.getGroupStates().containsKey(group))
                .collect(Collectors.toList());
    }

    /**
     * 根據指定的世界名稱獲取所有符合條件的世界列表。
     *
     * @param name 世界名稱
     * @return 符合條件的世界列表
     */
    public static List<AbstractsWorld> getAllWorldByName(String name) {
        List<AbstractsWorld> abstractsWorldList = new ArrayList<>();

        for (AbstractsWorld abstractsWorld : worldList) {
            if (abstractsWorld.getWorld().getName().equals(name)) {
                abstractsWorldList.add(abstractsWorld);
            }
        }
        return abstractsWorldList;
    }

    /**
     * 獲取所有已添加的世界物件列表。
     *
     * @return 世界物件列表
     */
    public static List<AbstractsWorld> getWorldList() {
        return worldList;
    }
}
