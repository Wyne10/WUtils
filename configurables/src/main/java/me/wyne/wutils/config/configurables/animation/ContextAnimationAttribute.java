package me.wyne.wutils.config.configurables.animation;

import me.wyne.wutils.animation.AnimationRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * An animation step effect, built from an {@link AnimationContext} into an {@link AnimationRunnable}.
 *
 * <p>Implementations check what they need from the context (a player, a location, ...) and return
 * {@link AnimationRunnable#EMPTY} rather than failing when it is missing — a location-based effect
 * with no location does nothing, silently. That makes partially-supplied contexts safe, and makes a
 * mis-set-up animation look like a config problem when it is a code problem.</p>
 *
 * @param <C> the context type this attribute reads from
 */
public interface ContextAnimationAttribute<C extends AnimationContext> {
    @NotNull AnimationRunnable create(@NotNull C context);
}
