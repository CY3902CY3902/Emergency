package io.github.cy3902.emergency.manager;
;
import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.world.DayWorld;
import io.github.cy3902.emergency.world.TimeWorld;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class WorldManager {
    private static final Emergency emergency = Emergency.getInstance();

    //所有物件
    private static List<AbstractsWorld> worldList = new ArrayList<>();

    public static void allEmergencyStop() {
        for (AbstractsWorld abstractsWorld : worldList) {
            for (String group : emergency.getEmergencyManager().getAllGroups()){
                emergency.getEmergencyManager().earlyStop(abstractsWorld,group);
            }
        }
    }

    public static void allRunningEmergencySave() {
        emergency.getSql().clearTables("emergency_day_world");
        emergency.getSql().clearTables("emergency_time_world");
        emergency.getSql().connect();
        try (Connection conn = emergency.getSql().getConnection()) {
            conn.setAutoCommit(false);
            for (AbstractsWorld world : worldList) {
                if (world instanceof DayWorld) {
                    emergency.getDayWorldDAO().saveDayWorld((DayWorld) world, conn);
                } else if (world instanceof TimeWorld) {
                    emergency.getTimeWorldDAO().saveTimeWorld((TimeWorld) world, conn);
                }
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void allEmergencyPause() {
        for (AbstractsWorld abstractsWorld : worldList) {
            for (String group : emergency.getEmergencyManager().getAllGroups()){
                emergency.getEmergencyManager().emergencyPause(abstractsWorld,group);
            }
        }
    }
    public void add(AbstractsWorld abstractsWorld){
        worldList.add(abstractsWorld);
    }

    public void clear(){
        worldList = new ArrayList<>();
    }



    public static List<String> getWorldNameList() {
        Set<String> nameSet = worldList.stream()
                .map(abstractWorld -> abstractWorld.getWorld().getName())
                .collect(Collectors.toSet());
        return new ArrayList<>(nameSet);
    }


    public static void allWorldStop() {
        for(AbstractsWorld world : worldList){
            if(world instanceof DayWorld){
                Emergency.getTaskManager().cancelTask("DayChangeChecker-" + world.getWorld().getName());
            }
            if(world instanceof TimeWorld){
                Emergency.getTaskManager().cancelTask("TimeChangeChecker-" + world.getWorld().getName());
            }
        }
    }

    public static void allWorldPause() {
        for(AbstractsWorld world : worldList){
            if(world instanceof DayWorld){
                Emergency.getTaskManager().pauseTask("DayChangeChecker-" + world.getWorld().getName());
            }
            if(world instanceof TimeWorld){
                Emergency.getTaskManager().pauseTask("TimeChangeChecker-" + world.getWorld().getName());
            }
        }
    }

    public static List<AbstractsWorld> getAllWorldByGroup(String group) {
        List<AbstractsWorld> abstractsWorldList = worldList.stream()
                .filter(abstractsWorld -> abstractsWorld.getGroupStates().containsKey(group))
                .collect(Collectors.toList());

        return abstractsWorldList;
    }

    public static List<AbstractsWorld> getAllWorldByName(String name) {
        List<AbstractsWorld> abstractsWorldList = new ArrayList<>();

        for (AbstractsWorld abstractsWorld : worldList) {
            if (abstractsWorld.getWorld().getName().equals(name)) {
                abstractsWorldList.add(abstractsWorld);
            }
        }
        return abstractsWorldList;
    }

    public static List<AbstractsWorld> getWorldList() {
        return worldList;
    }





}
