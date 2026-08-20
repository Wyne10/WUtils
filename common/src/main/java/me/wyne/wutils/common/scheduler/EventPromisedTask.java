package me.wyne.wutils.common.scheduler;

import me.wyne.wutils.common.event.EventRegistry;
import me.wyne.wutils.common.event.RegisterableListener;
import me.wyne.wutils.common.terminable.Terminable;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * A {@link PromisedTask} whose {@code promise} also completes early when a chosen {@link Event}
 * fires, whichever happens first.
 *
 * <p>Registers a private {@code @EventHandler} for {@code event} through an internal
 * {@link EventRegistry} (see {@link RegisterableListener}) for the lifetime of the scheduled
 * task. If the event fires before the task is cancelled, {@code promise} is invoked with the
 * fired event instance and the task and event listener are torn down; if {@link #cancel()} is
 * called first (or the task is {@link #close() closed}), {@code promise} is invoked with
 * {@code null} instead.
 *
 * <p><strong>The non-repeating {@code runTask*}/{@code runTaskLater*} overloads never invoke
 * {@code promise} on a natural run</strong>: once {@code runnable} finishes, they cancel the
 * underlying {@link BukkitTask} and close the internal {@link EventRegistry} directly, bypassing
 * {@link #cancel()}. If the target event never fires and the task is never explicitly cancelled,
 * {@code promise} is simply never called. The repeating {@code runTaskTimer*} overloads never
 * tear themselves down this way — the task and event listener stay alive indefinitely until the
 * event fires or {@link #cancel()}/{@link #close()} is called.
 */
public class EventPromisedTask<T extends Event> implements RegisterableListener, Terminable {

    private final Plugin plugin;
    private final Runnable runnable;
    private final Consumer<@Nullable T> promise;

    private final EventRegistry eventRegistry;
    private BukkitTask task;

    /**
     * Creates a task that runs {@code runnable} and separately notifies {@code promise} exactly
     * once, either with the fired {@code event} instance or with {@code null} on cancellation.
     */
    public EventPromisedTask(@NotNull Plugin plugin, @NotNull Runnable runnable, @NotNull Consumer<@Nullable T> promise, @NotNull Class<T> event) {
        this.plugin = plugin;
        this.runnable = runnable;
        this.promise = promise;
        this.eventRegistry = new EventRegistry(plugin);
        try {
            eventRegistry.register(this, event, getClass().getDeclaredMethod("onEvent", Event.class));
        } catch (NoSuchMethodException ignored) {}
    }

    /**
     * Equivalent to the four-argument constructor with {@code promise} set to invoke
     * {@code runnable} again if {@code event} fires before the task's own run — {@code runnable}
     * still only actually runs once either way, since whichever happens first cancels the other
     * path.
     */
    public EventPromisedTask(@NotNull Plugin plugin, @NotNull Runnable runnable, @NotNull Class<T> event) {
        this(plugin, runnable, calledEvent -> runnable.run(), event);
    }

    /**
     * Runs the task on the main thread on the next tick. Either this run or the target event
     * completes the task, whichever happens first.
     */
    public synchronized void runTask() {
        if (task != null) return;
        task = Bukkit.getScheduler().runTask(plugin, () -> { runnable.run(); task.cancel(); eventRegistry.closeAndReportException(); });
    }

    /**
     * Runs the task asynchronously, off the main thread, on the next tick. Either this run or
     * the target event completes the task, whichever happens first.
     */
    public synchronized void runTaskAsynchronously() {
        if (task != null) return;
        task = Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> { runnable.run(); task.cancel(); eventRegistry.closeAndReportException(); });
    }

    /**
     * Runs the task on the main thread after {@code delay} ticks. Either this run or the target
     * event completes the task, whichever happens first.
     */
    public synchronized void runTaskLater(long delay) {
        if (task != null) return;
        task = Bukkit.getScheduler().runTaskLater(plugin, () -> { runnable.run(); task.cancel(); eventRegistry.closeAndReportException(); }, delay);
    }

    /**
     * Runs the task asynchronously after {@code delay} ticks. Either this run or the target
     * event completes the task, whichever happens first.
     */
    public synchronized void runTaskLaterAsynchronously(long delay) {
        if (task != null) return;
        task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> { runnable.run(); task.cancel(); eventRegistry.closeAndReportException(); }, delay);
    }

    /**
     * Repeats {@code runnable} on the main thread every {@code period} ticks after an initial
     * {@code delay}. Unlike the non-repeating overloads, a run does not cancel the task or the
     * event listener — only the target event firing or an explicit {@link #cancel()} stops it.
     */
    public synchronized void runTaskTimer(long delay, long period) {
        if (task != null) return;
        task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

    /**
     * Repeats {@code runnable} asynchronously every {@code period} ticks after an initial
     * {@code delay}. Unlike the non-repeating overloads, a run does not cancel the task or the
     * event listener — only the target event firing or an explicit {@link #cancel()} stops it.
     */
    public synchronized void runTaskTimerAsynchronously(long delay, long period) {
        if (task != null) return;
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }

    private void cancel(Event event) {
        synchronized (this) {
            if (task == null || task.isCancelled()) return;
            eventRegistry.closeAndReportException();
            task.cancel();
        }
        promise.accept((T) event);
    }

    /**
     * Cancels the task and the internal event listener, then invokes {@code promise} with
     * {@code null}.
     *
     * @throws Exception if closing the internal {@link EventRegistry} fails
     */
    public void cancel() throws Exception {
        synchronized (this) {
            if (task == null || task.isCancelled()) return;
            eventRegistry.close();
            task.cancel();
        }
        promise.accept(null);
    }

    @Override
    public void close() throws Exception {
        cancel();
    }

    /**
     * @return true once the task has stopped — naturally, via {@link #cancel()}, or via the
     *         target event firing
     */
    @Override
    public synchronized boolean isClosed() {
        return task != null && task.isCancelled();
    }

    @Nullable
    public synchronized BukkitTask getTask() {
        return task;
    }

    @EventHandler
    private void onEvent(Event event) {
        cancel(event);
    }

}
