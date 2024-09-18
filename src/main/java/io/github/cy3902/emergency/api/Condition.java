package io.github.cy3902.emergency.api;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.manager.WorldManager;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.conditions.CustomCondition;
import org.bukkit.boss.BossBar;


import java.util.*;
import java.util.stream.Collectors;


/**
 * 自訂條件類別，實現了 IEntityCondition 接口。
 * 用於檢查實體是否符合特定條件，根據指定的緊急事件列表進行檢查。
 */
public class Condition extends CustomCondition implements IEntityCondition {

    private final List<String> emergencies;

    /**
     * 建構函數，初始化 Condition 物件並獲取mythicmobs怪物設定之緊急事件列表。
     *
     * @param condition 設定條件的字串
     * @param line 由 MythicLineConfig 提供的配置行
     * @param mlc MythicLineConfig 配置對象
     */
    public Condition(String condition, String line, MythicLineConfig mlc) {
        super(condition, line, mlc);
        this.emergencies = new ArrayList<>(Arrays.asList(line.split(",")));
    }

    /**
     * 檢查實體是否符合條件。
     * 如果實體所在的世界包含任何符合條件的緊急事件，則返回 true。
     *
     * @param abstractEntity 要檢查的實體
     * @return 如果實體所在世界中的緊急事件符合條件，返回 true；否則返回 false
     */
    @Override
    public boolean check(AbstractEntity abstractEntity) {
        String world = abstractEntity.getWorld().getName();
        Emergency emergency = Emergency.getInstance();
        List<AbstractsWorld> abstractsWorldList = WorldManager.getWorldList();

        if (abstractsWorldList == null) {
            return false;
        }

        Set<AbstractsEmergency> abstractsEmergencyList;
        for (AbstractsWorld abstractsWorld : abstractsWorldList) {
            if (!abstractsWorld.getWorld().getName().equals(world)) {
                continue;
            }

            abstractsEmergencyList = new HashSet<>(abstractsWorld.getWorldEmergency().values());
            List<String> nameList = abstractsEmergencyList.stream()
                    .map(AbstractsEmergency::getName)
                    .collect(Collectors.toList());

            if (!Collections.disjoint(nameList, emergencies)) {
                return true;
            }
        }

        return false;
    }
}
