package io.github.cy3902.emergency.command;

import io.github.cy3902.emergency.abstracts.AbstractsCommand;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.task.TaskManager;
import io.github.cy3902.emergency.utils.EmergencyUtils;
import io.github.cy3902.emergency.utils.WorldUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        List<AbstractsWorld> worlds = getWorldsToProcess(worldName);

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
                    .addTab(EmergencyUtils.getAllGroups(), 1, Arrays.asList("pause"), 0)
                    .build(args);
        }

        if (args.length == 3 && "pause".equalsIgnoreCase(args[0])) {

            return new CommandTabBuilder()
                    .addTab(WorldUtils.getAllWorldByGroup(args[1]), 2, EmergencyUtils.getAllGroups(), 1)
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


    private List<AbstractsWorld> getWorldsToProcess(String worldName) {
        return Arrays.asList(
                emergency.getEmergencyTimeWorld().get(worldName),
                emergency.getEmergencyDayWorld().get(worldName)
        );
    }
}
