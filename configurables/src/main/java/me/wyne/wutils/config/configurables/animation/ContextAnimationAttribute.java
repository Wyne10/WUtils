package me.wyne.wutils.config.configurables.animation;

import me.wyne.wutils.animation.AnimationRunnable;

public interface ContextAnimationAttribute {
    AnimationRunnable create(AnimationContext context);
}
