package me.wyne.wutils.common.scheduler;

import me.wyne.wutils.common.terminable.Terminable;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ObservableTask implements Runnable, Terminable {

    private final Plugin plugin;
    private final List<Runnable> subscribers = new CopyOnWriteArrayList<>();

    private BukkitTask task;

    public ObservableTask(Plugin plugin) {
        this.plugin = plugin;
    }

    public void subscribe(Runnable runnable) {
        subscribers.add(runnable);
    }

    public void clear() {
        subscribers.clear();
    }

    public synchronized void runTask() {
        cancel();
        task = Bukkit.getScheduler().runTask(plugin, this);
    }

    public synchronized void runTaskAsynchronously() {
        cancel();
        task = Bukkit.getScheduler().runTaskAsynchronously(plugin, this);
    }

    public synchronized void runTaskLater(long delay) {
        cancel();
        task = Bukkit.getScheduler().runTaskLater(plugin, this, delay);
    }

    public synchronized void runTaskLaterAsynchronously(long delay) {
        cancel();
        task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this, delay);
    }

    public synchronized void runTaskTimer(long delay, long period) {
        cancel();
        task = Bukkit.getScheduler().runTaskTimer(plugin, this, delay, period);
    }

    public synchronized void runTaskTimerAsynchronously(long delay, long period) {
        cancel();
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, delay, period);
    }

    @Override
    public void run() {
        subscribers.forEach(Runnable::run);
    }

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

    public List<Runnable> getSubscribers() {
        return Collections.unmodifiableList(subscribers);
    }

}
