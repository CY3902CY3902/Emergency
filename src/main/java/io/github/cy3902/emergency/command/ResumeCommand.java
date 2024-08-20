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

public class ResumeCommand extends AbstractsCommand {

    public ResumeCommand() {
        super("emergency.resume", "resume", 3);
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


        sender.sendMessage(lang.plugin + lang.resumeCommandComplete);
        return;
    }

    public List<String> complete(CommandSender sender, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 2 && "resume".equalsIgnoreCase(args[0])) {
            return new CommandTabBuilder()
                    .addTab(EmergencyUtils.getAllGroups(), 1, Arrays.asList("resume"), 0)
                    .build(args);
        }

        if (args.length == 3 && "resume".equalsIgnoreCase(args[0])) {

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

        if (isGroupAlreadyRunning(abstractsWorld, group)) {
            sender.sendMessage(lang.plugin + abstractsWorld.getWorld().getName() +lang.groupAlreadyRunning);
        } else {
            abstractsWorld.resume(group);
        }
    }


    private boolean isGroupAlreadyRunning(AbstractsWorld abstractsWorld, String group) {
        return abstractsWorld.getGroupStates().get(group) == TaskManager.TaskStatus.RUNNING;
    }

    private List<AbstractsWorld> getWorldsToProcess(String worldName) {
        return Arrays.asList(
                emergency.getEmergencyTimeWorld().get(worldName),
                emergency.getEmergencyDayWorld().get(worldName)
        );
    }
}
