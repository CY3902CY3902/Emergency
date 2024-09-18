package io.github.cy3902.emergency.world;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.manager.EmergencyManager;
import io.github.cy3902.emergency.manager.TaskManager;

import java.sql.*;
import java.util.*;

/**
 * 處理以日為單位的世界緊急事件。
 */
public class DayWorld extends AbstractsWorld {
    private long lastTime;

    // 儲存事件的優先隊列
    private final PriorityQueue<Map.Entry<String, Integer>> eventQueue;
    private final Map<String, Integer> groupDayEnd = new HashMap<>();
    private final Map<String, Integer> groupDayStop = new HashMap<>();
    private final Emergency emergency = Emergency.getInstance();

    /**
     * 建構子，初始化世界和事件列表。
     *
     * @param worldName 世界名稱
     * @param dayGroup  事件群組列表
     */
    public DayWorld(String worldName, List<String> dayGroup) {
        super(worldName);
        this.lastTime = this.world.getTime();
        this.eventQueue = new PriorityQueue<>(Map.Entry.comparingByValue());
        loadEmergencySafely();
        for (String g : dayGroup) {
            if (!groupDayEnd.containsKey(g)) {
                groupDayEnd.put(g, this.day);
                groupStates.put(g, TaskManager.TaskStatus.RUNNING);
                eventQueue.add(new AbstractMap.SimpleEntry<>(g, this.day));
            }
        }
        startDayChangeChecker();
    }

    /**
     * 啟動檢查時間變化的定時器。
     */
    private void startDayChangeChecker() {
        String taskId = "DayChangeChecker-" + this.world.getName();
        Emergency.getTaskManager().startPeriodicTask(taskId, this::checkDayChange, 0L, 5L);
    }

    /**
     * 檢查時間變化，並在必要時觸發隨機緊急事件。
     */
    private void checkDayChange() {
        if (this.world == null) return;

        long currentTime = this.world.getTime();

        if (currentTime < this.lastTime) {
            day++;
            randomEmergency();
        }

        this.lastTime = currentTime;
    }

    /**
     * 安全地加載緊急事件。
     */
    private void loadEmergencySafely() {
        try {
            Emergency.getDayWorldDAO().loadEmergency(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 隨機群組的緊急事件
     */
    @Override
    public void randomEmergency() {
        Map.Entry<String, Integer> entry = eventQueue.peek();
        String g = entry.getKey();
        if (groupStates.get(g) != TaskManager.TaskStatus.PAUSED && day >= groupDayEnd.get(g)) {
            AbstractsEmergency abstractsEmergency = randomGroupEmergency(g);
            if (abstractsEmergency != null) {
                startEmergency(g, abstractsEmergency);
            }
        }
    }

    /**
     * 啟動指定的緊急事件。
     *
     * @param group             事件群組
     * @param abstractsEmergency 緊急事件
     */
    public void startEmergency(String group, AbstractsEmergency abstractsEmergency) {
        if (!EmergencyManager.getAllEmergencyByGroup(group).contains(abstractsEmergency)) {
            return;
        }
        addOrUpdateEvent(group, this.day + abstractsEmergency.getDays());
        EmergencyManager.earlyStop(this, group);
        groupDayEnd.put(group, this.day + abstractsEmergency.getDays());
        this.worldEmergency.put(group, abstractsEmergency);
        abstractsEmergency.start(this, group);
    }

    /**
     * 隨機選擇群組中的緊急事件。
     *
     * @param g 群組名稱
     * @return 隨機選擇的緊急事件
     */
    private AbstractsEmergency randomGroupEmergency(String g) {
        List<AbstractsEmergency> emergencies = EmergencyManager.getAllEmergencyByGroup(g);

        if (emergencies == null || emergencies.isEmpty()) {
            return null;
        }

        double totalChance = emergencies.stream()
                .mapToDouble(AbstractsEmergency::getChance)
                .sum();

        if (totalChance <= 0) {
            return null; // 沒有機會，返回 null
        }

        Random random = new Random();
        double randomValue = random.nextDouble() * totalChance; // 在 0 和 totalChance 之間的隨機值

        double cumulative = 0.0;
        for (AbstractsEmergency e : emergencies) {
            cumulative += e.getChance();
            if (randomValue <= cumulative) {
                return e;
            }
        }
        return null;
    }

    /**
     * 添加或更新事件。
     *
     * @param key 事件群組
     * @param day 事件結束日期
     */
    public void addOrUpdateEvent(String key, Integer day) {
        if (groupDayEnd.containsKey(key)) {
            Map.Entry<String, Integer> oldEvent = new AbstractMap.SimpleEntry<>(key, groupDayEnd.get(key));
            eventQueue.remove(oldEvent);
        }
        Map.Entry<String, Integer> newEvent = new AbstractMap.SimpleEntry<>(key, day);
        eventQueue.add(newEvent);
        groupDayEnd.put(key, day);
        groupStates.put(key, TaskManager.TaskStatus.RUNNING);
    }

    /**
     * 獲取群組狀態。
     *
     * @return 群組狀態映射
     */
    public Map<String, TaskManager.TaskStatus> getGroupStates() {
        return groupStates;
    }

    /**
     * 暫停指定群組的緊急事件。
     *
     * @param group 群組名稱
     */
    public void pause(String group) {
        if (!groupDayEnd.containsKey(group)) {
            return;
        }
        this.groupStates.put(group, TaskManager.TaskStatus.PAUSED);
        this.groupDayStop.put(group, this.day);
        HashSet<AbstractsEmergency> emergencies = new HashSet<>(this.getWorldEmergency().values());
        for (AbstractsEmergency emergency : emergencies) {
            if (emergency.getGroup().contains(group)) {
                emergency.pause(this, group);
            }
        }
    }

    /**
     * 恢復指定群組的緊急事件。
     *
     * @param group 群組名稱
     */
    public void resume(String group) {
        if (!groupDayStop.containsKey(group)) {
            return;
        }
        this.groupStates.put(group, TaskManager.TaskStatus.RUNNING);
        int stopDay = this.groupDayStop.get(group);
        int endDay = this.groupDayEnd.get(group);
        int betweenDay = endDay - stopDay;
        int groupDayEnd = this.day + betweenDay;
        addOrUpdateEvent(group, groupDayEnd);
        List<AbstractsEmergency> emergencies = new ArrayList<>(worldEmergency.values());
        for (AbstractsEmergency emergency : emergencies) {
            if (emergency.getGroup().contains(group)) {
                emergency.resume(this, group);
            }
        }
    }

    /**
     * 獲取群組結束日期。
     *
     * @return 群組結束日期映射
     */
    public Map<String, Integer> getGroupDayEnd() {
        return groupDayEnd;
    }
}
