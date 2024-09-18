package io.github.cy3902.emergency.emergency;


import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.manager.EmergencyManager;
import io.github.cy3902.emergency.utils.Utils;
import io.github.cy3902.emergency.world.TimeWorld;
import org.bukkit.boss.BossBar;


import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;

/**
 * 代表基於時間的緊急事件。
 */
public class TimeEmergency extends AbstractsEmergency {

    /**
     * 構造函數，用於初始化 TimeEmergency 對象。
     *
     * @param name              緊急事件名稱
     * @param bossBarBool       是否顯示 BossBar
     * @param bossBar           BossBar 對象
     * @param startCommand      事件開始時執行的命令
     * @param endCommand        事件結束時執行的命令
     * @param onJoinCommand     玩家加入事件時執行的命令
     * @param onQuitCommand     玩家退出事件時執行的命令
     * @param group             事件的組別
     * @param chance            事件發生的機率
     * @param days              事件持續的天數
     * @param duration          事件持續的時間（秒）
     */
    public TimeEmergency(
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
     * 開始緊急事件。
     *
     * @param abstractsWorld  抽象世界對象
     * @param group           事件組
     */
    @Override
    public void start(AbstractsWorld abstractsWorld, String group) {
        if (abstractsWorld == null) {
            this.emergency.info(Emergency.getLang().worldNotFoundMessage + abstractsWorld.getWorld().getName(), Level.SEVERE);
            return;
        }
        TimeWorld timeWorld = (TimeWorld) abstractsWorld;
        delayTimeAndStop(timeWorld, group, duration);
        createBossBar(abstractsWorld);
        startCommand(abstractsWorld);
    }

    /**
     * 開始緊急事件（重載方法）。
     *
     * @param timeWorld       時間世界對象
     * @param group           事件組
     * @param duration        事件持續時間（秒）
     */
    public void start(TimeWorld timeWorld, String group, long duration) {
        if (timeWorld == null) {
            this.emergency.info(Emergency.getLang().worldNotFoundMessage + timeWorld.getWorld().getName(), Level.SEVERE);
            return;
        }
        delayTimeAndStop(timeWorld, group, duration);
        createBossBar(timeWorld);
        startCommand(timeWorld);
    }

    /**
     * 設置延遲時間並停止事件。
     *
     * @param world           時間世界對象
     * @param group           事件組
     * @param durationRemaining 剩餘時間（秒）
     */
    protected void delayTimeAndStop(TimeWorld world, String group, long durationRemaining) {
        String taskId = "task-" + group + "-" + world.getWorld().getName();
        Runnable taskRunnable = new Runnable() {
            double progress = Utils.calculateSecondsBetween(LocalDateTime.now(), world.getGroupTimeEnd().get(group)) / duration;

            @Override
            public void run() {
                LocalDateTime time = LocalDateTime.now();
                double remaining = (double) Utils.calculateSecondsBetween(LocalDateTime.now(), world.getGroupTimeEnd().get(group)) / duration;

                // 如果當前時間已過或等於結束時間，停止事件
                if (time.isAfter(world.getGroupTimeEnd().get(group)) || time.isEqual(world.getGroupTimeEnd().get(group))) {
                    if (bossBar != null) {
                        bossBar.setProgress(0);
                    }
                    EmergencyManager.earlyStop(world, group);
                } else {
                    // 更新 BossBar 的進度
                    if (bossBar != null) {
                        bossBar.setProgress(progress);
                    }
                    progress = Math.max(0.0, remaining);
                }
            }
        };

        Emergency.getTaskManager().startPeriodicTask(taskId, taskRunnable, 0L, 20L);
        this.activeTasks.put(taskId, Emergency.getTaskManager().getTask(taskId));
    }

    /**
     * 停止緊急事件。
     *
     * @param abstractsWorld  抽象世界對象
     * @param group           事件組
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
     * @param abstractsWorld  抽象世界對象
     * @param group           事件組
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
     * 恢復緊急事件。
     *
     * @param abstractsWorld  抽象世界對象
     * @param group           事件組
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