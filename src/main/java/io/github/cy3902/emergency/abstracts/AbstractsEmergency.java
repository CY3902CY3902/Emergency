package io.github.cy3902.emergency.abstracts;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.interfaces.EmergencyController;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
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
        if(startCommand.equals(Arrays.asList(""))){ return;};
        for (String c : startCommand) {
            if(c.contains("%player_in_world%") && c.contains("%online_player%")){
                this.emergency.info(emergency.getLang().conflictingPlaceholders + c, Level.SEVERE);
                return;
            }
            if(c.contains("%player_in_world%")){
                for (Player player : playersInWorld) {
                    String playerCommand = c.replace("%player_in_world%", player.getName());
                    // 執行命令
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), playerCommand);
                }
            }else if(c.contains("%online_player%")){
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String playerCommand = c.replace("%online_player%", player.getName());
                    // 執行命令
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), playerCommand);
                }
            }
            else{
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
            }
        }
    }

    public void endCommand(AbstractsWorld abstractsWorld) {
        Collection<? extends Player> playersInWorld = Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getWorld().equals(abstractsWorld.getWorld()))
                .collect(Collectors.toList());
        if(endCommand.equals(Arrays.asList(""))){ return;};
        for (String c : endCommand) {
            if(c.contains("%player_in_world%") && c.contains("%online_player%")){
                this.emergency.info(emergency.getLang().conflictingPlaceholders + c, Level.SEVERE);
                continue;
            }
            if(c.contains("%player_in_world%")){
                for (Player player : playersInWorld) {
                    String playerCommand = c.replace("%player_in_world%", player.getName());
                    // 執行命令
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), playerCommand);
                }
            }else if(c.contains("%online_player%")){
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String playerCommand = c.replace("%online_player%", player.getName());
                    // 執行命令
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), playerCommand);
                }
            }
            else{
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), c);
            }
        }
    }

    public void OnJoinCommand(AbstractsWorld abstractsWorld,Player  player){
        Collection<? extends Player> playersInWorld = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getWorld().equals(abstractsWorld.getWorld()))
                .collect(Collectors.toList());
        if(onJoinCommand.equals(Arrays.asList(""))){ return;};
        for (String c : onJoinCommand) {
            if(c.contains("%player_in_world%") && c.contains("%online_player%")){
                this.emergency.info(emergency.getLang().conflictingPlaceholders+ c, Level.SEVERE);
                continue;
            }
            String playerCommand = c.replace("%player%", player.getName());
            if(c.contains("%player_in_world%")){
                for (Player p : playersInWorld) {
                    playerCommand = playerCommand.replace("%player%", p.getName());
                    // 執行命令
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), playerCommand);
                }
            }else if(c.contains("%online_player%")){
                for (Player p : Bukkit.getOnlinePlayers()) {
                    playerCommand = playerCommand.replace("%player%", p.getName());
                    // 執行命令
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), playerCommand);
                }
            }
            else{
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), playerCommand);
            }
        }
    }
    public void OnQuitCommand(AbstractsWorld abstractsWorld,Player  player){
        Collection<? extends Player> playersInWorld = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getWorld().equals(abstractsWorld.getWorld()))
                .collect(Collectors.toList());
        if(onQuitCommand.equals(Arrays.asList(""))){ return;};
        for (String c : onQuitCommand) {
            if(c.contains("%player_in_world%") && c.contains("%online_player%")){
                this.emergency.info(emergency.getLang().conflictingPlaceholders+ c, Level.SEVERE);
                continue;
            }
            String playerCommand = c.replace("%player%", player.getName());
            if(c.contains("%player_in_world%")){
                for (Player p : playersInWorld) {
                    playerCommand = playerCommand.replace("%player%", p.getName());
                    // 執行命令
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), playerCommand);
                }
            }else if(c.contains("%online_player%")){
                for (Player p : Bukkit.getOnlinePlayers()) {
                    playerCommand = playerCommand.replace("%player%", p.getName());
                    // 執行命令
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), playerCommand);
                }
            }
            else{
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), playerCommand);
            }
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

    // Getter for onJoinCommand
    public List<String> getOnJoinCommand() {
        if (onJoinCommand == null) {
            return Collections.emptyList();
        }
        return onJoinCommand;
    }

    // Getter for onQuitCommand
    public List<String> getOnQuitCommand() {
        if (onQuitCommand == null) {
            return Collections.emptyList();
        }
        return onQuitCommand;
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
