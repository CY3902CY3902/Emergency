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

/**
 * 負責讀取和處理緊急事件配置的類別。
 */
public class EmergencyConfig extends FileProviderList {

    /**
     * 構造函數，用於初始化 EmergencyConfig 對象。
     *
     * @param path 配置文件的路徑
     */
    public EmergencyConfig(String path) {
        super(path);
    }

    /**
     * 讀取預設的配置值並初始化緊急事件。
     *
     * @param yml YAML 配置對象
     */
    @Override
    protected void readDefault(YamlConfiguration yml) {
        Set<String> keys = this.yml.getKeys(false); // 獲取根鍵集合
        for (String key : keys) {
            this.readEmergency(key); // 讀取每個緊急事件的配置
        }
    }

    /**
     * 讀取指定名稱的緊急事件配置並創建相應的緊急事件對象。
     *
     * @param name 緊急事件的名稱
     */
    private void readEmergency(String name) {
        List<String> startCommand = getValue(this.yml, name + ".start_command", Arrays.asList("")); // 開始命令
        List<String> endCommand = getValue(this.yml, name + ".end_command", Arrays.asList("")); // 結束命令
        List<String> onJoinCommand = getValue(this.yml, name + ".on_join_command", Arrays.asList("")); // 玩家加入命令
        List<String> onQuitCommand = getValue(this.yml, name + ".on_quit_command", Arrays.asList("")); // 玩家退出命令
        List<String> group = getValue(this.yml, name + ".group", Arrays.asList("")); // 組
        String type = getValue(this.yml, name + ".type", "Time"); // 事件類型（默認為 Time）
        int days = getValue(this.yml, name + ".days", 0); // 事件持續的天數
        double chance = ((Number) getValue(this.yml, name + ".chance", 0.0)).doubleValue(); // 事件發生的機率
        long duration = getValue(this.yml, name + ".duration", 200); // 事件持續時間（秒）

        BossBar bossBar = createBossBar(name); // 創建 BossBar 對象

        AbstractsEmergency abstractsEmergency;
        AbstractsEmergency.EmergencyType emergencyType;

        // 根據事件類型創建相應的緊急事件對象
        try {
            emergencyType = AbstractsEmergency.EmergencyType.valueOf(type);
        } catch (IllegalArgumentException t) {
            // 如果類型無效，默認為 Time 類型
            emergencyType = AbstractsEmergency.EmergencyType.valueOf("Time");
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
        } else {
            return; // 如果類型不匹配，則不處理
        }

        emergency.registerEmergency(abstractsEmergency); // 註冊緊急事件
    }

    /**
     * 根據配置創建 BossBar 對象。
     *
     * @param name 緊急事件的名稱
     * @return 創建的 BossBar 對象，如果未啟用則返回 null
     */
    private BossBar createBossBar(String name) {
        boolean bossBarBool = getValue(this.yml, name + ".BossBar.enable", false); // 是否啟用 BossBar
        if (bossBarBool) {
            String bossBarColor = getValue(this.yml, name + ".BossBar.color", "RED"); // BossBar 顏色
            String bossBarTitle = getValue(this.yml, name + ".BossBar.title", "NULL"); // BossBar 標題
            String bossBarStyle = getValue(this.yml, name + ".BossBar.bar_style", "SOLID"); // BossBar 樣式
            return Bukkit.createBossBar(bossBarTitle, BarColor.valueOf(bossBarColor), BarStyle.valueOf(bossBarStyle)); // 創建 BossBar 對象
        }
        return null; // 如果未啟用 BossBar，返回 null
    }
}
