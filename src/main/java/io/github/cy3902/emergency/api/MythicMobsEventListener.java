package io.github.cy3902.emergency.api;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MythicMobsEventListener implements Listener {

    @EventHandler
    public void onMythicConditionLoad(MythicConditionLoadEvent event) {
        if(event.getConditionName().equalsIgnoreCase("Emergency"))	{
            MythicLineConfig config = event.getConfig();
            String argument = config.getString("name", "name");
            event.register(new Condition( event.getConditionName(), argument, event.getConfig()));
        }
    }
}
