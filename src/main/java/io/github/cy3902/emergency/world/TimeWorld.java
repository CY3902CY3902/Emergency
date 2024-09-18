package io.github.cy3902.emergency.world;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.manager.EmergencyManager;
import io.github.cy3902.emergency.manager.TaskManager;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 處理與時間相關的事件世界。
 */
public class TimeWorld extends AbstractsWorld {

    // 儲存事件的優先佇列
    private final PriorityQueue<Map.Entry<String, LocalDateTime>> eventQueue;
    // 儲存每個群組的結束時間
    private final Map<String, LocalDateTime> groupTimeEnd = new HashMap<>();
    // 儲存每個群組的暫停時間
    private final Map<String, LocalDateTime> groupTimeStop = new HashMap<>();
    private final Emergency emergency = Emergency.getInstance();

    /**
     * 建構子，初始化世界和事件列表。
     *
     * @param worldName 世界名稱
     * @param timeGroup 事件群組列表
     */
    public TimeWorld(String worldName, List<String> timeGroup) {
        super(worldName);
        this.eventQueue = new PriorityQueue<>(Map.Entry.comparingByValue());
        loadEmergencySafely();
        for (String g : timeGroup) {
            if (!groupTimeEnd.containsKey(g)) {
                groupTimeEnd.put(g, LocalDateTime.now());
                groupStates.put(g, TaskManager.TaskStatus.RUNNING);
                eventQueue.add(new AbstractMap.SimpleEntry<>(g, LocalDateTime.now()));
            }
        }
        startTimeChangeChecker();
    }

    /**
     * 啟動一個定時任務，每 5 秒檢查一次時間變化。
     */
    private void startTimeChangeChecker() {
        String taskId = "TimeChangeChecker-" + this.world.getName();
        Emergency.getTaskManager().startPeriodicTask(taskId, this::checkTimeChange, 0L, 5L);
    }

    /**
     * 檢查時間變化，並隨機觸發事件。
     */
    private void checkTimeChange() {
        if (this.world == null) return;
        randomEmergency();
    }

    /**
     * 安全地加載事件數據。
     */
    private void loadEmergencySafely() {
        try {
            Emergency.getTimeWorldDAO().loadEmergency(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    /**
     * 隨機選擇並啟動一個事件，如果當前時間超過了群組的結束時間。
     */
    public void randomEmergency() {
        Map.Entry<String, LocalDateTime> entry = eventQueue.peek();
        String g = entry.getKey();
        if (groupStates.get(g) != TaskManager.TaskStatus.PAUSED && LocalDateTime.now().isAfter(groupTimeEnd.get(g))) {
            AbstractsEmergency abstractsEmergency = randomGroupEmergency(g);
            if (abstractsEmergency != null) {
                startEmergency(g, abstractsEmergency);
            }
        }
    }

    @Override
    /**
     * 啟動指定群組的事件並更新事件時間。
     *
     * @param group            群組名稱
     * @param abstractsEmergency  事件實例
     */
    public void startEmergency(String group, AbstractsEmergency abstractsEmergency) {
        if (!EmergencyManager.getAllEmergencyByGroup(group).contains(abstractsEmergency)) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newTime = now.plusSeconds(abstractsEmergency.getDuration());
        addOrUpdateEvent(group, newTime);
        groupTimeEnd.put(group, newTime);
        EmergencyManager.earlyStop(this, group);
        this.worldEmergency.put(group, abstractsEmergency);
        abstractsEmergency.start(this, group);
    }

    /**
     * 根據群組隨機選擇一個事件。
     *
     * @param g 群組名稱
     * @return 選擇的事件實例，如果沒有可選擇的事件則返回 null
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
        double randomValue = random.nextDouble() * totalChance; // 隨機值在 0 到 totalChance 之間

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
     * @param key     群組名稱
     * @param newTime 新的時間
     */
    public void addOrUpdateEvent(String key, LocalDateTime newTime) {
        if (groupTimeEnd.containsKey(key)) {
            Map.Entry<String, LocalDateTime> oldEvent = new AbstractMap.SimpleEntry<>(key, groupTimeEnd.get(key));
            eventQueue.remove(oldEvent);
        }
        Map.Entry<String, LocalDateTime> newEvent = new AbstractMap.SimpleEntry<>(key, newTime);
        eventQueue.add(newEvent);
        groupTimeEnd.put(key, newTime);
        groupStates.put(key, TaskManager.TaskStatus.RUNNING);
    }

    @Override
    /**
     * 暫停指定群組的事件。
     *
     * @param group 群組名稱
     */
    public void pause(String group) {
        if (!groupTimeEnd.containsKey(group)) {
            return;
        }
        this.groupStates.put(group, TaskManager.TaskStatus.PAUSED);
        this.groupTimeStop.put(group, LocalDateTime.now());
        HashSet<AbstractsEmergency> emergencies = new HashSet<>(this.getWorldEmergency().values());
        for (AbstractsEmergency emergency : emergencies) {
            if (emergency.getGroup().contains(group)) {
                emergency.pause(this, group);
            }
        }
    }

    @Override
    /**
     * 恢復指定群組的事件。
     *
     * @param group 群組名稱
     */
    public void resume(String group) {
        if (!groupTimeStop.containsKey(group)) {
            return;
        }
        this.groupStates.put(group, TaskManager.TaskStatus.RUNNING);
        LocalDateTime stopLocalDateTime = this.groupTimeStop.get(group);
        LocalDateTime endLocalDateTime = this.groupTimeEnd.get(group);
        Duration duration = Duration.between(stopLocalDateTime, endLocalDateTime);
        LocalDateTime result = LocalDateTime.now().plus(duration);
        addOrUpdateEvent(group, result);
        List<AbstractsEmergency> emergencies = new ArrayList<>(this.worldEmergency.values());
        for (AbstractsEmergency emergency : emergencies) {
            if (emergency.getGroup().contains(group)) {
                emergency.resume(this, group);
            }
        }
    }

    /**
     * 獲取每個群組的結束時間。
     *
     * @return 包含群組名稱和結束時間的映射
     */
    public Map<String, LocalDateTime> getGroupTimeEnd() {
        return groupTimeEnd;
    }
}
