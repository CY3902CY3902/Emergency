package io.github.cy3902.emergency.command;

import io.github.cy3902.emergency.abstracts.AbstractsCommand;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.emergency.DayEmergency;
import io.github.cy3902.emergency.emergency.TimeEmergency;
import io.github.cy3902.emergency.utils.WorldUtils;
import io.github.cy3902.emergency.world.DayWorld;
import io.github.cy3902.emergency.world.TimeWorld;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class StartCommand extends AbstractsCommand {
    public StartCommand() {
        super("emergency.start", "start", 4);
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        String group = args[1];
        if (group == null) {
            sender.sendMessage(lang.plugin + lang.emergencyNotFoundGroup);
            return;
        }

        AbstractsEmergency abstractsEmergency = emergency.getEmergencyManager().findEmergencyByName(args[2]);
        if (abstractsEmergency == null) {
            sender.sendMessage(lang.plugin + lang.emergencyNotFoundMessage + args[2]);
            return;
        }

        AbstractsWorld abstractsWorld = getCorrespondingWorld(abstractsEmergency, args[3]);
        if (abstractsWorld == null) {
            sender.sendMessage(lang.plugin + lang.worldNotFoundMessage + args[3]);
            return;
        }
        abstractsWorld.startEmergency(group, abstractsEmergency);

        sender.sendMessage(lang.plugin + lang.startCommandComplete);
    }


    public List<String> complete(CommandSender sender, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 2) {
            return new CommandTabBuilder()
                    .addTab(new ArrayList<>(emergency.getEmergencyManager().getAllGroups()), 1, Arrays.asList("start"), 0)
                    .build(args);
        }

        if (args.length == 3 && "start".equalsIgnoreCase(args[0])) {
            List<AbstractsEmergency> abstractsEmergencies = emergency.getEmergencyManager().getAllEmergencyByGroup(args[1]);
            if (abstractsEmergencies.isEmpty()) {
                return suggestions;
            }

            List<String> abstractsEmergenceNames = abstractsEmergencies.stream()
                    .map(AbstractsEmergency::getName)
                    .collect(Collectors.toList());

            return new CommandTabBuilder()
                    .addTab(abstractsEmergenceNames, 2,new ArrayList<>(emergency.getEmergencyManager().getAllGroups()), 1)
                    .build(args);
        }
        if (args.length == 4 && "start".equalsIgnoreCase(args[0])) {
            return new CommandTabBuilder()
                    .addTab(emergency.getWorldManager().getWorldNameList(), 3, new ArrayList<>(emergency.getEmergencyManager().getAllGroups()), 1)
                    .build(args);
        }

        return suggestions;
    }

    private AbstractsWorld getCorrespondingWorld(AbstractsEmergency abstractsEmergency, String worldName) {
        List<AbstractsWorld> abstractsWorldList = emergency.getWorldManager().getAllWorldByName(worldName);
        for(AbstractsWorld abstractsWorld : abstractsWorldList){
            if(abstractsWorld instanceof TimeWorld && abstractsEmergency instanceof TimeEmergency){
                return  abstractsWorld;
            }
            if(abstractsWorld instanceof DayWorld && abstractsEmergency instanceof DayEmergency){
                return  abstractsWorld;
            }
        }
        return null;
    }


}
