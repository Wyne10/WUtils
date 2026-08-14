package me.wyne.wutils.config.configurables.animation;

import me.wyne.wutils.animation.AnimationRunnable;

public interface ContextAnimationAttribute<C extends AnimationContext> {
    AnimationRunnable create(C context);
}
