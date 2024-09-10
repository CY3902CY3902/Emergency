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
    private static Emergency emergency = Emergency.getInstance();
    //所有物件
    private static List<AbstractsEmergency> emergencyList = new ArrayList<>();

    //群組分類後的物件
    private static Multimap<String, AbstractsEmergency> emergencyGroupMap = ArrayListMultimap.create();



    public static AbstractsEmergency findAndRemoveTask(AbstractsWorld abstractsWorld, String group) {
        Collection<AbstractsEmergency> emergencies = emergencyGroupMap.get(group);
        String taskId = "task-" + group + "-" + abstractsWorld.getWorld().getName();
        AbstractsEmergency abstractsEmergency = null;

        //搜尋事件
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
            Emergency.getInstance().getTaskManager().pauseTask(taskId);
            Emergency.getInstance().getTaskManager().cancelTask(taskId);
            return abstractsEmergency;
        }
        return null;
    }


    public static void earlyStop(AbstractsWorld abstractsWorld, String group){
        AbstractsEmergency abstractsEmergency =  findAndRemoveTask(abstractsWorld, group);
        if(abstractsEmergency !=null){
            abstractsEmergency.stop(abstractsWorld,group);
        }
        return;
    }

    public static void emergencyPause(AbstractsWorld abstractsWorld, String group){
        AbstractsEmergency abstractsEmergency =  findAndRemoveTask(abstractsWorld, group);
        if(abstractsEmergency !=null){
            abstractsEmergency.pause(abstractsWorld,group);
        }
        return;
    }

    public static Set<String> getAllGroups() {
        return emergencyGroupMap.keySet();
    }

    public static  List<AbstractsEmergency> getAllEmergencyByGroup(String group) {
        return new ArrayList<>(emergencyGroupMap.get(group));
    }


    public static AbstractsEmergency findEmergencyByName(String name) {
        return   emergencyList.stream()
                .filter(emergency -> name.equals(emergency.getName()))
                .findFirst()
                .orElse(null);
    }

    public static ArrayList<String> checkForDuplicateGroup(AbstractsEmergency abstractsEmergency) {
        List<String> group = abstractsEmergency.getGroup();
        ArrayList<String> result = new ArrayList<String>();
        for (String g : group) {
            if ( emergencyGroupMap.containsKey(g)) {
                AbstractsEmergency abstractsEmergencyFirst = emergencyGroupMap.get(g).stream().findFirst().get();
                if(abstractsEmergencyFirst.getClass() != abstractsEmergencyFirst.getClass()){
                    result.add(g);
                }
            }
        }
        return result;
    }



    public void add(AbstractsEmergency abstractsEmergency) {
        ArrayList<String> checkForDuplicateGroup= checkForDuplicateGroup(abstractsEmergency);
        if (!checkForDuplicateGroup.isEmpty()) {
            this.emergency.info(this.emergency.getLang().duplicateGroupName + checkForDuplicateGroup, Level.SEVERE);
            return;
        }
        if(findEmergencyByName(abstractsEmergency.getName()) != null){
            this.emergency.info(this.emergency.getLang().duplicateEmergencyMessage+ abstractsEmergency.getName(), Level.SEVERE);
            return;
        }
        emergencyList.add(abstractsEmergency);
        for(String group : abstractsEmergency.getGroup()){
            emergencyGroupMap.put(group,abstractsEmergency);
        }
    }

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

    public void clear(){
        emergencyList = new ArrayList<>();
        emergencyGroupMap = ArrayListMultimap.create();
    }




}
