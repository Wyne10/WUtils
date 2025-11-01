package me.wyne.wutils.common.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;
import java.util.List;

public class ObservableScheduler {

    private final Plugin plugin;
    private final List<Runnable> subscribers = new LinkedList<>();
    private BukkitTask task;

    public ObservableScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    public void subscribe(Runnable runnable) {
        subscribers.add(runnable);
    }

    public void clear() {
        subscribers.clear();
    }

    public void runTaskLater(long delay) {
        if (task != null && !task.isCancelled()) task.cancel();
        task = Bukkit.getScheduler().runTaskLater(plugin, this::run, delay);
    }

    public void runTaskLaterAsynchronously(long delay) {
        if (task != null && !task.isCancelled()) task.cancel();
        task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this::run, delay);
    }

    public void runTaskTimer(long delay, long period) {
        if (task != null && !task.isCancelled()) task.cancel();
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::run, delay, period);
    }

    public void runTaskTimerAsynchronously(long delay, long period) {
        if (task != null && !task.isCancelled()) task.cancel();
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::run, delay, period);
    }

    public void run() {
        subscribers.forEach(Runnable::run);
    }

    public void cancel() {
        if (task != null && !task.isCancelled()) task.cancel();
    }

}
