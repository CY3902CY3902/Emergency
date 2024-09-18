package io.github.cy3902.emergency;

import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsSQL;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.api.MythicMobsEventListener;
import io.github.cy3902.emergency.command.Commands;
import io.github.cy3902.emergency.dao.DayWorldDAO;
import io.github.cy3902.emergency.dao.ShutdownDAO;
import io.github.cy3902.emergency.dao.TimeWorldDAO;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;

/**
 * 主插件類，負責插件的啟用和禁用，以及事件處理。
 */
public final class Emergency extends JavaPlugin implements Listener {

    private final MsgUtils msgUtils = new MsgUtils(this);

    private static Emergency emergency;

    private static Lang lang;
    private static String DATABASE_URL;
    private static Lang.LangType langType;

    private static EmergencyConfig emergencyConfig;
    private static WorldConfig worldConfig;
    private static ConfigFile configFile;

    private static EmergencyManager emergencyManager;
    private static WorldManager worldManager;
    private static TaskManager taskManager;

    private static AbstractsSQL sql;
    private static LocalDateTime lastShutdownTime;

    private static ShutdownDAO shutdownDAO;
    private static DayWorldDAO dayWorldDAO;
    private static TimeWorldDAO timeWorldDAO;

    /**
     * 插件啟用時呼叫，初始化插件核心組件和註冊事件。
     */
    @Override
    public void onEnable() {
        emergency = this;
        try {
            initEssential();
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "初始化核心組件失敗", e);
        }

        Plugin mythicMobs = Bukkit.getPluginManager().getPlugin("MythicMobs");
        if (mythicMobs != null && mythicMobs.isEnabled()) {
            registerMythicMobsEvents();
        }
        getServer().getPluginManager().registerEvents(this, this);
    }

    /**
     * 註冊 MythicMobs 事件。
     */
    private void registerMythicMobsEvents() {
        getServer().getPluginManager().registerEvents(new MythicMobsEventListener(), this);
    }

    /**
     * 初始化插件所需的核心組件和文件夾。
     *
     * @throws IOException 如果初始化過程中發生 IO 錯誤
     */
    public void initEssential() throws IOException {
        emergencyManager = new EmergencyManager();
        worldManager = new WorldManager();
        taskManager = new TaskManager();

        File eventFolder = new File(getDataFolder(), "Event");
        File worldFolder = new File(getDataFolder(), "World");
        File langFolder = new File(getDataFolder(), "Lang");
        File sqlFolder = new File(getDataFolder(), "SQL");

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
        if (!sqlFolder.exists()) {
            sqlFolder.mkdirs();
        }

        lang = null;
        configFile = new ConfigFile("plugins/Emergency","Emergency/", "config.yml");
        shutdownDAO = new ShutdownDAO(sql);
        dayWorldDAO = new DayWorldDAO(sql);
        timeWorldDAO = new TimeWorldDAO(sql);
        lang = new Lang("plugins/Emergency/Lang", "Lang/", langType + ".yml");
        lastShutdownTime = shutdownDAO.readLastShutdownTime();

        worldManager.clear();
        emergencyManager.clear();
        emergencyConfig = new EmergencyConfig("plugins/Emergency/Event");
        worldConfig = new WorldConfig("plugins/Emergency/World");

        Bukkit.getPluginCommand("emergency").setExecutor(new Commands());
        Bukkit.getPluginCommand("emergency").setTabCompleter(new Commands());
        Commands.register();
    }

    /**
     * 插件禁用時呼叫，處理插件關閉邏輯。
     */
    @Override
    public void onDisable() {
        getLogger().info(lang.pluginDisable);
        LocalDateTime shutdownTime = LocalDateTime.now();
        WorldManager.allWorldPause();
        WorldManager.allEmergencyStop();
        WorldManager.allRunningEmergencySave();
        shutdownDAO.saveShutdownTime(shutdownTime);
        sql.close();
    }

    /**
     * 處理玩家加入世界事件。
     *
     * @param event 玩家加入事件
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String worldName = event.getPlayer().getWorld().getName();
        onJoinWorldEvents(worldName, event.getPlayer());
    }

    /**
     * 處理玩家退出世界事件。
     *
     * @param event 玩家退出事件
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String worldName = event.getPlayer().getWorld().getName();
        onQuitWorldEvents(worldName, event.getPlayer());
    }

    /**
     * 處理玩家變更世界事件。
     *
     * @param event 玩家變更世界事件
     */
    @EventHandler
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String fromWorldName = event.getFrom().getName();
        String toWorldName = player.getWorld().getName();

        onQuitWorldEvents(fromWorldName, event.getPlayer());
        onJoinWorldEvents(toWorldName, event.getPlayer());
    }

    /**
     * 處理玩家進入世界的事件。
     *
     * @param worldName 世界名稱
     * @param player 玩家實例
     */
    private void onJoinWorldEvents(String worldName, Player player) {
        if (!WorldManager.getWorldNameList().contains(worldName)) {
            return;
        }
        Set<AbstractsEmergency> abstractsEmergencyList;
        for (AbstractsWorld abstractsWorld : WorldManager.getWorldList()) {
            abstractsEmergencyList = new HashSet<>(abstractsWorld.getWorldEmergency().values());
            for (AbstractsEmergency abstractsEmergency : abstractsEmergencyList) {
                BossBar bossBar = abstractsEmergency.getBossBar();
                if (bossBar != null) {
                    bossBar.addPlayer(player);
                    abstractsEmergency.OnJoinCommand(abstractsWorld, player);
                }
            }
        }
    }

    /**
     * 處理玩家離開世界的事件。
     *
     * @param worldName 世界名稱
     * @param player 玩家實例
     */
    private void onQuitWorldEvents(String worldName, Player player) {
        if (!WorldManager.getWorldNameList().contains(worldName)) {
            return;
        }
        Set<AbstractsEmergency> abstractsEmergencyList;
        for (AbstractsWorld abstractsWorld : WorldManager.getWorldList()) {
            abstractsEmergencyList = new HashSet<>(abstractsWorld.getWorldEmergency().values());
            for (AbstractsEmergency abstractsEmergency : abstractsEmergencyList) {
                BossBar bossBar = abstractsEmergency.getBossBar();
                if (bossBar != null) {
                    bossBar.removePlayer(player);
                    abstractsEmergency.OnQuitCommand(abstractsWorld, player);
                }
            }
        }
    }

    /**
     * 註冊一個新的緊急事件。
     *
     * @param abstractsEmergency 緊急事件實例
     */
    public void registerEmergency(AbstractsEmergency abstractsEmergency) {
        emergencyManager.add(abstractsEmergency);
    }

    /**
     * 註冊一個新的世界。
     *
     * @param abstractsWorld 世界實例
     */
    public void registerWorld(AbstractsWorld abstractsWorld) {
        worldManager.add(abstractsWorld);
    }

    /**
     * 輸出訊息到日誌。
     *
     * @param msg 訊息內容
     * @param level 訊息級別
     */
    public void info(String msg, Level level){
        getLogger().log(level,msg);
    }

    /**
     * 將訊息轉換為顏色訊息。
     *
     * @param msg 訊息內容
     * @return 處理過的顏色訊息
     */
    public String color(String msg){return msgUtils.msg(msg);}
    public List<String> color(List<String> msg){return msgUtils.msg(msg);}

    public static Emergency getInstance(){
        return emergency;
    }

    public static Lang getLang() {
        return lang;
    }

    public static EmergencyConfig getEmergencyConfig() {
        return emergencyConfig;
    }

    public static void setEmergencyConfig(EmergencyConfig emergencyConfig) {
        Emergency.emergencyConfig = emergencyConfig;
    }

    public static WorldConfig getWorldConfig() {
        return worldConfig;
    }

    public static void setWorldConfig(WorldConfig worldConfig) {
        Emergency.worldConfig = worldConfig;
    }

    public static ConfigFile getConfigFile() {
        return configFile;
    }

    public static void setConfigFile(ConfigFile configFile) {
        Emergency.configFile = configFile;
    }

    public static Lang.LangType getLangType() {
        return langType;
    }

    public static void setLangType(Lang.LangType langType) {
        Emergency.langType = langType;
    }

    public static EmergencyManager getEmergencyManager() {
        return emergencyManager;
    }

    public static void setEmergencyManager(EmergencyManager emergencyManager) {
        Emergency.emergencyManager = emergencyManager;
    }

    public static WorldManager getWorldManager() {
        return worldManager;
    }

    public static void setWorldManager(WorldManager worldManager) {
        Emergency.worldManager = worldManager;
    }

    public static TaskManager getTaskManager() {
        return taskManager;
    }

    public static void setTaskManager(TaskManager taskManager) {
        Emergency.taskManager = taskManager;
    }

    public static AbstractsSQL getSql() {
        return sql;
    }

    public static void setSql(AbstractsSQL sql) {
        Emergency.sql = sql;
    }

    public static LocalDateTime getLastShutdownTime() {
        return lastShutdownTime;
    }

    public static void setLastShutdownTime(LocalDateTime lastShutdownTime) {
        Emergency.lastShutdownTime = lastShutdownTime;
    }

    public static String getDatabaseUrl() {
        return DATABASE_URL;
    }

    public static void setDatabaseUrl(String databaseUrl) {
        DATABASE_URL = databaseUrl;
    }
    public static ShutdownDAO getShutdownDAO() {
        return shutdownDAO;
    }
    public static TimeWorldDAO getTimeWorldDAO() {
        return timeWorldDAO;
    }
    public static DayWorldDAO getDayWorldDAO() {
        return dayWorldDAO;
    }
}
