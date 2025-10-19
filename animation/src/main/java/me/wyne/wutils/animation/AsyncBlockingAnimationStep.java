package me.wyne.wutils.animation;

import org.bukkit.Bukkit;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

public class AsyncBlockingAnimationStep extends BaseAnimationStep {

    public AsyncBlockingAnimationStep(AnimationRunnable runnable, long delay, long period, long duration) {
        super(runnable, delay, period, duration);
    }

    public AsyncBlockingAnimationStep(AnimationRunnable runnable) {
        super(runnable);
    }

    @Override
    protected void runOnce(@NotNull Animation animation) {
        var task = Bukkit.getScheduler().runTaskLaterAsynchronously(
                animation.getPlugin(),
                () -> {
                    getRunnable().run(getDelay(), getPeriod(), getDuration());
                    close();
                    startNext(animation);
        }, getDelay());
        animation.setCurrentTask(new Pair<>(this, task));
    }

    @Override
    protected void runRepeating(Animation animation) {
        var task = Bukkit.getScheduler().runTaskTimerAsynchronously(
                animation.getPlugin(),
                () -> {
                    if (getDuration() > 0 && ticksElapsed >= getDuration()) {
                        close();
                        var currentTask = animation.getCurrentTask();
                        if (currentTask != null)
                            currentTask.getValue1().cancel();
                        startNext(animation);
                        return;
                    }
                    getRunnable().run(getDelay(), getPeriod(), getDuration());
                    ticksElapsed += getPeriod();
        }, getDelay(), getPeriod());
        animation.setCurrentTask(new Pair<>(this, task));
    }

}
