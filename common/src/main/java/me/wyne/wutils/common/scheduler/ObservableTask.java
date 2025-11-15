package me.wyne.wutils.common.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class ObservableTask {

    private final Plugin plugin;
    private final List<Runnable> subscribers = new LinkedList<>();

    private BukkitTask task;

    public ObservableTask(Plugin plugin) {
        this.plugin = plugin;
    }

    public void subscribe(Runnable runnable) {
        subscribers.add(runnable);
    }

    public void clear() {
        subscribers.clear();
    }

    public void runTaskLater(long delay) {
        cancel();
        task = Bukkit.getScheduler().runTaskLater(plugin, this::run, delay);
    }

    public void runTaskLaterAsynchronously(long delay) {
        cancel();
        task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this::run, delay);
    }

    public void runTaskTimer(long delay, long period) {
        cancel();
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::run, delay, period);
    }

    public void runTaskTimerAsynchronously(long delay, long period) {
        cancel();
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::run, delay, period);
    }

    public void run() {
        subscribers.forEach(Runnable::run);
    }

    public void cancel() {
        if (task == null || task.isCancelled()) return;
        task.cancel();
    }

    @Nullable
    public BukkitTask getTask() {
        return task;
    }

    public List<Runnable> getSubscribers() {
        return subscribers;
    }

}
