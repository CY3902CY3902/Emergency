package io.github.cy3902.emergency.command;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsCommand;
import io.github.cy3902.emergency.files.Lang;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.*;

public class Commands implements CommandExecutor, TabCompleter {
    protected final Emergency emergency = Emergency.getInstance();
    protected final Lang lang = emergency.getLang();
    protected static LinkedHashMap<String, AbstractsCommand> commands = new LinkedHashMap<>();


    public static void register(){
        commands.put("reload",new ReloadCommand());
        commands.put("start", new StartCommand());
        commands.put("pause", new PauseCommand());
        commands.put("resume", new ResumeCommand());
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command command, @NotNull String s, @NotNull String[] strings) {
        int length = strings.length;
        if (length == 0){
            lang.helpPlayer.forEach(msg -> {
                commandSender.sendMessage(msg);
            });
        }
        else {
            String subCommandName = strings[0];
            HashMap<String,AbstractsCommand> map=commands;
            AbstractsCommand cmd = map.get(subCommandName);
            if (cmd == null){
                commandSender.sendMessage(lang.plugin+lang.unknownCommand);
                return false;
            }
            if (commandSender.hasPermission(cmd.getPermission())) {
                if (cmd.getLength().contains(length)) {
                    cmd.handle(commandSender, strings);
                    return true;
                }
            }
            else {
                commandSender.sendMessage(lang.plugin + lang.noPermission);
                return true;
            }
            commandSender.sendMessage(lang.plugin+lang.unknownCommand);
            return false;
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command command, @NotNull String s, @NotNull String[] strings) {
        int length = strings.length;
        List<String> tab=new ArrayList<>();
        if (length == 1 ){
            for (Map.Entry<String,AbstractsCommand> entry:commands.entrySet()){
                AbstractsCommand abstractsCommand=entry.getValue();
                if ((commandSender.isOp()) || (commandSender.hasPermission(abstractsCommand.getPermission()))){
                    tab.add(entry.getKey());
                }
            }
            return tab;
        }
        else {
            String subCommand = strings[0];
            HashMap<String,AbstractsCommand> map=commands;
            AbstractsCommand abstractsCommand = map.get(subCommand);
            if (abstractsCommand != null){
                if ((commandSender.isOp()) || (commandSender.hasPermission(abstractsCommand.getPermission()))){
                    return abstractsCommand.complete(commandSender,strings);
                }
            }
        }
        return null;
    }
}