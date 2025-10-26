package me.wyne.wutils.common.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

public final class PromisedTask {

    private final Plugin plugin;
    private final Runnable runnable;
    private final long delay;

    private BukkitTask task;

    public PromisedTask(Plugin plugin, Runnable runnable, long delay) {
        this.plugin = plugin;
        this.runnable = runnable;
        this.delay = delay;
    }

    public BukkitTask start() {
        task = Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        return task;
    }

    public void cancel() {
        if (task == null || task.isCancelled()) return;
        task.cancel();
        runnable.run();
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public long getDelay() {
        return delay;
    }

    @Nullable
    public BukkitTask getTask() {
        return task;
    }

}
