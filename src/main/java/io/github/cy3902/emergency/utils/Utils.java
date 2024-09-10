package io.github.cy3902.emergency.utils;

import io.github.cy3902.emergency.abstracts.AbstractsWorld;


import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static List<String> getAbstractsWorldListName(List<AbstractsWorld> abstractsWorldList){
        List<String> nameList = abstractsWorldList.stream()
                .map(abstractsWorld -> abstractsWorld.getWorld().getName())
                .collect(Collectors.toList());
        return nameList;
    }

    public static long calculateSecondsBetween(LocalDateTime startTime, LocalDateTime endTime) {
        // 使用系統默認的時區
        ZoneId zoneId = ZoneId.systemDefault();

        // 將 LocalDateTime 轉換為 Instant
        Instant startInstant = startTime.atZone(zoneId).toInstant();
        Instant endInstant = endTime.atZone(zoneId).toInstant();

        // 計算秒數差異
        Duration duration = Duration.between(startInstant, endInstant);
        return duration.getSeconds();
    }
}

