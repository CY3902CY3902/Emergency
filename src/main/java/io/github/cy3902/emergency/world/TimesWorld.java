package io.github.cy3902.emergency.world;


import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.task.TaskManager;
import io.github.cy3902.emergency.utils.EmergencyUtils;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class TimesWorld extends AbstractsWorld {
    //時間
    private PriorityQueue<Map.Entry<String, LocalDateTime>> eventQueue;
    private Map<String, LocalDateTime> groupTimeEnd = new HashMap<>();
    private Map<String, LocalDateTime> groupTimeStop = new HashMap<>();

    public TimesWorld(String worldName, List<String> timeGroup) {
        super( worldName);
        this.eventQueue = new PriorityQueue<>(Map.Entry.comparingByValue());
        for (String g : timeGroup) {
            eventQueue.add(new AbstractMap.SimpleEntry<>(g, LocalDateTime.now()));
            groupTimeEnd.put(g,LocalDateTime.now());
            groupStates.put(g, TaskManager.TaskStatus.RUNNING);
        }
        startTimesChangeChecker();
    }


    private void startTimesChangeChecker() {
        String taskId = "TimesChangeChecker-" + this.world.getName();

        Runnable timesChangeCheckerTask = () -> {
            if (this.world != null) {
                randomEmergency();
            }
        };

        emergency.getTaskManager().startPeriodicTask(taskId, timesChangeCheckerTask, 0L, 20L);
    }

    @Override
    public void randomEmergency() {
        Map.Entry<String, LocalDateTime> entry = eventQueue.peek();
        String g = entry.getKey();
        if(groupStates.get(g) != TaskManager.TaskStatus.PAUSED && LocalDateTime.now().isAfter(groupTimeEnd.get(g))){
            AbstractsEmergency abstractsEmergency = randomGroupEmergency(g);
            if (abstractsEmergency != null) {
                startEmergency(g, abstractsEmergency);
            }
        }
    }

    @Override
    public void startEmergency(String group,  AbstractsEmergency abstractsEmergency){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newTime = now.plusSeconds(abstractsEmergency.getDuration());
        addOrUpdateEvent(group, newTime);
        groupTimeEnd.put(group,newTime);
        EmergencyUtils.earlyTimeStop(this,group);
        this.worldEmergency.add(abstractsEmergency);
        abstractsEmergency.start(this, group);
    }


    private AbstractsEmergency randomGroupEmergency(String g) {
        List<AbstractsEmergency> emergencies = this.emergency.getEmergencyTimeGroup().get(g);

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
    public void pause(String group){
        this.groupStates.put(group, TaskManager.TaskStatus.PAUSED);
        this.groupTimeStop.put(group, LocalDateTime.now());
        List<AbstractsEmergency> emergencies = this.getWorldEmergency();
        for (AbstractsEmergency emergency : emergencies) {
            if (emergency.getGroup().contains(group)) {
                emergency.pause(this, group);
            }
        }
    }
    @Override
    public void resume(String group){
        this.groupStates.put(group, TaskManager.TaskStatus.RUNNING);
        LocalDateTime stopLocalDateTime= this.groupTimeStop.get(group);
        LocalDateTime endLocalDateTime= this.groupTimeEnd.get(group);
        Duration duration = Duration.between(stopLocalDateTime, endLocalDateTime);
        LocalDateTime result = LocalDateTime.now().plus(duration);
        addOrUpdateEvent(group,result);
        List<AbstractsEmergency> emergencies = this.worldEmergency;
        for (AbstractsEmergency emergency : emergencies) {
            if (emergency.getGroup().contains(group)) {
                emergency.resume(this, group);
            }
        }
    }

    public Map<String, LocalDateTime> getGroupTimeEnd() {
        return groupTimeEnd;
    }


}
