package io.github.cy3902.emergency.command;

import io.github.cy3902.emergency.abstracts.AbstractsCommand;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.emergency.DayEmergency;
import io.github.cy3902.emergency.emergency.TimeEmergency;
import io.github.cy3902.emergency.manager.TaskManager;
import io.github.cy3902.emergency.utils.Utils;
import io.github.cy3902.emergency.world.DayWorld;
import io.github.cy3902.emergency.world.TimeWorld;
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

        List<AbstractsWorld> worlds = emergency.getWorldManager().getAllWorldByName(worldName);

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
                    .addTab(new ArrayList<>(emergency.getEmergencyManager().getAllGroups()), 1, Arrays.asList("resume"), 0)
                    .build(args);
        }

        if (args.length == 3 && "resume".equalsIgnoreCase(args[0])) {
            List<AbstractsWorld> abstractsWorldList = emergency.getWorldManager().getAllWorldByGroup(args[1]);
            return new CommandTabBuilder()
                    .addTab(Utils.getAbstractsWorldListName(abstractsWorldList), 2, new ArrayList<>(emergency.getEmergencyManager().getAllGroups()), 1)
                    .build(args);
        }

        return suggestions;
    }

    public void processWorld(AbstractsWorld abstractsWorld, String group, CommandSender sender) {
        if (abstractsWorld == null) {
            return;
        }

        if (!isGroupAlreadyRunning(abstractsWorld, group)) {
            abstractsWorld.resume(group);
        }
    }


    private boolean isGroupAlreadyRunning(AbstractsWorld abstractsWorld, String group) {
        return abstractsWorld.getGroupStates().get(group) == TaskManager.TaskStatus.RUNNING;
    }


    private AbstractsWorld getCorrespondingWorld(AbstractsEmergency abstractsEmergency, String worldName) {
        List<AbstractsWorld> abstractsWorldList = emergency.getWorldManager().getAllWorldByName(worldName);
        for(AbstractsWorld abstractsWorld : abstractsWorldList){
            if(abstractsWorld instanceof TimeWorld && abstractsEmergency instanceof TimeEmergency){
            }
            if(abstractsWorld instanceof DayWorld && abstractsEmergency instanceof DayEmergency){
                return  abstractsWorld;
            }
        }
        return null;
    }
}
