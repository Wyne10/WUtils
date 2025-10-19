package me.wyne.wutils.animation;

import org.jetbrains.annotations.NotNull;

public abstract class BaseAnimationStep implements AnimationStep {

    private final AnimationRunnable runnable;
    private final long delay;
    private final long period;
    private final long duration;

    protected long ticksElapsed;

    public BaseAnimationStep(AnimationRunnable runnable, long delay, long period, long duration) {
        this.runnable = runnable;
        this.delay = delay;
        this.period = period;
        this.duration = duration;
    }

    public BaseAnimationStep(AnimationRunnable runnable) {
        this(runnable, 0, 0, 0);
    }

    @Override
    public void run(@NotNull Animation animation) {
        ticksElapsed = 0;
        createTask(animation);
    }

    @Override
    public void close() {
        runnable.close();
    }

    private void createTask(Animation animation) {
        if (period == 0)
            runOnce(animation);
        else
            runRepeating(animation);
    }

    protected void startNext(Animation animation) {
        var nextStep = animation.pollStep();
        if (nextStep != null)
            nextStep.run(animation);
    }

    public AnimationRunnable getRunnable() {
        return runnable;
    }

    public long getDelay() {
        return delay;
    }

    public long getPeriod() {
        return period;
    }

    public long getDuration() {
        return duration;
    }

    protected abstract void runOnce(Animation animation);
    protected abstract void runRepeating(Animation animation);

}
