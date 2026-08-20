package me.wyne.wutils.animation;

import org.bukkit.Bukkit;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

/**
 * A step whose {@link AnimationRunnable} runs off the main server thread (via
 * {@link org.bukkit.scheduler.BukkitScheduler#runTaskLaterAsynchronously} /
 * {@code runTaskTimerAsynchronously}), so most Bukkit API is unsafe to call from it. It blocks
 * the animation queue the same way {@link BlockingAnimationStep} does: the next step is not
 * started until this one's {@code duration} has elapsed.
 *
 * <p>The step never calls {@link #close()} from its async callback: on completion it is
 * scheduled back onto the main server thread, so a runnable's cleanup may safely use Bukkit
 * API. A close triggered by {@link Animation#stop()} instead runs on whichever thread called
 * {@code stop()}.</p>
 */
public class AsyncBlockingAnimationStep extends BaseAnimationStep {

    public AsyncBlockingAnimationStep(AnimationRunnable runnable, long delay, long period, long duration) {
        super(runnable, delay, period, duration);
    }

    public AsyncBlockingAnimationStep(AnimationRunnable runnable, long delay) {
        super(runnable, delay);
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
                    var close = Bukkit.getScheduler().runTaskLater(animation.getPlugin(), () -> {
                        close();
                        startNext(animation);
                    }, getDuration());
                    animation.setCurrentTask(new Pair<>(this, close));
        }, getDelay());
        animation.setCurrentTask(new Pair<>(this, task));
    }

    @Override
    protected void runRepeating(@NotNull Animation animation) {
        var task = Bukkit.getScheduler().runTaskTimerAsynchronously(
                animation.getPlugin(),
                () -> {
                    if (getDuration() > 0 && ticksElapsed >= getDuration()) {
                        var currentTask = animation.getCurrentTask();
                        if (currentTask != null)
                            currentTask.getValue1().cancel();
                        Bukkit.getScheduler().runTask(animation.getPlugin(), () -> {
                            close();
                            startNext(animation);
                        });
                        return;
                    }
                    getRunnable().run(getDelay(), getPeriod(), getDuration());
                    ticksElapsed += getPeriod();
        }, getDelay(), getPeriod());
        animation.setCurrentTask(new Pair<>(this, task));
    }

}
