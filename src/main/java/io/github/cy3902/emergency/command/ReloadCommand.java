package io.github.cy3902.emergency.command;


import io.github.cy3902.emergency.abstracts.AbstractsCommand;

import org.bukkit.command.CommandSender;

import java.time.LocalDateTime;
import java.util.List;


public class ReloadCommand extends AbstractsCommand {


    public ReloadCommand() {
        super("emergency.reload", "position", 1 );
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        try {
            emergency.getWorldManager().allWorldStop();
            emergency.getWorldManager().allEmergencyStop();
            emergency.getWorldManager().allRunningEmergencySave();
            emergency.getShutdownDAO().saveShutdownTime(LocalDateTime.now());
            emergency.initEssential();
            sender.sendMessage(lang.plugin + lang.reload);
        }catch (Exception e){
            sender.sendMessage(lang.plugin + lang.reloadError);
            sender.sendMessage(String.valueOf(e));
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }


}
