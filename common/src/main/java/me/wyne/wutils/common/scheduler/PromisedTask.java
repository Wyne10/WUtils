package me.wyne.wutils.common.scheduler;

import me.wyne.wutils.common.terminable.Terminable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

public class PromisedTask implements Terminable {

    private final Plugin plugin;
    private final Runnable runnable;
    private final Runnable promise;

    private BukkitTask task;

    public PromisedTask(Plugin plugin, Runnable runnable, Runnable promise) {
        this.plugin = plugin;
        this.runnable = runnable;
        this.promise = promise;
    }

    public PromisedTask(Plugin plugin, Runnable runnable) {
        this(plugin, runnable, runnable);
    }

    public synchronized void runTask() {
        cancel();
        task = Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public synchronized void runTaskAsynchronously() {
        cancel();
        task = Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    public synchronized void runTaskLater(long delay) {
        cancel();
        task = Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
    }

    public synchronized void runTaskLaterAsynchronously(long delay) {
        cancel();
        task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    public synchronized void runTaskTimer(long delay, long period) {
        cancel();
        task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

    public synchronized void runTaskTimerAsynchronously(long delay, long period) {
        cancel();
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }

    public void cancel() {
        synchronized (this) {
            if (task == null || task.isCancelled()) return;
            task.cancel();
        }
        promise.run();
    }

    @Override
    public void close() {
        cancel();
    }

    @Nullable
    public synchronized BukkitTask getTask() {
        return task;
    }

}
