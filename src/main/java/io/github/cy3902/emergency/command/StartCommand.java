package io.github.cy3902.emergency.command;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsCommand;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.emergency.DayEmergency;
import io.github.cy3902.emergency.emergency.TimeEmergency;
import io.github.cy3902.emergency.manager.EmergencyManager;
import io.github.cy3902.emergency.manager.WorldManager;
import io.github.cy3902.emergency.world.DayWorld;
import io.github.cy3902.emergency.world.TimeWorld;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 處理啟動緊急事件的指令。
 */
public class StartCommand extends AbstractsCommand {

    /**
     * 初始化 StartCommand 實例。
     */
    public StartCommand() {
        super("emergency.start", "start", 4);
    }

    /**
     * 處理啟動指令。
     *
     * @param sender 指令發送者
     * @param args 指令參數
     */
    @Override
    public void handle(CommandSender sender, String[] args) {
        // 取得參數中的組別名稱
        String group = args[1];
        if (group == null) {
            sender.sendMessage(lang.plugin + lang.emergencyNotFoundGroup);
            return;
        }

        // 根據緊急事件名稱查找緊急事件
        AbstractsEmergency abstractsEmergency = EmergencyManager.findEmergencyByName(args[2]);
        if (abstractsEmergency == null) {
            sender.sendMessage(lang.plugin + lang.emergencyNotFoundMessage + args[2]);
            return;
        }

        // 根據世界名稱取得對應的世界
        AbstractsWorld abstractsWorld = getCorrespondingWorld(abstractsEmergency, args[3]);
        if (abstractsWorld == null) {
            sender.sendMessage(lang.plugin + lang.worldNotFoundMessage + args[3]);
            return;
        }

        // 在世界中啟動緊急事件
        abstractsWorld.startEmergency(group, abstractsEmergency);

        // 發送指令完成消息
        sender.sendMessage(lang.plugin + lang.startCommandComplete);
    }

    /**
     * 提供自動完成建議。
     *
     * @param sender 指令發送者
     * @param args 指令參數
     * @return 自動完成選項列表
     */
    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 2) {
            // 提供組別名稱的自動完成選項
            return new CommandTabBuilder()
                    .addTab(new ArrayList<>(EmergencyManager.getAllGroups()), 1, Arrays.asList("start"), 0)
                    .build(args);
        }

        if (args.length == 3 && "start".equalsIgnoreCase(args[0])) {
            // 根據組別提供緊急事件名稱的自動完成選項
            List<AbstractsEmergency> abstractsEmergencies = EmergencyManager.getAllEmergencyByGroup(args[1]);
            if (abstractsEmergencies.isEmpty()) {
                return suggestions;
            }

            List<String> abstractsEmergencyNames = abstractsEmergencies.stream()
                    .map(AbstractsEmergency::getName)
                    .collect(Collectors.toList());

            return new CommandTabBuilder()
                    .addTab(abstractsEmergencyNames, 2, new ArrayList<>(EmergencyManager.getAllGroups()), 1)
                    .build(args);
        }

        if (args.length == 4 && "start".equalsIgnoreCase(args[0])) {
            // 提供世界名稱的自動完成選項
            return new CommandTabBuilder()
                    .addTab(WorldManager.getWorldNameList(), 3, new ArrayList<>(EmergencyManager.getAllGroups()), 1)
                    .build(args);
        }

        return suggestions;
    }

    /**
     * 根據緊急事件和世界名稱取得對應的世界。
     *
     * @param abstractsEmergency 緊急事件
     * @param worldName 世界名稱
     * @return 對應的世界，如果找不到則返回 null
     */
    private AbstractsWorld getCorrespondingWorld(AbstractsEmergency abstractsEmergency, String worldName) {
        List<AbstractsWorld> abstractsWorldList = WorldManager.getAllWorldByName(worldName);
        for (AbstractsWorld abstractsWorld : abstractsWorldList) {
            // 根據緊急事件和世界的類型匹配
            if (abstractsWorld instanceof TimeWorld && abstractsEmergency instanceof TimeEmergency) {
                return abstractsWorld;
            }
            if (abstractsWorld instanceof DayWorld && abstractsEmergency instanceof DayEmergency) {
                return abstractsWorld;
            }
        }
        return null;
    }
}
