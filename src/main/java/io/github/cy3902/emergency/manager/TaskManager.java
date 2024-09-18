package io.github.cy3902.emergency.manager;

import io.github.cy3902.emergency.Emergency;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理插件中的所有任務。
 * 提供對定期任務的啟動、暫停、恢復和取消功能，
 * 並能夠追蹤任務的狀態。
 */
public class TaskManager {
    // 存儲任務 ID 和 BukkitTask 物件的映射
    private final Map<String, BukkitTask> tasks = new ConcurrentHashMap<>();
    // 存儲任務 ID 和其狀態（運行中、暫停、停止）的映射
    private final Map<String, TaskStatus> taskStatus = new ConcurrentHashMap<>();

    public TaskManager() {
    }

    /**
     * 任務狀態的枚舉。
     */
    public enum TaskStatus {
        RUNNING, PAUSED, STOPPED
    }

    /**
     * 啟動一個定期任務。
     *
     * @param taskId 任務 ID
     * @param taskRunnable 任務執行的邏輯
     * @param initialDelay 任務首次啟動的延遲（單位：tick）
     * @param period 任務的重複間隔（單位：tick）
     */
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
                    // 任務暫停，什麼也不做
                    return;
                }

                // 執行任務邏輯
                taskRunnable.run();
            }
        }, initialDelay, period);

        tasks.put(taskId, task);
        taskStatus.put(taskId, TaskStatus.RUNNING);
    }

    /**
     * 暫停指定的任務。
     *
     * @param taskId 任務 ID
     */
    public void pauseTask(String taskId) {
        taskStatus.put(taskId, TaskStatus.PAUSED);
    }

    /**
     * 恢復指定的任務。
     *
     * @param taskId 任務 ID
     */
    public void resumeTask(String taskId) {
        TaskStatus status = taskStatus.get(taskId);
        if (status == TaskStatus.PAUSED) {
            taskStatus.put(taskId, TaskStatus.RUNNING);
        }
    }

    /**
     * 取消指定的任務。
     *
     * @param taskId 任務 ID
     */
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