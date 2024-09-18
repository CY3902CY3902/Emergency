package io.github.cy3902.emergency.command;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsCommand;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.manager.EmergencyManager;
import io.github.cy3902.emergency.manager.TaskManager;
import io.github.cy3902.emergency.manager.WorldManager;
import io.github.cy3902.emergency.utils.Utils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 處理暫停緊急事件的指令。
 */
public class PauseCommand extends AbstractsCommand {

    /**
     * 初始化 PauseCommand 實例。
     */
    public PauseCommand() {
        super("emergency.pause", "pause", 3);
    }

    /**
     * 處理暫停指令。
     *
     * @param sender 指令發送者
     * @param args 指令參數
     */
    @Override
    public void handle(CommandSender sender, String[] args) {

        String group = args[1];
        String worldName = args[2];

        // 確保 group 參數不為 null
        if (group == null) {
            sender.sendMessage(lang.plugin + lang.emergencyNotFoundGroup);
            return;
        }

        // 獲取所有匹配的世界
        List<AbstractsWorld> worlds = WorldManager.getAllWorldByName(worldName);
        for (AbstractsWorld abstractsWorld : worlds) {
            processWorld(abstractsWorld, group, sender);
        }

        // 發送操作完成消息
        sender.sendMessage(lang.plugin + lang.pauseCommandComplete);
    }

    /**
     * 自動完成建議的生成方法。
     *
     * @param sender 指令發送者
     * @param args 指令參數
     * @return 自動完成選項列表
     */
    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        List<String> suggestions = new ArrayList<>();

        // 根據參數長度和指令選項生成自動完成選項
        if (args.length == 2 && "pause".equalsIgnoreCase(args[0])) {
            return new CommandTabBuilder()
                    .addTab(new ArrayList<>(EmergencyManager.getAllGroups()), 1, Arrays.asList("pause"), 0)
                    .build(args);
        }

        if (args.length == 3 && "pause".equalsIgnoreCase(args[0])) {
            List<AbstractsWorld> abstractsWorldList = WorldManager.getAllWorldByGroup(args[1]);
            return new CommandTabBuilder()
                    .addTab(Utils.getAbstractsWorldListName(abstractsWorldList), 2, new ArrayList<>(EmergencyManager.getAllGroups()), 1)
                    .build(args);
        }

        return suggestions;
    }

    /**
     * 處理世界中的緊急事件暫停。
     *
     * @param abstractsWorld 處理的世界
     * @param group 緊急事件組
     * @param sender 指令發送者
     */
    public void processWorld(AbstractsWorld abstractsWorld, String group, CommandSender sender) {
        if (abstractsWorld == null) {
            return;
        }

        // 檢查緊急事件組是否已經暫停
        if (isGroupAlreadyPaused(abstractsWorld, group)) {
            sender.sendMessage(lang.plugin + abstractsWorld.getWorld().getName() + lang.groupAlreadyPaused);
        } else {
            abstractsWorld.pause(group);
        }
    }

    /**
     * 檢查緊急事件組是否已經暫停。
     *
     * @param abstractsWorld 處理的世界
     * @param group 緊急事件組
     * @return 是否已經暫停
     */
    private boolean isGroupAlreadyPaused(AbstractsWorld abstractsWorld, String group) {
        return abstractsWorld.getGroupStates().get(group) == TaskManager.TaskStatus.PAUSED;
    }
}
