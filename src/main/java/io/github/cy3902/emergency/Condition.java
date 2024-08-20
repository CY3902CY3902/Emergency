package io.github.cy3902.emergency;

import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.utils.EmergencyUtils;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.conditions.CustomCondition;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

        //世界天數週期的緊急事件是否存在
        List<AbstractsEmergency> abstractsEmergencies = Emergency.getEmergencyDayWorld().get(world).getWorldEmergency();
        List<String> eventNames = abstractsEmergencies.stream()
                .map(customObject -> customObject.getName())
                .collect(Collectors.toList());
        if (emergencies.stream().anyMatch(eventNames::contains)) {
            return true;
        }
        //時間週期的緊急事件是否存在
        abstractsEmergencies = Emergency.getEmergencyTimeWorld().get(world).getWorldEmergency();
        eventNames = abstractsEmergencies.stream()
                .map(customObject -> customObject.getName())
                .collect(Collectors.toList());
        if (emergencies.stream().anyMatch(eventNames::contains)) {
            return true;
        }

        return false;
    }

}
