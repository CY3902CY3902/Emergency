package io.github.cy3902.emergency.manager;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.logging.Level;

public class EmergencyManager {
    private static final Emergency emergency = Emergency.getInstance();
    // 所有緊急事件物件
    private static List<AbstractsEmergency> emergencyList = new ArrayList<>();

    // 群組分類後的緊急事件物件
    private static Multimap<String, AbstractsEmergency> emergencyGroupMap = ArrayListMultimap.create();

    /**
     * 查找並移除指定世界和群組的緊急事件。
     *
     * @param abstractsWorld 世界物件
     * @param group 群組名稱
     * @return 被找到的緊急事件，若找不到則為 null
     */
    public static AbstractsEmergency findAndRemoveTask(AbstractsWorld abstractsWorld, String group) {
        Collection<AbstractsEmergency> emergencies = emergencyGroupMap.get(group);
        String taskId = "task-" + group + "-" + abstractsWorld.getWorld().getName();
        AbstractsEmergency abstractsEmergency = null;

        // 搜尋事件
        for (AbstractsEmergency emergency : emergencies) {
            if (new ArrayList<>(abstractsWorld.getWorldEmergency().values()).contains(emergency)) {
                abstractsEmergency = emergency;
            }
        }
        if (abstractsEmergency == null) {
            return null;
        }

        Map<String, BukkitTask> tasks = abstractsEmergency.getActiveTasks();
        BukkitTask task = tasks.get(taskId);
        if (task != null) {
            tasks.remove(taskId);
            Emergency.getTaskManager().pauseTask(taskId);
            Emergency.getTaskManager().cancelTask(taskId);
            return abstractsEmergency;
        }
        return null;
    }

    /**
     * 提前停止指定世界和群組的緊急事件。
     *
     * @param abstractsWorld 世界物件
     * @param group 群組名稱
     */
    public static void earlyStop(AbstractsWorld abstractsWorld, String group) {
        AbstractsEmergency abstractsEmergency = findAndRemoveTask(abstractsWorld, group);
        if (abstractsEmergency != null) {
            abstractsEmergency.stop(abstractsWorld, group);
        }
    }

    /**
     * 暫停指定世界和群組的緊急事件。
     *
     * @param abstractsWorld 世界物件
     * @param group 群組名稱
     */
    public static void emergencyPause(AbstractsWorld abstractsWorld, String group) {
        AbstractsEmergency abstractsEmergency = findAndRemoveTask(abstractsWorld, group);
        if (abstractsEmergency != null) {
            abstractsEmergency.pause(abstractsWorld, group);
        }
    }

    /**
     * 獲取所有群組的名稱集合。
     *
     * @return 群組名稱的集合
     */
    public static Set<String> getAllGroups() {
        return emergencyGroupMap.keySet();
    }

    /**
     * 根據群組名稱獲取所有對應的緊急事件。
     *
     * @param group 群組名稱
     * @return 該群組下的緊急事件列表
     */
    public static List<AbstractsEmergency> getAllEmergencyByGroup(String group) {
        return new ArrayList<>(emergencyGroupMap.get(group));
    }

    /**
     * 根據名稱查找緊急事件。
     *
     * @param name 緊急事件名稱
     * @return 找到的緊急事件，若找不到則為 null
     */
    public static AbstractsEmergency findEmergencyByName(String name) {
        return emergencyList.stream()
                .filter(emergency -> name.equals(emergency.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 檢查是否有重複的群組名稱。
     *
     * @param abstractsEmergency 緊急事件物件
     * @return 重複群組名稱的列表
     */
    public static ArrayList<String> checkForDuplicateGroup(AbstractsEmergency abstractsEmergency) {
        List<String> group = abstractsEmergency.getGroup();
        ArrayList<String> result = new ArrayList<>();
        for (String g : group) {
            if (emergencyGroupMap.containsKey(g)) {
                AbstractsEmergency abstractsEmergencyFirst = emergencyGroupMap.get(g).stream().findFirst().get();
                if (abstractsEmergencyFirst.getClass() != abstractsEmergency.getClass()) {
                    result.add(g);
                }
            }
        }
        return result;
    }

    /**
     * 添加一個緊急事件。
     *
     * @param abstractsEmergency 緊急事件物件
     */
    public void add(AbstractsEmergency abstractsEmergency) {
        ArrayList<String> checkForDuplicateGroup = checkForDuplicateGroup(abstractsEmergency);
        if (!checkForDuplicateGroup.isEmpty()) {
            emergency.info(Emergency.getLang().duplicateGroupName + checkForDuplicateGroup, Level.SEVERE);
            return;
        }
        if (findEmergencyByName(abstractsEmergency.getName()) != null) {
            emergency.info(Emergency.getLang().duplicateEmergencyMessage + abstractsEmergency.getName(), Level.SEVERE);
            return;
        }
        emergencyList.add(abstractsEmergency);
        for (String group : abstractsEmergency.getGroup()) {
            emergencyGroupMap.put(group, abstractsEmergency);
        }
    }

    /**
     * 移除一個緊急事件。
     *
     * @param abstractsEmergency 緊急事件物件
     */
    public void remove(AbstractsEmergency abstractsEmergency) {
        emergencyList.remove(abstractsEmergency);

        for (String group : abstractsEmergency.getGroup()) {
            Collection<AbstractsEmergency> emergencies = emergencyGroupMap.get(group);
            if (emergencies != null) {
                emergencies.remove(abstractsEmergency);
                if (emergencies.isEmpty()) {
                    emergencyGroupMap.removeAll(group);
                }
            }
        }
    }

    /**
     * 清空所有緊急事件。
     */
    public void clear() {
        emergencyList = new ArrayList<>();
        emergencyGroupMap = ArrayListMultimap.create();
    }
}
