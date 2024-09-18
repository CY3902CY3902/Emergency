package io.github.cy3902.emergency.command;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsCommand;
import io.github.cy3902.emergency.files.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.*;

/**
 * 處理插件指令的類別。
 * 實現了 CommandExecutor 和 TabCompleter 介面，以處理指令執行和自動完成。
 */
public class Commands implements CommandExecutor, TabCompleter {

    protected final Emergency emergency = Emergency.getInstance();
    protected final Lang lang = Emergency.getLang();
    protected static LinkedHashMap<String, AbstractsCommand> commands = new LinkedHashMap<>();

    /**
     * 註冊所有支持的指令及其處理類別。
     */
    public static void register() {
        commands.put("reload", new ReloadCommand());
        commands.put("start", new StartCommand());
        commands.put("pause", new PauseCommand());
        commands.put("resume", new ResumeCommand());
    }

    /**
     * 處理指令執行。
     * 根據指令名稱和參數調用對應的處理類別，並檢查權限。
     *
     * @param commandSender 指令發送者
     * @param command 指令
     * @param s 指令名稱
     * @param strings 指令參數
     * @return 是否成功處理指令
     */
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        int length = strings.length;
        if (length == 0) {
            // 顯示幫助訊息
            lang.helpPlayer.forEach(commandSender::sendMessage);
        } else {
            String subCommandName = strings[0];
            AbstractsCommand cmd = commands.get(subCommandName);
            if (cmd == null) {
                commandSender.sendMessage(lang.plugin + lang.unknownCommand);
                return false;
            }
            if (commandSender.hasPermission(cmd.getPermission())) {
                if (cmd.getLength().contains(length)) {
                    cmd.handle(commandSender, strings);
                    return true;
                }
            } else {
                commandSender.sendMessage(lang.plugin + lang.noPermission);
                return true;
            }
            commandSender.sendMessage(lang.plugin + lang.unknownCommand);
            return false;
        }
        return false;
    }

    /**
     * 提供指令的自動完成選項。
     * 根據指令參數和權限提供適當的選項。
     *
     * @param commandSender 指令發送者
     * @param command 指令
     * @param s 指令名稱
     * @param strings 指令參數
     * @return 自動完成選項
     */
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        int length = strings.length;
        List<String> tab = new ArrayList<>();
        if (length == 1) {
            for (Map.Entry<String, AbstractsCommand> entry : commands.entrySet()) {
                AbstractsCommand abstractsCommand = entry.getValue();
                if ((commandSender.isOp()) || (commandSender.hasPermission(abstractsCommand.getPermission()))) {
                    tab.add(entry.getKey());
                }
            }
            return tab;
        } else {
            String subCommand = strings[0];
            AbstractsCommand abstractsCommand = commands.get(subCommand);
            if (abstractsCommand != null) {
                if ((commandSender.isOp()) || (commandSender.hasPermission(abstractsCommand.getPermission()))) {
                    return abstractsCommand.complete(commandSender, strings);
                }
            }
        }
        return null;
    }
}