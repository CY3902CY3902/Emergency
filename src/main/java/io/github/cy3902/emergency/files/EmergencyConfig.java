package io.github.cy3902.emergency.files;

import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.FileProviderList;
import io.github.cy3902.emergency.emergency.DayEmergency;

import io.github.cy3902.emergency.emergency.TimeEmergency;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.*;

import java.util.*;

public class EmergencyConfig extends FileProviderList {

    public EmergencyConfig(String path) {
        super(path);
    }

    @Override
    protected void readDefault(YamlConfiguration yml) {
        Set<String> keys = this.yml.getKeys(false);
        for (String key : keys) {
            this.readEmergency(key);
        }
    }

    private void readEmergency(String name) {
        List<String> startCommand = getValue(this.yml, name + ".start_command", Arrays.asList(""));
        List<String> endCommand = getValue(this.yml, name + ".end_command", Arrays.asList(""));
        List<String> onJoinCommand = getValue(this.yml, name + ".on_join_command", Arrays.asList(""));
        List<String> onQuitCommand = getValue(this.yml, name + ".on_quit_command", Arrays.asList(""));
        List<String> group = getValue(this.yml, name + ".group", Arrays.asList(""));
        String type = getValue(this.yml, name + ".type", "Time");
        int days = getValue(this.yml, name + ".days", 0);
        double chance = ((Number) getValue(this.yml, name + ".chance", 0.0)).doubleValue();
        long duration = getValue(this.yml, name + ".duration", 200);

        BossBar bossBar = createBossBar(name);

        AbstractsEmergency abstractsEmergency;
        AbstractsEmergency.EmergencyType emergencyType;

        try{
            emergencyType  = AbstractsEmergency.EmergencyType.valueOf(type);
        }catch (IllegalArgumentException t){
            emergencyType  = AbstractsEmergency.EmergencyType.valueOf("Time");
        }

        if (emergencyType == AbstractsEmergency.EmergencyType.Day) {
            abstractsEmergency = new DayEmergency(
                    name,
                    bossBar != null,
                    bossBar,
                    startCommand,
                    endCommand,
                    onJoinCommand,
                    onQuitCommand,
                    group,
                    chance,
                    days,
                    duration
            );
        } else if (emergencyType == AbstractsEmergency.EmergencyType.Time) {
            abstractsEmergency = new TimeEmergency(
                    name,
                    bossBar != null,
                    bossBar,
                    startCommand,
                    endCommand,
                    onJoinCommand,
                    onQuitCommand,
                    group,
                    chance,
                    days,
                    duration
            );
        }else{
            return;
        }
        emergency.registerEmergency(abstractsEmergency);
    }

    private BossBar createBossBar(String name) {
        boolean bossBarBool = getValue(this.yml, name + ".BossBar.enable", false);
        if (bossBarBool) {
            String bossBarColor = getValue(this.yml, name + ".BossBar.color", "RED");
            String bossBarTitle = getValue(this.yml, name + ".BossBar.title", "NULL");
            String bossBarStyle = getValue(this.yml, name + ".BossBar.bar_style", "SOLID");
            return Bukkit.createBossBar(bossBarTitle, BarColor.valueOf(bossBarColor), BarStyle.valueOf(bossBarStyle));
        }
        return null;
    }


}
