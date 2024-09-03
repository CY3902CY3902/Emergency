package io.github.cy3902.emergency.utils;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.manager.WorldManager;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldUtils {

    public static List<String> getAbstractsWorldListName(List<AbstractsWorld> abstractsWorldList){
        List<String> nameList = abstractsWorldList.stream()
                .map(abstractsWorld -> abstractsWorld.getWorld().getName())
                .collect(Collectors.toList());
        return nameList;
    }
}

