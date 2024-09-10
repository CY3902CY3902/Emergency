package io.github.cy3902.emergency.api;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.conditions.CustomCondition;
import org.bukkit.boss.BossBar;


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
        Emergency emergency = Emergency.getInstance();
        List<AbstractsWorld> abstractsWorldList = emergency.getWorldManager().getWorldList();
        if (abstractsWorldList == null){
            return false;
        }


        Set<AbstractsEmergency> abstractsEmergencyList;
        for(AbstractsWorld abstractsWorld : abstractsWorldList) {
            if(abstractsWorld.getWorld().getName() != world){
                continue;
            }
            abstractsEmergencyList = new HashSet<>(abstractsWorld.getWorldEmergency().values());
            List<String> nameList = abstractsEmergencyList.stream()
                    .map(AbstractsEmergency::getName)
                    .collect(Collectors.toList());
            if(!Collections.disjoint(nameList, emergencies)){
                return true;
            }
        }

        return false;
    }

}
