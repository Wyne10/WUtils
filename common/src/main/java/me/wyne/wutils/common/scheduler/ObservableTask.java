package me.wyne.wutils.common.scheduler;

import me.wyne.wutils.common.terminable.Terminable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A {@link BukkitTask} builder that invokes every {@link #subscribe(Runnable) subscribed}
 * runnable on each run, instead of the single {@code runnable}/{@code promise} pair used by
 * {@link PromisedTask}.
 *
 * <p>Subscribers are stored in a {@link CopyOnWriteArrayList}, so {@link #subscribe(Runnable)}
 * is safe to call while the task is running (e.g. from within a subscriber); a subscriber added
 * mid-run only receives future runs. Subscribers run on whichever thread the task itself was
 * scheduled on — see the per-method notes below.
 */
public class ObservableTask implements Runnable, Terminable {

    private final Plugin plugin;
    private final List<Runnable> subscribers = new CopyOnWriteArrayList<>();

    private BukkitTask task;

    public ObservableTask(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds a runnable to be invoked on every future run of this task.
     */
    public void subscribe(@NotNull Runnable runnable) {
        subscribers.add(runnable);
    }

    /**
     * Removes all subscribers without affecting a running task.
     */
    public void clear() {
        subscribers.clear();
    }

    /**
     * Runs the task on the main thread on the next tick, invoking every subscriber once. Cancels
     * any task previously scheduled through this instance.
     */
    public synchronized void runTask() {
        cancel();
        task = Bukkit.getScheduler().runTask(plugin, this);
    }

    /**
     * Runs the task asynchronously, off the main thread, on the next tick, invoking every
     * subscriber once. Cancels any task previously scheduled through this instance.
     */
    public synchronized void runTaskAsynchronously() {
        cancel();
        task = Bukkit.getScheduler().runTaskAsynchronously(plugin, this);
    }

    /**
     * Runs the task on the main thread after {@code delay} ticks, invoking every subscriber
     * once. Cancels any task previously scheduled through this instance.
     */
    public synchronized void runTaskLater(long delay) {
        cancel();
        task = Bukkit.getScheduler().runTaskLater(plugin, this, delay);
    }

    /**
     * Runs the task asynchronously after {@code delay} ticks, invoking every subscriber once.
     * Cancels any task previously scheduled through this instance.
     */
    public synchronized void runTaskLaterAsynchronously(long delay) {
        cancel();
        task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this, delay);
    }

    /**
     * Repeats on the main thread every {@code period} ticks after an initial {@code delay},
     * invoking every subscriber on each run. Cancels any task previously scheduled through this
     * instance.
     */
    public synchronized void runTaskTimer(long delay, long period) {
        cancel();
        task = Bukkit.getScheduler().runTaskTimer(plugin, this, delay, period);
    }

    /**
     * Repeats asynchronously every {@code period} ticks after an initial {@code delay}, invoking
     * every subscriber on each run. Cancels any task previously scheduled through this instance.
     */
    public synchronized void runTaskTimerAsynchronously(long delay, long period) {
        cancel();
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, delay, period);
    }

    @Override
    public void run() {
        subscribers.forEach(Runnable::run);
    }

    /**
     * Cancels the current task, if any, without clearing subscribers.
     */
    public synchronized void cancel() {
        if (task == null || task.isCancelled()) return;
        task.cancel();
    }

    @Override
    public void close() {
        cancel();
    }

    @Nullable
    public synchronized BukkitTask getTask() {
        return task;
    }

    /**
     * @return an unmodifiable view of the current subscribers
     */
    public @NotNull List<@NotNull Runnable> getSubscribers() {
        return Collections.unmodifiableList(subscribers);
    }

}
