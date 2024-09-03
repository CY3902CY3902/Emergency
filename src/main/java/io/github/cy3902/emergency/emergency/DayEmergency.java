package io.github.cy3902.emergency.emergency;


import io.github.cy3902.emergency.Emergency;
import io.github.cy3902.emergency.abstracts.AbstractsEmergency;
import io.github.cy3902.emergency.abstracts.AbstractsWorld;
import io.github.cy3902.emergency.utils.EmergencyUtils;
import io.github.cy3902.emergency.world.DayWorld;
import org.bukkit.boss.BossBar;

import java.util.List;
import java.util.logging.Level;

public class DayEmergency extends AbstractsEmergency {

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



    @Override
    public void start(AbstractsWorld abstractsWorld, String group) {
        if (abstractsWorld == null) {
            this.emergency.info(emergency.getLang().worldNotFoundMessage + abstractsWorld.getWorld().getName() , Level.SEVERE);
            return;
        }
        DayWorld dayWorld = (DayWorld) abstractsWorld;
        delayDayAndStop(dayWorld, group);
        createBossBar(abstractsWorld);
        startCommand(abstractsWorld);
    }



    protected void delayDayAndStop(DayWorld world, String group) {
        String taskId = "task-" + group + "-" + world.getWorld().getName();

        Runnable taskRunnable = new Runnable() {
            @Override
            public void run() {
                long currentTime = world.getWorld().getFullTime();
                double totalTime = 1200 * 20;
                double progress = Math.max(0.0, 1.0 - (currentTime % totalTime) / totalTime);

                if (bossBar != null) {
                    bossBar.setProgress(progress);
                }

                if (world.getDay() >= world.getGroupDayEnd().get(group)) {
                    emergency.getEmergencyManager().earlyStop(world, group);
                }
            }
        };
        emergency.getTaskManager().startPeriodicTask(taskId, taskRunnable, 20L, 20L);
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
            this.emergency.info(emergency.getLang().worldNotFoundMessage + abstractsWorld.getWorld().getName(), Level.SEVERE);
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
