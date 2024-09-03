package io.github.cy3902.emergency.command;

import io.github.cy3902.emergency.abstracts.AbstractsCommand;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.manager.TaskManager;
import io.github.cy3902.emergency.utils.WorldUtils;
import org.bukkit.command.CommandSender;

import javax.sound.sampled.LineUnavailableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class PauseCommand extends AbstractsCommand {
    public PauseCommand() {
        super("emergency.pause", "pause", 3);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {

        String group = args[1];
        String worldName = args[2];
        if(group == null) {
            sender.sendMessage(lang.plugin + lang.emergencyNotFoundGroup);
            return;
        }

        List<AbstractsWorld> worlds = emergency.getWorldManager().getAllWorldByName(worldName);
        for (AbstractsWorld abstractsWorld : worlds) {
            processWorld(abstractsWorld, group, sender);
        }

        sender.sendMessage(lang.plugin + lang.pauseCommandComplete);
        return;
    }


    public List<String> complete(CommandSender sender, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 2 && "pause".equalsIgnoreCase(args[0])) {
            return new CommandTabBuilder()
                    .addTab(new ArrayList<>(emergency.getEmergencyManager().getAllGroups()), 1, Arrays.asList("pause"), 0)
                    .build(args);
        }

        if (args.length == 3 && "pause".equalsIgnoreCase(args[0])) {
            List<AbstractsWorld> abstractsWorldList = emergency.getWorldManager().getAllWorldByGroup(args[1]);
            return new CommandTabBuilder()
                    .addTab(WorldUtils.getAbstractsWorldListName(abstractsWorldList), 2, new ArrayList<>(emergency.getEmergencyManager().getAllGroups()), 1)
                    .build(args);
        }

        return suggestions;
    }

    public void processWorld(AbstractsWorld abstractsWorld, String group, CommandSender sender) {
        if (abstractsWorld == null) {
            return;
        }

        if (isGroupAlreadyPaused(abstractsWorld, group)) {
            sender.sendMessage(lang.plugin+ abstractsWorld.getWorld().getName() + lang.groupAlreadyPaused);
        } else {
            abstractsWorld.pause(group);
        }
    }

    private boolean isGroupAlreadyPaused(AbstractsWorld abstractsWorld, String group) {
        return abstractsWorld.getGroupStates().get(group) == TaskManager.TaskStatus.PAUSED;
    }



}
