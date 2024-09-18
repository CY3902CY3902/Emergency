package io.github.cy3902.emergency.api;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * 監聽 MythicMobs 事件的類別。
 * 用於處理 MythicMobs 的條件加載事件，並註冊自定義條件。
 */
public class MythicMobsEventListener implements Listener {

    /**
     * 處理 MythicConditionLoadEvent 事件。
     * 當條件名稱為 "Emergency" 時，從配置中讀取參數並註冊自定義條件。
     *
     * @param event MythicConditionLoadEvent 事件對象
     */
    @EventHandler
    public void onMythicConditionLoad(MythicConditionLoadEvent event) {
        if (event.getConditionName().equalsIgnoreCase("Emergency")) {
            MythicLineConfig config = event.getConfig();
            String argument = config.getString("name", "name");
            event.register(new Condition(event.getConditionName(), argument, event.getConfig()));
        }
    }
}
