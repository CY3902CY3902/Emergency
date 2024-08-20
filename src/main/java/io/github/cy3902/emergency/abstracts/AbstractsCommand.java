package io.github.cy3902.emergency.abstracts;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.files.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.*;

public abstract class AbstractsCommand {
    protected final Emergency emergency = Emergency.getInstance();
    protected final Lang lang = emergency.getLang();
    protected String permission;
    protected String command;
    protected List<Integer> length;

    public AbstractsCommand(String permission, String command, int length) {
        this.permission = permission;
        this.command = command;
        this.length = Collections.singletonList(length);
    }
    public AbstractsCommand(String permission, String command, List<Integer> length) {
        this.permission = permission;
        this.command = command;
        this.length = length;
    }


    public OfflinePlayer getPlayer(String name) {
        OfflinePlayer p = Bukkit.getPlayerExact(name);
        if (p == null) {
            // Not the best option, but Spigot doesn't offer a good replacement (as usual)
            p = Bukkit.getOfflinePlayer(name);
            return p.hasPlayedBefore() ? p : null;
        }
        return p;
    }


    public abstract void handle(CommandSender sender,String[] args);
    public abstract List<String> complete(CommandSender sender,String[] args);

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

