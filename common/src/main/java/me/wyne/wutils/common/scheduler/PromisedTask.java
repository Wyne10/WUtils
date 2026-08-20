package me.wyne.wutils.common.scheduler;

import me.wyne.wutils.common.terminable.Terminable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link BukkitTask} builder that runs a {@code promise} callback exactly once, when the task
 * is stopped.
 *
 * <p>Unlike helper's {@link Task}, which represents an already-running repeating task, this class
 * is the scheduling entry point: it mirrors {@link org.bukkit.scheduler.BukkitScheduler}'s own
 * run/runLater/runTimer method family directly. The {@code promise} is only invoked by
 * {@link #cancel()} (and {@link #close()}, which calls it) — the scheduled {@code runnable} is
 * handed to Bukkit unwrapped, so for the non-repeating {@code runTask}/{@code runTaskLater}
 * overloads, {@code runnable} finishing on its own does <strong>not</strong> run {@code promise};
 * the caller must call {@link #cancel()} once the work is done for {@code promise} to fire at
 * all.
 */
public class PromisedTask implements Terminable {

    private final Plugin plugin;
    private final Runnable runnable;
    private final Runnable promise;

    private BukkitTask task;

    public PromisedTask(@NotNull Plugin plugin, @NotNull Runnable runnable, @NotNull Runnable promise) {
        this.plugin = plugin;
        this.runnable = runnable;
        this.promise = promise;
    }

    /**
     * Equivalent to the three-argument constructor with {@code promise} set to {@code runnable}
     * itself.
     */
    public PromisedTask(@NotNull Plugin plugin, @NotNull Runnable runnable) {
        this(plugin, runnable, runnable);
    }

    /**
     * Runs the task on the main thread on the next tick. Cancels and completes any task
     * previously scheduled through this instance.
     */
    public synchronized void runTask() {
        cancel();
        task = Bukkit.getScheduler().runTask(plugin, runnable);
    }

    /**
     * Runs the task asynchronously, off the main thread, on the next tick. Cancels and completes
     * any task previously scheduled through this instance.
     */
    public synchronized void runTaskAsynchronously() {
        cancel();
        task = Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    /**
     * Runs the task on the main thread after {@code delay} ticks. Cancels and completes any task
     * previously scheduled through this instance.
     */
    public synchronized void runTaskLater(long delay) {
        cancel();
        task = Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
    }

    /**
     * Runs the task asynchronously after {@code delay} ticks. Cancels and completes any task
     * previously scheduled through this instance.
     */
    public synchronized void runTaskLaterAsynchronously(long delay) {
        cancel();
        task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    /**
     * Repeats the task on the main thread every {@code period} ticks, starting after
     * {@code delay} ticks. Cancels and completes any task previously scheduled through this
     * instance.
     */
    public synchronized void runTaskTimer(long delay, long period) {
        cancel();
        task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

    /**
     * Repeats the task asynchronously every {@code period} ticks, starting after {@code delay}
     * ticks. Cancels and completes any task previously scheduled through this instance.
     */
    public synchronized void runTaskTimerAsynchronously(long delay, long period) {
        cancel();
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }

    /**
     * Cancels the current task, if any, and runs {@code promise} exactly once.
     *
     * <p>Synchronizes only around the cancellation check, so {@code promise} — arbitrary caller
     * code — runs without holding this instance's lock. Safe to call concurrently or more than
     * once; only the first call after scheduling triggers {@code promise}. Runs on whichever
     * thread calls this method.
     */
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
