package io.github.cy3902.emergency.emergency;

import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.utils.EmergencyUtils;
import io.github.cy3902.emergency.world.TimeWorld;
import org.bukkit.boss.BossBar;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;

public class TimeEmergency extends AbstractsEmergency{

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



    @Override
    public void start(AbstractsWorld abstractsWorld, String group) {
        if (abstractsWorld == null) {
            this.emergency.info(emergency.getLang().worldNotFoundMessage + abstractsWorld.getWorld().getName(), Level.SEVERE);
            return;
        }
        TimeWorld timeWorld = (TimeWorld) abstractsWorld;
        delayTimeAndStop(timeWorld, group);
        createBossBar(abstractsWorld);
        startCommand(abstractsWorld);
    }


    protected void delayTimeAndStop(TimeWorld world, String group) {
        long d = this.duration;
        String taskId = "task-" + group + "-" + world.getWorld().getName();
        Runnable taskRunnable = new Runnable() {
            double progress = 1.0;
            long totalDurationInSeconds = d * 20L;
            long updateInterval = 20L;
            double progressDecreasePerTime = 1.0 / (totalDurationInSeconds / updateInterval);

            @Override
            public void run() {
                LocalDateTime time = LocalDateTime.now();

                if (time.isAfter(world.getGroupTimeEnd().get(group)) || time.isEqual(world.getGroupTimeEnd().get(group))) {
                    if (bossBar != null) {
                        bossBar.setProgress(0);
                    }
                    emergency.getEmergencyManager().earlyStop(world, group);
                } else {
                    if (bossBar != null) {
                        bossBar.setProgress(progress);
                    }
                    progress = Math.max(0.0, progress - progressDecreasePerTime);
                }
            }
        };

        emergency.getTaskManager().startPeriodicTask(taskId, taskRunnable, 0L, 20L);
        this.activeTasks.put(taskId, emergency.getTaskManager().getTask(taskId));
    }



    @Override
    public void stop(AbstractsWorld abstractsWorld, String group) {
        if (abstractsWorld == null) {
            this.emergency.info(emergency.getLang().worldNotFoundMessage + abstractsWorld.getWorld().getName(), Level.SEVERE);
            return;
        }

        this.removeBossBar(abstractsWorld);
        if (bossBar != null) {
            bossBar.setProgress(1);
        }
        this.endCommand(abstractsWorld);
        abstractsWorld.getWorldEmergency().remove(this);
    }

    @Override
    public void pause(AbstractsWorld abstractsWorld, String group) {
        if (abstractsWorld == null) {
            this.emergency.info(emergency.getLang().worldNotFoundMessage+ abstractsWorld.getWorld().getName(), Level.SEVERE);
            return;
        }

        String taskId = "task-" + group + "-" + abstractsWorld.getWorld().getName();
        Emergency.getInstance().getTaskManager().pauseTask(taskId);
    }

    @Override
    public void resume(AbstractsWorld abstractsWorld, String group) {
        if (abstractsWorld == null) {
            this.emergency.info(emergency.getLang().worldNotFoundMessage+ abstractsWorld.getWorld().getName(), Level.SEVERE);
            return;
        }
        String taskId = "task-" + group + "-" + abstractsWorld.getWorld().getName();
        Emergency.getInstance().getTaskManager().resumeTask(taskId);

    }


}
