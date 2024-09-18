package io.github.cy3902.emergency.abstracts;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.files.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * AbstractsCommand 是抽象命令類別，為所有具體命令提供基本的行為和屬性定義。
 * 包含了處理指令發送者、權限檢查及命令長度限制等功能。
 * 此類主要用於擴展以創建具體命令實現。
 */
public abstract class AbstractsCommand {

    protected final Emergency emergency = Emergency.getInstance();
    protected final Lang lang = Emergency.getLang();
    protected String permission;
    protected String command;
    protected List<Integer> length;

    /**
     * 構造方法，用於初始化帶有單一長度要求的指令。
     *
     * @param permission 指令所需的權限
     * @param command    指令名稱
     * @param length     指令參數的長度
     */
    public AbstractsCommand(String permission, String command, int length) {
        this.permission = permission;
        this.command = command;
        this.length = Collections.singletonList(length);
    }

    /**
     * 構造方法，用於初始化帶有多個長度要求的指令。
     *
     * @param permission 指令所需的權限
     * @param command    指令名稱
     * @param length     指令參數長度的列表
     */
    public AbstractsCommand(String permission, String command, List<Integer> length) {
        this.permission = permission;
        this.command = command;
        this.length = length;
    }

    /**
     * 根據玩家名稱獲取玩家對象。如果玩家在線，返回在線玩家，否則嘗試返回離線玩家。
     *
     * @param name 玩家名稱
     * @return 對應的 OfflinePlayer 對象，如果玩家從未加入過伺服器，則返回 null。
     */
    public OfflinePlayer getPlayer(String name) {
        OfflinePlayer p = Bukkit.getPlayerExact(name);
        if (p == null) {
            p = Bukkit.getOfflinePlayer(name);
            return p.hasPlayedBefore() ? p : null;
        }
        return p;
    }

    /**
     * 處理指令邏輯的抽象方法，具體子類需實現此方法。
     *
     * @param sender 指令的發送者
     * @param args   指令的參數
     */
    public abstract void handle(CommandSender sender, String[] args);

    /**
     * 自動完成指令參數的抽象方法，具體子類需實現此方法。
     *
     * @param sender 指令的發送者
     * @param args   指令的參數
     * @return 可能的自動完成結果列表
     */
    public abstract List<String> complete(CommandSender sender, String[] args);

    public String getPermission() {
        return permission;
    }

    public String getCommand() {
        return command;
    }

    public List<Integer> getLength() {
        return length;
    }
}

