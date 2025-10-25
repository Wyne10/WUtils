package me.wyne.wutils.animation;

import org.bukkit.Bukkit;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

public class BlockingAnimationStep extends BaseAnimationStep {

    public BlockingAnimationStep(AnimationRunnable runnable, long delay, long period, long duration) {
        super(runnable, delay, period, duration);
    }

    public BlockingAnimationStep(AnimationRunnable runnable, long delay) {
        super(runnable, delay);
    }

    public BlockingAnimationStep(AnimationRunnable runnable) {
        super(runnable);
    }

    @Override
    protected void runOnce(@NotNull Animation animation) {
        var task = Bukkit.getScheduler().runTaskLater(
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
        var task = Bukkit.getScheduler().runTaskTimer(
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
