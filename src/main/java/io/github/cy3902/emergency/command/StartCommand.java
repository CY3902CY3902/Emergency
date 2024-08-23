package io.github.cy3902.emergency.command;

import io.github.cy3902.emergency.abstracts.AbstractsCommand;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.emergency.DayEmergency;
import io.github.cy3902.emergency.emergency.TimeEmergency;
import io.github.cy3902.emergency.utils.EmergencyUtils;
import io.github.cy3902.emergency.utils.WorldUtils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

        AbstractsEmergency abstractsEmergency = EmergencyUtils.findEmergencyByName(args[2]);
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
                    .addTab(EmergencyUtils.getAllGroups(), 1, Arrays.asList("start"), 0)
                    .build(args);
        }

        if (args.length == 3 && "start".equalsIgnoreCase(args[0])) {
            List<AbstractsEmergency> abstractsEmergencies = EmergencyUtils.getAllEmergencyByGroup(args[1]);
            if (abstractsEmergencies.isEmpty()) {
                return suggestions;
            }

            List<String> abstractsEmergenceNames = abstractsEmergencies.stream()
                    .map(AbstractsEmergency::getName)
                    .collect(Collectors.toList());

            return new CommandTabBuilder()
                    .addTab(abstractsEmergenceNames, 2, EmergencyUtils.getAllGroups(), 1)
                    .build(args);
        }
        if (args.length == 4 && "start".equalsIgnoreCase(args[0])) {

            return new CommandTabBuilder()
                    .addTab(WorldUtils.getAllWorld(), 3, EmergencyUtils.getAllGroups(), 1)
                    .build(args);
        }

        return suggestions;
    }

    private AbstractsWorld getCorrespondingWorld(AbstractsEmergency abstractsEmergency, String worldName) {
        if (abstractsEmergency instanceof TimeEmergency) {
            return emergency.getEmergencyTimeWorld().get(worldName);
        } else if (abstractsEmergency instanceof DayEmergency) {
            return emergency.getEmergencyDayWorld().get(worldName);
        }
        return null;
    }


}
