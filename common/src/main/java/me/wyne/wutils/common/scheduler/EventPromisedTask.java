package me.wyne.wutils.common.scheduler;

import me.wyne.wutils.common.event.EventRegistry;
import me.wyne.wutils.common.event.RegisterableListener;
import me.wyne.wutils.common.terminable.Terminable;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class EventPromisedTask<T extends Event> implements RegisterableListener, Terminable {

    private final Plugin plugin;
    private final Runnable runnable;
    private final Consumer<@Nullable T> promise;

    private final EventRegistry eventRegistry;
    private BukkitTask task;

    public EventPromisedTask(Plugin plugin, Runnable runnable, Consumer<@Nullable T> promise, Class<T> event) {
        this.plugin = plugin;
        this.runnable = runnable;
        this.promise = promise;
        this.eventRegistry = new EventRegistry(plugin);
        try {
            eventRegistry.register(plugin, this, event, getClass().getDeclaredMethod("onEvent", Event.class));
        } catch (NoSuchMethodException ignored) {}
    }

    public EventPromisedTask(Plugin plugin, Runnable runnable, Class<T> event) {
        this(plugin, runnable, calledEvent -> runnable.run(), event);
    }

    public synchronized void runTask() {
        if (task != null) return;
        task = Bukkit.getScheduler().runTask(plugin, () -> { runnable.run(); task.cancel(); eventRegistry.closeAndReportException(); });
    }

    public synchronized void runTaskAsynchronously() {
        if (task != null) return;
        task = Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> { runnable.run(); task.cancel(); eventRegistry.closeAndReportException(); });
    }

    public synchronized void runTaskLater(long delay) {
        if (task != null) return;
        task = Bukkit.getScheduler().runTaskLater(plugin, () -> { runnable.run(); task.cancel(); eventRegistry.closeAndReportException(); }, delay);
    }

    public synchronized void runTaskLaterAsynchronously(long delay) {
        if (task != null) return;
        task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> { runnable.run(); task.cancel(); eventRegistry.closeAndReportException(); }, delay);
    }

    public synchronized void runTaskTimer(long delay, long period) {
        if (task != null) return;
        task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

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
