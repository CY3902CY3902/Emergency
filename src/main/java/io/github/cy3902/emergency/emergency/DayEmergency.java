package io.github.cy3902.emergency.emergency;


import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.manager.EmergencyManager;
import io.github.cy3902.emergency.world.DayWorld;
import org.bukkit.boss.BossBar;

import java.util.List;
import java.util.logging.Level;

/**
 * 處理基於天數的緊急事件。
 */
public class DayEmergency extends AbstractsEmergency {

    private String name;
    private boolean bossBarBool;
    private BossBar bossBar;
    private List<String> startCommand;
    private List<String> endCommand;
    private List<String> onJoinCommand;
    private List<String> onQuitCommand;
    private List<String> group;
    private double chance;
    private int days;
    private long duration;

    /**
     * 初始化 DayEmergency 實例。
     *
     * @param name 緊急事件名稱
     * @param bossBarBool 是否顯示 BossBar
     * @param bossBar BossBar 物件
     * @param startCommand 啟動指令列表
     * @param endCommand 結束指令列表
     * @param onJoinCommand 玩家進入事件時執行的指令列表
     * @param onQuitCommand 玩家退出事件時執行的指令列表
     * @param group 事件組別列表
     * @param chance 觸發機率
     * @param days 持續天數
     * @param duration 持續時間
     */
    public DayEmergency(
            String name,
            boolean bossBarBool,
            BossBar bossBar,
            List<String> startCommand,
            List<String> endCommand,
            List<String> onJoinCommand,
            List<String> onQuitCommand,
            List<String> group,
            double chance,
            int days,
            long duration
    ) {
        super(name);
        this.name = name;
        this.bossBar = bossBar;
        this.bossBarBool = bossBarBool;
        this.startCommand = startCommand;
        this.endCommand = endCommand;
        this.onJoinCommand = onJoinCommand;
        this.onQuitCommand = onQuitCommand;
        this.group = group;
        this.chance = chance;
        this.days = days;
        this.duration = duration;
    }

    /**
     * 啟動緊急事件。
     *
     * @param abstractsWorld 世界物件
     * @param group 組別
     */
    @Override
    public void start(AbstractsWorld abstractsWorld, String group) {
        if (abstractsWorld == null) {
            this.emergency.info(Emergency.getLang().worldNotFoundMessage + abstractsWorld.getWorld().getName(), Level.SEVERE);
            return;
        }
        DayWorld dayWorld = (DayWorld) abstractsWorld;
        delayDayAndStop(dayWorld, group);
        createBossBar(abstractsWorld);
        startCommand(abstractsWorld);
    }

    /**
     * 延遲天數並停止緊急事件。
     *
     * @param world DayWorld 物件
     * @param group 組別
     */
    protected void delayDayAndStop(DayWorld world, String group) {
        String taskId = "task-" + group + "-" + world.getWorld().getName();

        Runnable taskRunnable = new Runnable() {
            @Override
            public void run() {
                int daysRemaining = world.getGroupDayEnd().get(group) - world.getDay();
                long currentTime = world.getWorld().getFullTime() % (1200 * 20) + (days - daysRemaining) * 1200 * 20;
                double totalTime = 1200 * 20 * days;
                double progress = Math.max(0.0, 1.0 - (currentTime % totalTime) / totalTime);
                if (bossBar != null) {
                    bossBar.setProgress(progress);
                }

                if (world.getDay() >= world.getGroupDayEnd().get(group)) {
                    EmergencyManager.earlyStop(world, group);
                }
            }
        };
        Emergency.getTaskManager().startPeriodicTask(taskId, taskRunnable, 20L, 20L);
        this.activeTasks.put(taskId, Emergency.getTaskManager().getTask(taskId));
    }

    /**
     * 停止緊急事件。
     *
     * @param abstractsWorld 世界物件
     * @param group 組別
     */
    @Override
    public void stop(AbstractsWorld abstractsWorld, String group) {
        if (abstractsWorld == null) {
            this.emergency.info(Emergency.getLang().worldNotFoundMessage + abstractsWorld.getWorld().getName(), Level.SEVERE);
            return;
        }

        this.removeBossBar(abstractsWorld);
        if (bossBar != null) {
            bossBar.setProgress(1);
        }
        this.endCommand(abstractsWorld);
        abstractsWorld.getWorldEmergency().remove(this);
    }

    /**
     * 暫停緊急事件。
     *
     * @param abstractsWorld 世界物件
     * @param group 組別
     */
    @Override
    public void pause(AbstractsWorld abstractsWorld, String group) {
        if (abstractsWorld == null) {
            this.emergency.info(Emergency.getLang().worldNotFoundMessage + abstractsWorld.getWorld().getName(), Level.SEVERE);
            return;
        }
        String taskId = "task-" + group + "-" + abstractsWorld.getWorld().getName();
        Emergency.getTaskManager().pauseTask(taskId);
    }

    /**
     * 恢復已暫停的緊急事件。
     *
     * @param abstractsWorld 世界物件
     * @param group 組別
     */
    @Override
    public void resume(AbstractsWorld abstractsWorld, String group) {
        if (abstractsWorld == null) {
            this.emergency.info(Emergency.getLang().worldNotFoundMessage + abstractsWorld.getWorld().getName(), Level.SEVERE);
            return;
        }
        String taskId = "task-" + group + "-" + abstractsWorld.getWorld().getName();
        Emergency.getTaskManager().resumeTask(taskId);
    }
}
