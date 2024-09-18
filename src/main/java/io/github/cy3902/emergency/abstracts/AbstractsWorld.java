package io.github.cy3902.emergency.abstracts;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.manager.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;



/**
 * 抽象類別，代表一個世界中的緊急事件管理系統。
 * 提供了管理和操作世界內緊急事件的基本功能。
 */
public abstract class AbstractsWorld {

    protected final Emergency emergency;
    protected World world;
    protected int day;
    protected Map<String, AbstractsEmergency> worldEmergency = new HashMap<>();
    protected Map<String, TaskManager.TaskStatus> groupStates = new HashMap<>();

    /**
     * 建立 AbstractsWorld 物件。
     *
     * @param worldName 要初始化的世界名稱
     * @throws SQLException 如果初始化過程中發生 SQL 錯誤，則拋出該異常
     */
    protected AbstractsWorld(String worldName) {
        this.emergency = Emergency.getInstance();
        this.world = Bukkit.getWorld(worldName);
        this.day = 0;

        if (this.world == null) {
            emergency.info(Emergency.getLang().plugin + Emergency.getLang().worldNotFoundMessage, Level.SEVERE);
            return;
        }
    }

    /**
     * 隨機選擇一個緊急事件並執行。
     * 具體實現由子類別定義。
     *
     * @throws SQLException 如果隨機選擇過程中發生 SQL 錯誤，則拋出該異常
     */
    public void randomEmergency() throws SQLException {}

    /**
     * 開始指定組的緊急事件。
     *
     * @param group 指定的組名稱
     * @param abstractsEmergency 要啟動的緊急事件
     */
    public void startEmergency(String group, AbstractsEmergency abstractsEmergency) {}

    /**
     * 暫停指定組的緊急事件。
     *
     * @param group 指定的組名稱
     */
    public void pause(String group) {}

    /**
     * 恢復指定組的緊急事件。
     *
     * @param group 指定的組名稱
     */
    public void resume(String group) {}


    public World getWorld() {
        return world;
    }


    public int getDay() {
        return day;
    }


    public Map<String, AbstractsEmergency> getWorldEmergency() {
        return worldEmergency;
    }


    public Map<String, TaskManager.TaskStatus> getGroupStates() {
        return groupStates;
    }
}