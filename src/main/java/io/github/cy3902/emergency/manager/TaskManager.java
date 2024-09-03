package io.github.cy3902.emergency.manager;

import io.github.cy3902.emergency.Emergency;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskManager {
    private final Map<String, BukkitTask> tasks = new ConcurrentHashMap<>();
    private final Map<String, TaskStatus> taskStatus = new ConcurrentHashMap<>();

    public TaskManager() {
    }

    public enum TaskStatus {
        RUNNING, PAUSED, STOPPED
    }

    public void startPeriodicTask(String taskId, Runnable taskRunnable, long initialDelay, long period) {
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Emergency.getInstance(), new Runnable() {
            @Override
            public void run() {
                TaskStatus status = taskStatus.get(taskId);
                if (status == TaskStatus.STOPPED) {
                    cancelTask(taskId);
                    return;
                }

                if (status == TaskStatus.PAUSED) {
                    // 任務暫停
                    return;
                }

                // 執行邏輯
                taskRunnable.run();
            }
        }, initialDelay, period);

        tasks.put(taskId, task);
        taskStatus.put(taskId, TaskStatus.RUNNING);
    }

    public void pauseTask(String taskId) {
        taskStatus.put(taskId, TaskStatus.PAUSED);
    }

    public void resumeTask(String taskId) {
        TaskStatus status = taskStatus.get(taskId);
        if (status == TaskStatus.PAUSED) {
            taskStatus.put(taskId, TaskStatus.RUNNING);
        }
    }

    public void cancelTask(String taskId) {
        BukkitTask task = tasks.get(taskId);
        if (task != null) {
            task.cancel();
            tasks.remove(taskId);
            taskStatus.remove(taskId);
        }
    }


    public TaskStatus getTaskStatus(String taskId) {
        return taskStatus.getOrDefault(taskId, TaskStatus.STOPPED);
    }
    public BukkitTask getTask(String taskId) {
        return tasks.get(taskId);
    }
}