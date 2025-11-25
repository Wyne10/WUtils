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

    public void runTaskLater(long delay) {
        cancel();
        task = Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
    }

    public void runTaskLaterAsynchronously(long delay) {
        cancel();
        task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    public void runTaskTimer(long delay, long period) {
        cancel();
        task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

    public void runTaskTimerAsynchronously(long delay, long period) {
        cancel();
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }

    public void cancel() {
        if (task == null || task.isCancelled()) return;
        task.cancel();
        promise.run();
    }

    @Override
    public void close() {
        cancel();
    }

    @Nullable
    public BukkitTask getTask() {
        return task;
    }

}
