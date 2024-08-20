package io.github.cy3902.emergency;

import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.command.Commands;
import io.github.cy3902.emergency.emergency.DayEmergency;
import io.github.cy3902.emergency.emergency.TimeEmergency;
import io.github.cy3902.emergency.files.ConfigFile;
import io.github.cy3902.emergency.files.EmergencyConfig;
import io.github.cy3902.emergency.files.Lang;
import io.github.cy3902.emergency.files.WorldConfig;
import io.github.cy3902.emergency.task.TaskManager;
import io.github.cy3902.emergency.utils.EmergencyUtils;
import io.github.cy3902.emergency.utils.FileUtils;
import io.github.cy3902.emergency.utils.MsgUtils;


import io.github.cy3902.emergency.utils.WorldUtils;
import io.github.cy3902.emergency.world.DayWorld;
import io.github.cy3902.emergency.world.TimesWorld;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public final class Emergency extends JavaPlugin implements Listener {


    private MsgUtils msgUtils = new MsgUtils(this);

    private static Emergency emergency;

    //語系
    private Lang lang;
    private EmergencyConfig emergencyConfig;
    private WorldConfig worldConfig;


    public ConfigFile getConfigFile() {
        return configFile;
    }

    private ConfigFile configFile;

    private Lang.LangType langType;

    //世界註冊
    private static Map<String, AbstractsWorld> emergencyTimeWorld = new HashMap<>();
    private static Map<String, AbstractsWorld> emergencyDayWorld = new HashMap<>();
    //時間週期的緊急事件註冊
    private static Map<String, List<AbstractsEmergency>> emergencyTimeGroup = new HashMap<>();
    //世界天數週期的緊急事件註冊
    private static Map<String, List<AbstractsEmergency>> emergencyDayGroup = new HashMap<>();


    //
    private static final TaskManager taskManager = new TaskManager();

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
        saveDefaultConfig();
        //監聽
        getServer().getPluginManager().registerEvents(this, this);


    }

    public void initEssential() throws IOException {
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

        //世界註冊
        this.emergencyTimeWorld = new HashMap<>();
        this.emergencyDayWorld = new HashMap<>();
        //時間週期的緊急事件註冊
        this.emergencyTimeGroup = new HashMap<>();
        //世界天數週期的緊急事件註冊
        this.emergencyDayGroup = new HashMap<>();
        this.lang = null;
        this.configFile = new ConfigFile("plugins/Emergency","Emergency/", "config.yml");
        this.lang = new Lang("plugins/Emergency/Lang", "Lang/", this.langType + ".yml");
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
        WorldUtils.allWorldStop();
        EmergencyUtils.allEmergencyStop();
    }



    @EventHandler
    public void onMythicConditionLoad(MythicConditionLoadEvent event) {
        if(event.getConditionName().equalsIgnoreCase("Emergency"))	{
            MythicLineConfig config = event.getConfig();
            String argument = config.getString("name", "default");
            event.register(new Condition( event.getConditionName(), argument, event.getConfig()));
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String worldName = event.getPlayer().getWorld().getName();
        onJoinWorldEvents(emergencyDayWorld, worldName, event.getPlayer());
        onJoinWorldEvents(emergencyTimeWorld, worldName, event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String worldName = event.getPlayer().getWorld().getName();
        onQuitWorldEvents(emergencyDayWorld, worldName, event.getPlayer());
        onQuitWorldEvents(emergencyTimeWorld, worldName, event.getPlayer());
    }

    @EventHandler
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String fromWorldName = event.getFrom().getName();
        String toWorldName = player.getWorld().getName();

        onQuitWorldEvents(emergencyDayWorld, fromWorldName, event.getPlayer());
        onJoinWorldEvents(emergencyTimeWorld, fromWorldName, event.getPlayer());

        onQuitWorldEvents(emergencyDayWorld, toWorldName, event.getPlayer());
        onJoinWorldEvents(emergencyTimeWorld, toWorldName, event.getPlayer());
    }

    private void onJoinWorldEvents(Map<String, AbstractsWorld> abstractsWorldMap, String worldName, Player player) {
        if (abstractsWorldMap.containsKey(worldName)) {
            List<AbstractsEmergency> worldEvents = abstractsWorldMap.get(worldName).getWorldEmergency();
            if (worldEvents.isEmpty()) {
                return;
            }
            for (AbstractsEmergency abstractsEmergency : worldEvents) {
                BossBar bossBar = abstractsEmergency.getBossBar();
                if (bossBar != null) {
                    bossBar.addPlayer(player);
                    abstractsEmergency.OnJoinCommand(player);
                }
            }
        }
    }

    private void onQuitWorldEvents(Map<String, AbstractsWorld> abstractsWorldMap, String worldName, Player player) {
        if (abstractsWorldMap.containsKey(worldName)) {
            List<AbstractsEmergency> worldEvents = abstractsWorldMap.get(worldName).getWorldEmergency();
            if (worldEvents.isEmpty()) {
                return;
            }
            for (AbstractsEmergency abstractsEmergency : worldEvents) {
                BossBar bossBar = abstractsEmergency.getBossBar();
                if (bossBar != null) {
                    bossBar.removePlayer(player);
                    abstractsEmergency.OnQuitCommand(player);
                }
            }
        }
    }

    public void registerEmergency(AbstractsEmergency abstractsEmergency){
        for (String g : abstractsEmergency.getGroup()) {
            if(EmergencyUtils.findEmergencyByName(abstractsEmergency.getName()) != null){
                emergency.info(emergency.getLang().duplicateEmergencyMessage+ abstractsEmergency.getName(), Level.SEVERE);
                continue;
            }
            if (EmergencyUtils.checkForDuplicateGroup(abstractsEmergency)) {
                emergency.info(emergency.getLang().duplicateGroupName + g, Level.SEVERE);
                continue;
            }
            if (abstractsEmergency instanceof DayEmergency) {
                emergencyDayGroup.computeIfAbsent(g, k -> new ArrayList<>()).add(abstractsEmergency);
            }
            if (abstractsEmergency instanceof TimeEmergency) {
                emergencyTimeGroup.computeIfAbsent(g, k -> new ArrayList<>()).add(abstractsEmergency);
            }
        }
    }


    public void registerWorld(String name,List<String> dayGroup, List<String> timeGroup){
        TimesWorld timesWorld = new TimesWorld(name, timeGroup);
        DayWorld dayWorld = new DayWorld(name, dayGroup);
        emergencyDayWorld.put(name,dayWorld);
        emergencyTimeWorld.put(name,timesWorld);
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

    public static Map<String, List<AbstractsEmergency>> getEmergencyTimeGroup() {
        return Collections.unmodifiableMap(emergencyTimeGroup);
    }

    public static Map<String, List<AbstractsEmergency>> getEmergencyDayGroup() {
        return Collections.unmodifiableMap(emergencyDayGroup);
    }


    public static Map<String, AbstractsWorld> getEmergencyTimeWorld() {
        return emergencyTimeWorld;
    }

    public static Map<String, AbstractsWorld> getEmergencyDayWorld() {
        return emergencyDayWorld;
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



}
