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

    public void runTaskLater(long delay) {
        if (task != null) return;
        task = Bukkit.getScheduler().runTaskLater(plugin, () -> { runnable.run(); eventRegistry.closeAndReportException(); task.cancel(); }, delay);
    }

    public void runTaskLaterAsynchronously(long delay) {
        if (task != null) return;
        task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> { runnable.run(); eventRegistry.closeAndReportException(); task.cancel(); }, delay);
    }

    public void runTaskTimer(long delay, long period) {
        if (task != null) return;
        task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

    public void runTaskTimerAsynchronously(long delay, long period) {
        if (task != null) return;
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }

    public void cancel(Event event) {
        eventRegistry.closeAndReportException();
        if (task == null || task.isCancelled()) return;
        task.cancel();
        promise.accept((T) event);
    }

    public void cancel() throws Exception {
        eventRegistry.close();
        if (task == null || task.isCancelled()) return;
        task.cancel();
        promise.accept(null);
    }

    @Override
    public void close() throws Exception {
        cancel();
    }

    @Override
    public boolean isClosed() {
        return task != null && task.isCancelled();
    }

    @Nullable
    public BukkitTask getTask() {
        return task;
    }

    @EventHandler
    private void onEvent(Event event) {
        cancel(event);
    }

}
