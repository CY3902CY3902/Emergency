package io.github.cy3902.emergency;

import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.api.MythicMobsEventListener;
import io.github.cy3902.emergency.command.Commands;
import io.github.cy3902.emergency.files.ConfigFile;
import io.github.cy3902.emergency.files.EmergencyConfig;
import io.github.cy3902.emergency.files.Lang;
import io.github.cy3902.emergency.files.WorldConfig;
import io.github.cy3902.emergency.manager.EmergencyManager;
import io.github.cy3902.emergency.manager.TaskManager;
import io.github.cy3902.emergency.manager.WorldManager;
import io.github.cy3902.emergency.utils.FileUtils;
import io.github.cy3902.emergency.utils.MsgUtils;


import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;


import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public final class Emergency extends JavaPlugin implements Listener {


    private MsgUtils msgUtils = new MsgUtils(this);

    private static Emergency emergency;
    private Lang lang;
    private EmergencyConfig emergencyConfig ;
    private WorldConfig worldConfig;
    private ConfigFile configFile;
    private Lang.LangType langType;
    private static EmergencyManager emergencyManager;
    private static WorldManager worldManager;
    private static TaskManager taskManager;


    public Emergency() {
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        this.emergency = this;
        try {
            initEssential();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Plugin mythicMobs = Bukkit.getPluginManager().getPlugin("MythicMobs");
        if (mythicMobs != null && mythicMobs.isEnabled()) {
            registerMythicMobsEvents();
        }
        getServer().getPluginManager().registerEvents(this, this);

    }

    private void registerMythicMobsEvents() {
        getServer().getPluginManager().registerEvents(new MythicMobsEventListener(), this);
    }


    public void initEssential() throws IOException {
        emergencyManager = new EmergencyManager();
        worldManager = new WorldManager();
        taskManager = new TaskManager();
        File eventFolder = new File(getDataFolder(), "Event");
        File worldFolder = new File(getDataFolder(), "World");
        File langFolder = new File(getDataFolder(), "Lang");

        if (!eventFolder.exists()) {
            eventFolder.mkdirs();
            FileUtils.copyResourceFolder(this, "Emergency/Event", eventFolder);
        }
        if (!worldFolder.exists()) {
            worldFolder.mkdirs();
            FileUtils.copyResourceFolder(this, "Emergency/World", worldFolder);
        }
        if (!langFolder.exists()) {
            langFolder.mkdirs();
            FileUtils.copyResourceFolder(this, "Emergency/Lang", langFolder);
        }

        this.lang = null;

        this.configFile = new ConfigFile("plugins/Emergency","Emergency/", "config.yml");
        this.lang = new Lang("plugins/Emergency/Lang", "Lang/", this.langType + ".yml");
        this.worldManager.clear();
        this.emergencyManager.clear();
        this.emergencyConfig = new EmergencyConfig("plugins/Emergency/Event");
        this.worldConfig = new WorldConfig("plugins/Emergency/World");
        Bukkit.getPluginCommand("emergency").setExecutor(new Commands());
        Bukkit.getPluginCommand("emergency").setTabCompleter(new Commands());
        Commands.register();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Emergency.this.getLogger().info(lang.pluginDisable);
        worldManager.allWorldStop();
        this.worldManager.allEmergencyStop();
    }



    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String worldName = event.getPlayer().getWorld().getName();
        onJoinWorldEvents(worldName, event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String worldName = event.getPlayer().getWorld().getName();
        onQuitWorldEvents(worldName, event.getPlayer());
        onQuitWorldEvents( worldName, event.getPlayer());
    }

    @EventHandler
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String fromWorldName = event.getFrom().getName();
        String toWorldName = player.getWorld().getName();

        onQuitWorldEvents(fromWorldName, event.getPlayer());
        onJoinWorldEvents(fromWorldName, event.getPlayer());

        onQuitWorldEvents(toWorldName, event.getPlayer());
        onJoinWorldEvents(toWorldName, event.getPlayer());
    }

    private void onJoinWorldEvents( String worldName, Player player) {
        if (! worldManager.getWorldNameList().contains(worldName)) {
            return;
        }
        List<AbstractsEmergency> abstractsEmergencyList = new ArrayList<>();
        for(AbstractsWorld abstractsWorld : worldManager.getWorldList())   {
           abstractsEmergencyList = abstractsWorld.getWorldEmergency();
            for (AbstractsEmergency abstractsEmergency : abstractsEmergencyList) {
                BossBar bossBar = abstractsEmergency.getBossBar();
                if (bossBar != null) {
                    bossBar.addPlayer(player);
                    abstractsEmergency.OnJoinCommand(abstractsWorld,player);
                }
            }
        }
    }

    private void onQuitWorldEvents(String worldName, Player player) {
        if (! worldManager.getWorldNameList().contains(worldName)) {
            return;
        }
        List<AbstractsEmergency> abstractsEmergencyList = new ArrayList<>();
        for(AbstractsWorld abstractsWorld : worldManager.getWorldList())   {
            abstractsEmergencyList = abstractsWorld.getWorldEmergency();
            for (AbstractsEmergency abstractsEmergency : abstractsEmergencyList) {
                BossBar bossBar = abstractsEmergency.getBossBar();
                if (bossBar != null) {
                    bossBar.addPlayer(player);
                    abstractsEmergency.OnQuitCommand(abstractsWorld,player);
                }
            }
        }
    }

    public void registerEmergency(AbstractsEmergency abstractsEmergency){
        emergencyManager.add(abstractsEmergency);
    }



    public void registerWorld(AbstractsWorld abstractsWorld){
        worldManager.add(abstractsWorld);
    }

    public void info(String msg, Level level){
        getLogger().log(level,msg);
    }


    public String color(String msg){return msgUtils.msg(msg);}
    public List<String> color(List<String> msg){return msgUtils.msg(msg);}
    public static Emergency getInstance(){
        return emergency;
    }
    public Lang getLang() {
        return lang;
    }
    public void setLang(Lang lang) {
        this.lang = lang;
    }
    public void setLangType(Lang.LangType langType) {
        this.langType = langType;
    }
    public static TaskManager getTaskManager() {
        return taskManager;
    }
    public static EmergencyManager getEmergencyManager() {
        return emergencyManager;
    }
    public static WorldManager getWorldManager() {
        return worldManager;
    }
    public ConfigFile getConfigFile() {
        return configFile;
    }
}
