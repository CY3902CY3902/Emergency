package io.github.cy3902.emergency.world;


import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.emergency.DayEmergency;
import io.github.cy3902.emergency.manager.TaskManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public class DayWorld extends AbstractsWorld {
    private long lastTime;

    //時間
    private PriorityQueue<Map.Entry<String, Integer>> eventQueue;
    private Map<String, Integer> groupDayEnd = new HashMap<>();
    private Map<String, Integer> groupDayStop = new HashMap<>();
    private Emergency emergency = Emergency.getInstance();


    public DayWorld(String worldName, List<String> dayGroup) {
        super( worldName);
        this.lastTime = this.world.getTime();
        this.eventQueue = new PriorityQueue<>(Map.Entry.comparingByValue());
        loadEmergencySafely();
        for (String g : dayGroup) {
            if(!groupDayEnd.containsKey(g)){
                groupDayEnd.put(g, this.day);
                groupStates.put(g, TaskManager.TaskStatus.RUNNING);
                eventQueue.add(new AbstractMap.SimpleEntry<>(g, this.day));
            }
        }
        startDayChangeChecker();
    }


    // 計時器(day)
    private void startDayChangeChecker() {
        String taskId = "DayChangeChecker-" + this.world.getName();
        emergency.getTaskManager().startPeriodicTask(taskId, this::checkDayChange, 0L, 5L);
    }

    private void checkDayChange() {
        if (this.world == null) return;

        long currentTime = this.world.getTime();

        if (currentTime < this.lastTime) {
            day++;
            randomEmergency();
        }

        this.lastTime = currentTime;
    }

    private void loadEmergencySafely() {
        try {
            emergency.getDayWorldDAO().loadEmergency(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void randomEmergency() {
        Map.Entry<String, Integer> entry = eventQueue.peek();
        String g = entry.getKey();
        if(groupStates.get(g) != TaskManager.TaskStatus.PAUSED &&  day >= groupDayEnd.get(g)){
            AbstractsEmergency abstractsEmergency = randomGroupEmergency(g);
            if (abstractsEmergency != null) {
                startEmergency(g,abstractsEmergency);
            }
        }
    }

    public void startEmergency(String group,  AbstractsEmergency abstractsEmergency){
        if(!emergency.getEmergencyManager().getAllEmergencyByGroup(group).contains(abstractsEmergency)){
            return;
        }
        addOrUpdateEvent(group, this.day + abstractsEmergency.getDays());
        emergency.getEmergencyManager().earlyStop(this, group);
        groupDayEnd.put(group,this.day + abstractsEmergency.getDays());
        this.worldEmergency.put(group,abstractsEmergency);
        abstractsEmergency.start(this, group);
    }

    private AbstractsEmergency randomGroupEmergency(String g) {
        List<AbstractsEmergency> emergencies = this.emergency.getEmergencyManager().getAllEmergencyByGroup(g);

        if (emergencies == null || emergencies.isEmpty()) {
            return null;
        }

        double totalChance = emergencies.stream()
                .mapToDouble(AbstractsEmergency::getChance)
                .sum();

        if (totalChance <= 0) {
            return null; // No chance at all, return null
        }

        Random random = new Random();
        double randomValue = random.nextDouble() * totalChance; // Random value between 0 and totalChance

        double cumulative = 0.0;
        for (AbstractsEmergency e : emergencies) {
            cumulative += e.getChance();
            if (randomValue <= cumulative) {
                return e;
            }
        }
        return null;
    }


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

    public Map<String, TaskManager.TaskStatus> getGroupStates() {
        return groupStates;
    }

    public void pause(String group){
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

    public void resume(String group){
        if (!groupDayStop.containsKey(group)) {
            return;
        }
        this.groupStates.put(group, TaskManager.TaskStatus.RUNNING);
        int stopDay= this.groupDayStop.get(group);
        int endDay = this.groupDayEnd.get(group);
        int betweenDay = endDay-stopDay;
        int groupDayEnd = this.day+betweenDay;
        addOrUpdateEvent(group,groupDayEnd);
        List<AbstractsEmergency> emergencies =  new ArrayList<>(worldEmergency.values());
        for (AbstractsEmergency emergency : emergencies) {
            if (emergency.getGroup().contains(group)) {
                emergency.resume(this, group);
            }
        }
    }

    public Map<String, Integer> getGroupDayEnd() {
        return groupDayEnd;
    }


}
