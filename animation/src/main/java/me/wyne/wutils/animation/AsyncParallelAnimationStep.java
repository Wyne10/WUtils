package me.wyne.wutils.animation;

import org.bukkit.Bukkit;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

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
                    close();
                    animation.getParallelTasks().remove(this);
        }, getDelay());
        animation.getParallelTasks().put(this, task);
        startNext(animation);
    }

    @Override
    protected void runRepeating(Animation animation) {
        var task = Bukkit.getScheduler().runTaskTimerAsynchronously(
                animation.getPlugin(),
                () -> {
                    if (getDuration() > 0 && ticksElapsed >= getDuration()) {
                        close();
                        var currentTask = animation.getParallelTasks().remove(this);
                        if (currentTask != null)
                            currentTask.cancel();
                        return;
                    }
                    getRunnable().run(getDelay(), getPeriod(), getDuration());
                    ticksElapsed += getPeriod();
        }, getDelay(), getPeriod());
        animation.getParallelTasks().put(this, task);
        startNext(animation);
    }

}
