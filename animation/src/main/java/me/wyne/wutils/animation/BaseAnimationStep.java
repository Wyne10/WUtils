package me.wyne.wutils.animation;

import org.jetbrains.annotations.NotNull;

/**
 * Base implementation shared by the four concrete {@link AnimationStep} types.
 *
 * <p>Holds the step's {@link AnimationRunnable} and its timing ({@code delay}, {@code period}
 * and {@code duration}, all in ticks), and routes {@link #run(Animation)} to either
 * {@link #runOnce(Animation)} or {@link #runRepeating(Animation)} depending on whether a
 * repeat {@code period} was given. Subclasses only need to implement those two methods and
 * decide, via the Bukkit scheduler, whether the step runs synchronously or asynchronously and
 * whether it blocks the animation queue or runs in parallel with it.</p>
 *
 * <p>{@link #close()} releases the runnable's resources if it is {@link AutoCloseable};
 * {@link #_finalize()} does the equivalent via {@link Finalizable}, for a runnable belonging
 * to a step that was queued but never got to run.</p>
 */
public abstract class BaseAnimationStep implements AnimationStep, Finalizable {

    private final AnimationRunnable runnable;
    private final long delay;
    private final long period;
    private final long duration;

    /**
     * Ticks elapsed since {@link #run(Animation)} was called; reset there and advanced by
     * subclasses on each repetition.
     */
    protected long ticksElapsed;

    public BaseAnimationStep(@NotNull AnimationRunnable runnable, long delay, long period, long duration) {
        this.runnable = runnable;
        this.delay = delay;
        this.period = period;
        this.duration = duration;
    }

    public BaseAnimationStep(@NotNull AnimationRunnable runnable, long delay) {
        this(runnable, delay, 0, 0);
    }

    public BaseAnimationStep(@NotNull AnimationRunnable runnable) {
        this(runnable, 0, 0, 0);
    }

    @Override
    public void run(@NotNull Animation animation) {
        ticksElapsed = 0;
        createTask(animation);
    }

    @Override
    public void close() {
        if (runnable instanceof AutoCloseable) {
            try {
                ((AutoCloseable)runnable).close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void _finalize() {
        if (runnable instanceof Finalizable)
            ((Finalizable)runnable)._finalize();
    }

    private void createTask(@NotNull Animation animation) {
        if (period == 0)
            runOnce(animation);
        else
            runRepeating(animation);
    }

    /**
     * Polls the next queued step from the animation and starts it, if there is one.
     */
    protected void startNext(@NotNull Animation animation) {
        var nextStep = animation.pollStep();
        if (nextStep != null)
            nextStep.run(animation);
    }

    public @NotNull AnimationRunnable getRunnable() {
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

    protected abstract void runOnce(@NotNull Animation animation);
    protected abstract void runRepeating(@NotNull Animation animation);

}
