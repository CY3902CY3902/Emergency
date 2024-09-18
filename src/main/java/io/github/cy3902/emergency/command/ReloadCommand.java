package io.github.cy3902.emergency.command;


import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsCommand;

import io.github.cy3902.emergency.manager.WorldManager;
import org.bukkit.command.CommandSender;

import java.time.LocalDateTime;
import java.util.List;


/**
 * 處理重新加載緊急事件的指令。
 */
public class ReloadCommand extends AbstractsCommand {

    /**
     * 初始化 ReloadCommand 實例。
     */
    public ReloadCommand() {
        super("emergency.reload", "position", 1);
    }

    /**
     * 處理重新加載指令。
     *
     * @param sender 指令發送者
     * @param args 指令參數
     */
    @Override
    public void handle(CommandSender sender, String[] args) {
        try {
            // 停止所有世界中的緊急事件
            WorldManager.allWorldStop();

            // 停止所有緊急事件
            WorldManager.allEmergencyStop();

            // 保存所有運行中的緊急事件狀態
            WorldManager.allRunningEmergencySave();

            // 保存關機時間
            Emergency.getShutdownDAO().saveShutdownTime(LocalDateTime.now());

            // 重新初始化系統
            emergency.initEssential();

            // 發送重新加載成功消息
            sender.sendMessage(lang.plugin + lang.reload);
        } catch (Exception e) {
            // 發送重新加載錯誤消息
            sender.sendMessage(lang.plugin + lang.reloadError);
            // 發送異常詳情
            sender.sendMessage(String.valueOf(e));
        }
    }

    /**
     * 提供自動完成建議，此指令沒有自動完成建議。
     *
     * @param sender 指令發送者
     * @param args 指令參數
     * @return 自動完成選項列表，為 null
     */
    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }
}
