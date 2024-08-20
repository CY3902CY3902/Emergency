package io.github.cy3902.emergency.abstracts;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.interfaces.EmergencyController;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public abstract class AbstractsEmergency implements EmergencyController {
    protected Emergency emergency;
    protected String name;
    protected List<String> startCommand;
    protected List<String> endCommand;
    protected List<String> onJoinCommand;
    protected List<String> onQuitCommand;
    protected List<String> group;
    protected boolean bossBarBool;
    protected BossBar bossBar;
    protected double chance;
    protected int days;
    protected final Map<String, BukkitTask> activeTasks = new ConcurrentHashMap<>();
    protected long duration;
    public enum EmergencyType {
        Day, Time
    }



    protected void startCommand(AbstractsWorld abstractsWorld) {
        Collection<? extends Player> playersInWorld = Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getWorld().equals(abstractsWorld.getWorld()))
                .collect(Collectors.toList());
        for (String c : startCommand) {
            for (Player player : playersInWorld) {
                String playerCommand = c.replace("%player%", player.getName());

                // 執行命令
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), playerCommand);
            }
        }
    }

    public void endCommand(AbstractsWorld abstractsWorld) {
        Collection<? extends Player> playersInWorld = Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getWorld().equals(abstractsWorld.getWorld()))
                .collect(Collectors.toList());
        for (String c : endCommand) {
            for (Player player : playersInWorld) {
                String playerCommand = c.replace("%player%", player.getName());

                // 執行命令
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), playerCommand);
            }
        }
    }

    public void OnJoinCommand(Player  player){
        for (String c : onJoinCommand) {
            String playerCommand = c.replace("%player%", player.getName());

            // 執行命令
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), playerCommand);
        }
    }
    public void OnQuitCommand(Player  player){
        for (String c : onQuitCommand) {
            String playerCommand = c.replace("%player%", player.getName());

            // 執行命令
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), playerCommand);
        }
    }

    public AbstractsEmergency(String name) {
        this.emergency = Emergency.getInstance();
        this.name = name;
    }

    @Override
    public void start(AbstractsWorld world, String group) {
    }


    @Override
    public void stop(AbstractsWorld world, String group) {
    }

    @Override
    public void pause(AbstractsWorld world, String group) {
    }

    @Override
    public void resume(AbstractsWorld world, String group) {
    }

    @Override
    public void createBossBar(AbstractsWorld world) {
        if (this.bossBarBool == true) {
            for (Player player : world.getWorld().getPlayers()) {
                this.bossBar.addPlayer(player);
            }
        }
    }

    @Override
    public void removeBossBar(AbstractsWorld world) {
        if (this.bossBarBool == true) {
            for (Player player : world.getWorld().getPlayers()) {
                this.bossBar.removePlayer(player);
            }
        }
    }


    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for startCommand
    public List<String> getStartCommand() {
        if (startCommand == null) {
            return Collections.emptyList();
        }
        return startCommand;
    }

    // Getter for endCommand
    public List<String> getEndCommand() {
        if (endCommand == null) {
            return Collections.emptyList();
        }
        return endCommand;
    }

    // Getter for bossBarBool
    public boolean isBossBarBool() {
        return bossBarBool;
    }

    // Getter for bossBar
    public BossBar getBossBar() {
        return bossBar;
    }

    // Getter for chance
    public double getChance() {
        return chance;
    }


    // Getter for days
    public int getDays() {
        return days;
    }

    public Map<String, BukkitTask> getActiveTasks() {
        return activeTasks;
    }

    public long getDuration() {
        return duration;
    }

    public List<String> getGroup() {
        return group;
    }

}
