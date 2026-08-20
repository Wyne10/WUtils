package me.wyne.wutils.config.configurables.animation;

/**
 * Mutable delay/period/duration accumulator, in ticks, that {@link TimingsAnimationAttribute}
 * implementations write onto while an animation step is built.
 */
public class AnimationTimings {
    public long delay;
    public long period;
    public long duration;

    public AnimationTimings(long delay, long period, long duration) {
        this.delay = delay;
        this.period = period;
        this.duration = duration;
    }
}
