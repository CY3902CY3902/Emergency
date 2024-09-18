package io.github.cy3902.emergency.utils;

import io.github.cy3902.emergency.abstracts.AbstractsWorld;


import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工具類別，提供通用的輔助方法。
 */
public class Utils {

    /**
     * 從 AbstractsWorld 物件列表中取得世界名稱列表。
     *
     * @param abstractsWorldList AbstractsWorld 物件列表
     * @return 世界名稱列表
     */
    public static List<String> getAbstractsWorldListName(List<AbstractsWorld> abstractsWorldList) {
        List<String> nameList = abstractsWorldList.stream()
                .map(abstractsWorld -> abstractsWorld.getWorld().getName())
                .collect(Collectors.toList());
        return nameList;
    }

    /**
     * 計算兩個 LocalDateTime 之間的秒數差異。
     *
     * @param startTime 開始時間
     * @param endTime   結束時間
     * @return 兩者之間的秒數差異
     */
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

