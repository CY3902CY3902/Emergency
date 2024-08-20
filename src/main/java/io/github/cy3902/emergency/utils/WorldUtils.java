package io.github.cy3902.emergency.utils;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldUtils {
    public static List<String> getAllWorld() {
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(Emergency.getEmergencyDayWorld().keySet());
        allKeys.addAll(Emergency.getEmergencyTimeWorld().keySet());
        return new ArrayList<>(allKeys);
    }

    public static void allWorldStop() {
        for(AbstractsWorld world : Emergency.getEmergencyDayWorld().values()){
            Emergency.getTaskManager().cancelTask("DayChangeChecker-" + world.getWorld().getName());
        }
        for(AbstractsWorld world : Emergency.getEmergencyTimeWorld().values()){
            Emergency.getTaskManager().cancelTask("TimesChangeChecker-" + world.getWorld().getName());
        }
    }

    public static void allWorldPause() {
        for(AbstractsWorld world : Emergency.getEmergencyDayWorld().values()){
            Emergency.getTaskManager().pauseTask("DayChangeChecker-" + world.getWorld().getName());
        }
        for(AbstractsWorld world : Emergency.getEmergencyTimeWorld().values()){
            Emergency.getTaskManager().pauseTask("TimesChangeChecker-" + world.getWorld().getName());
        }
    }

    public static List<String> getAllWorldByGroup(String group) {
        Set<AbstractsWorld> allValue = new HashSet<>();
        allValue.addAll(Emergency.getEmergencyDayWorld().values());
        allValue.addAll(Emergency.getEmergencyTimeWorld().values());

        List<String> abstractsWorlds = allValue.stream()
                .filter(abstractsworld -> abstractsworld.getGroupStates().containsKey(group))
                .map(abstractsworld -> abstractsworld.getWorld().getName()) // 使用 lambda 表達式來取得世界名稱
                .collect(Collectors.toList());

        return abstractsWorlds;
    }
}

