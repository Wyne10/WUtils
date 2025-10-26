package me.wyne.wutils.common.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class PromisedTask<T extends Event> {

    private final Plugin plugin;
    private final Runnable runnable;
    private final Consumer<T> promise;
    private final Predicate<T> condition;
    private final long delay;

    private BukkitTask task;

    public PromisedTask(Plugin plugin, Runnable runnable, Consumer<T> promise, Predicate<T> condition, long delay) {
        this.plugin = plugin;
        this.runnable = runnable;
        this.promise = promise;
        this.condition = condition;
        this.delay = delay;
    }

    public PromisedTask(Plugin plugin, Runnable runnable, Consumer<T> promise, long delay) {
        this(plugin, runnable, promise, event -> true, delay);
    }

    public PromisedTask(Plugin plugin, Runnable runnable, Predicate<T> condition, long delay) {
        this(plugin, runnable, event -> runnable.run(), condition, delay);
    }

    public PromisedTask(Plugin plugin, Runnable runnable, long delay) {
        this(plugin, runnable, event -> runnable.run(), event -> true, delay);
    }

    public BukkitTask start() {
        task = Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        return task;
    }

    public void cancel(Event event) {
        if (task == null || task.isCancelled()) return;
        if (!condition.test((T) event)) return;
        task.cancel();
        promise.accept((T) event);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public Consumer<T> getPromise() {
        return promise;
    }

    public Predicate<T> getCondition() {
        return condition;
    }

    public long getDelay() {
        return delay;
    }

    @Nullable
    public BukkitTask getTask() {
        return task;
    }

}
