package io.github.cy3902.emergency.api;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.conditions.CustomCondition;


import java.util.*;
import java.util.stream.Collectors;


public class Condition extends CustomCondition implements IEntityCondition {

    private List<String> emergencies;

    public Condition(String condition, String line, MythicLineConfig mlc) {

        super(condition, line, mlc);
        this.emergencies = new ArrayList<String>(Arrays.asList(line.split(",")));
    }


    @Override
    public boolean check(AbstractEntity abstractEntity){
        String world = abstractEntity.getWorld().getName();

        if (Emergency.getEmergencyDayWorld() == null) {
            return false;
        }

        if (Emergency.getEmergencyDayWorld().get(world) == null && Emergency.getEmergencyTimeWorld().get(world) == null){
            return false;
        }

        List<AbstractsEmergency> dayWorldEmergencies = Emergency.getEmergencyDayWorld().get(world).getWorldEmergency();
        List<AbstractsEmergency> timeWorldEmergencies = Emergency.getEmergencyTimeWorld().get(world).getWorldEmergency();

        Set<String> eventNames = new HashSet<>();
        if (dayWorldEmergencies != null) {
            for (AbstractsEmergency emergency : dayWorldEmergencies) {
                eventNames.add(emergency.getName());
            }
        }
        if (timeWorldEmergencies != null) {
            for (AbstractsEmergency emergency : timeWorldEmergencies) {
                eventNames.add(emergency.getName());
            }
        }

        // 檢查是否有任何指定的緊急事件名稱存在
        for (String emergency : emergencies) {
            if (eventNames.contains(emergency)) {
                return true;
            }
        }

        return false;
    }

}
