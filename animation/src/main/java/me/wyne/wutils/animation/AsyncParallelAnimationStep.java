package me.wyne.wutils.animation;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * A step whose {@link AnimationRunnable} runs off the main server thread (via
 * {@link org.bukkit.scheduler.BukkitScheduler#runTaskLaterAsynchronously} /
 * {@code runTaskTimerAsynchronously}), so most Bukkit API is unsafe to call from it. It does
 * not block the animation queue: the next step is started immediately, running alongside this
 * one until its {@code duration} elapses.
 *
 * <p>The step never calls {@link #close()} from its async callback: on completion it is
 * scheduled back onto the main server thread, so a runnable's cleanup may safely use Bukkit
 * API. A close triggered by {@link Animation#stop()} instead runs on whichever thread called
 * {@code stop()}.</p>
 */
public class AsyncParallelAnimationStep extends BaseAnimationStep {

    public AsyncParallelAnimationStep(AnimationRunnable runnable, long delay, long period, long duration) {
        super(runnable, delay, period, duration);
    }

    public AsyncParallelAnimationStep(AnimationRunnable runnable, long delay) {
        super(runnable, delay);
    }

    public AsyncParallelAnimationStep(AnimationRunnable runnable) {
        super(runnable);
    }

    @Override
    protected void runOnce(@NotNull Animation animation) {
        var task = Bukkit.getScheduler().runTaskLaterAsynchronously(
                animation.getPlugin(),
                () -> {
                    getRunnable().run(getDelay(), getPeriod(), getDuration());
                    var close = Bukkit.getScheduler().runTaskLater(animation.getPlugin(), () -> {
                        close();
                        animation.getParallelTasks().remove(this);
                    }, getDuration());
                    animation.getParallelTasks().put(this, close);
        }, getDelay());
        animation.getParallelTasks().put(this, task);
        startNext(animation);
    }

    @Override
    protected void runRepeating(@NotNull Animation animation) {
        var task = Bukkit.getScheduler().runTaskTimerAsynchronously(
                animation.getPlugin(),
                () -> {
                    if (getDuration() > 0 && ticksElapsed >= getDuration()) {
                        var currentTask = animation.getParallelTasks().remove(this);
                        if (currentTask != null)
                            currentTask.cancel();
                        Bukkit.getScheduler().runTask(animation.getPlugin(), this::close);
                        return;
                    }
                    getRunnable().run(getDelay(), getPeriod(), getDuration());
                    ticksElapsed += getPeriod();
        }, getDelay(), getPeriod());
        animation.getParallelTasks().put(this, task);
        startNext(animation);
    }

}
