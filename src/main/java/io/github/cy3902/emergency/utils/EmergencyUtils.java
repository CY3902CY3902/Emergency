package io.github.cy3902.emergency.utils;

import io.github.cy3902.emergency.Emergency;

import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.emergency.DayEmergency;
import io.github.cy3902.emergency.emergency.TimeEmergency;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class EmergencyUtils {


    public static AbstractsEmergency findEmergencyByName(String name) {
        AbstractsEmergency abstractsTimeEmergency = Emergency.getEmergencyTimeGroup().values().stream()
                .flatMap(List::stream)
                .filter(emergency -> name.equals(emergency.getName()))
                .findFirst()
                .orElse(null);
        AbstractsEmergency abstractsDayEmergency = Emergency.getEmergencyDayGroup().values().stream()
                .flatMap(List::stream)
                .filter(emergency -> name.equals(emergency.getName()))
                .findFirst()
                .orElse(null);
        return (abstractsTimeEmergency != null) ? abstractsTimeEmergency : abstractsDayEmergency;
    }

    public static List<String> getAllGroups() {
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(Emergency.getEmergencyTimeGroup().keySet());
        allKeys.addAll(Emergency.getEmergencyDayGroup().keySet());
        return new ArrayList<>(allKeys);
    }

    public static  List<AbstractsEmergency> getAllEmergencyByGroup(String group) {
        if (Emergency.getEmergencyTimeGroup().containsKey(group)){
            return  Emergency.getEmergencyTimeGroup().get(group);
        }
        if (Emergency.getEmergencyDayGroup().containsKey(group)){
            return  Emergency.getEmergencyDayGroup().get(group);
        }
        return new ArrayList<>();
    }

    public static void allEmergencyStop() {
        for (AbstractsWorld abstractsWorld : Emergency.getEmergencyDayWorld().values()) {
            for (String group : Emergency.getEmergencyDayGroup().keySet()){
                earlyDayStop(abstractsWorld,group);
            }
        }

        for (AbstractsWorld abstractsWorld : Emergency.getEmergencyTimeWorld().values()) {
            for (String group : Emergency.getEmergencyTimeGroup().keySet()){
                earlyTimeStop(abstractsWorld,group);
            }
        }
    }

    public static void allEmergencyPause() {
        for (AbstractsWorld abstractsWorld : Emergency.getEmergencyDayWorld().values()) {
            for (String group : Emergency.getEmergencyDayGroup().keySet()){
                emergencyPause(abstractsWorld,group);
            }
        }

        for (AbstractsWorld abstractsWorld : Emergency.getEmergencyTimeWorld().values()) {
            for (String group : Emergency.getEmergencyTimeGroup().keySet()){
                emergencyPause(abstractsWorld,group);
            }
        }
    }

    public static AbstractsEmergency findAndRemoveTask(AbstractsWorld abstractsWorld, String group, Map<String, List<AbstractsEmergency>> emergencyGroup) {
        List<AbstractsEmergency> emergencies = emergencyGroup.get(group);
        String taskId = "task-" + group + "-" + abstractsWorld.getWorld().getName();
        AbstractsEmergency abstractsEmergency = null;

        //搜尋事件
        for (AbstractsEmergency emergency : emergencies) {
            if (abstractsWorld.getWorldEmergency().contains(emergency)) {
                abstractsEmergency = emergency;
            }
        }
        if (abstractsEmergency == null) {
            return null;
        }

        Map<String,BukkitTask> tasks = abstractsEmergency.getActiveTasks();
        BukkitTask task = tasks.get(taskId);
        if (task != null) {
            tasks.remove(taskId);
            Emergency.getInstance().getTaskManager().pauseTask(taskId);
            Emergency.getInstance().getTaskManager().cancelTask(taskId);
            return abstractsEmergency;
        }
        return null;
    }



    public static void earlyTimeStop(AbstractsWorld abstractsWorld, String group){
        AbstractsEmergency abstractsEmergency =  EmergencyUtils.findAndRemoveTask(abstractsWorld, group, Emergency.getEmergencyTimeGroup());
        if(abstractsEmergency !=null){
            abstractsEmergency.stop(abstractsWorld,group);
        }
        return;
    }

    public static void earlyDayStop(AbstractsWorld abstractsWorld, String group){
        AbstractsEmergency abstractsEmergency =  EmergencyUtils.findAndRemoveTask(abstractsWorld, group, Emergency.getEmergencyDayGroup());
        if(abstractsEmergency !=null){
            abstractsEmergency.stop(abstractsWorld,group);
        }
        return;
    }

    public static void emergencyPause(AbstractsWorld abstractsWorld, String group){
        AbstractsEmergency abstractsEmergency =  EmergencyUtils.findAndRemoveTask(abstractsWorld, group, Emergency.getEmergencyDayGroup());
        if(abstractsEmergency !=null){
            abstractsEmergency.pause(abstractsWorld,group);
        }
        return;
    }

    public static boolean checkForDuplicateGroup(AbstractsEmergency abstractsEmergency) {
        List<String> group = abstractsEmergency.getGroup();
        Emergency emergency = Emergency.getInstance();
        for (String g : group) {
            if ( emergency.getEmergencyTimeGroup().containsKey(g) && abstractsEmergency instanceof DayEmergency) {
                return true;
            }
            if (emergency.getEmergencyDayGroup().containsKey(g) && abstractsEmergency instanceof TimeEmergency) {
                return true;
            }
        }
        return false;
    }


}
