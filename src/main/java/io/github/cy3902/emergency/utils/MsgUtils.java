package io.github.cy3902.emergency.utils;

import io.github.cy3902.emergency.Emergency;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * 用於處理訊息相關操作的工具類別。
 */
public class MsgUtils {
    private final Emergency emergency;

    /**
     * 建構函數，初始化 MsgUtils 實例。
     *
     * @param emergency Emergency 實例
     */
    public MsgUtils(Emergency emergency) {
        this.emergency = emergency;
    }

    /**
     * 向指定玩家發送訊息。
     *
     * @param playerName 玩家名稱
     * @param msg        要發送的訊息
     */
    public void sendMessage(String playerName, String msg) {
        Player p = Bukkit.getPlayerExact(playerName);
        if (p != null) {
            p.sendMessage(msg(msg));
            return;
        }
    }

    /**
     * 將訊息文本轉換為顯示格式的訊息。
     *
     * @param msg 要轉換的訊息
     * @return 轉換後的訊息
     */
    public String msg(String msg) {
        return tm(msg);
    }

    /**
     * 將文本中的顏色代碼（&）轉換為 Minecraft 顏色代碼（§）。
     *
     * @param textToColor 要轉換顏色的文本
     * @return 轉換後的文本
     */
    private String tm(String textToColor) {
        if (textToColor == null) {
            return null;
        }
        return textToColor.replace("&", "§");
    }

    /**
     * 將訊息列表中的每一條訊息轉換為顯示格式的訊息。
     *
     * @param msg 訊息列表
     * @return 轉換後的訊息列表
     */
    public List<String> msg(List<String> msg) {
        List<String> lore = new ArrayList<>();
        for (String l : msg) {
            lore.add(tm(l));
        }
        return lore;
    }

    /**
     * 去除顏色代碼後的多餘空格。
     *
     * @param textToStrip 要處理的文本
     * @return 處理後的文本
     */
    private String stripSpaceAfterColorCodes(String textToStrip) {
        textToStrip = textToStrip.replaceAll("(" + ChatColor.COLOR_CHAR + ".)[\\s]", "$1");
        return textToStrip;
    }
}