package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.data.AnimationParticle;
import me.wyne.wutils.animation.runnable.WorldParticle;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.animation.ContextAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code worldParticle} effect — spawns a particle at the context location. Needs a location;
 * returns {@link AnimationRunnable#EMPTY} when the context has none.
 *
 * <p>Its own factory is a plain {@code AttributeFactory} that delegates to
 * {@link AnimationParticleAttribute.Factory}, a composite one, so this attribute can be aliased with
 * {@code attributeType}. See {@link AnimationParticleAttribute} for the shared string/section grammar.</p>
 */
public class WorldParticleAttribute extends AnimationParticleAttribute implements ContextAnimationAttribute<AnimationContext> {

    public WorldParticleAttribute(@NotNull String key, @NotNull AnimationParticle value) {
        super(key, value);
    }

    public WorldParticleAttribute(@NotNull AnimationParticle value) {
        super(AnimationAttribute.WORLD_PARTICLE.getKey(), value);
    }

    @Override
    public @NotNull AnimationRunnable create(@NotNull AnimationContext context) {
        if (context.getLocation() == null) return AnimationRunnable.EMPTY;
        return new WorldParticle(context.getLocation(), getValue());
    }

    public static final class Factory implements AttributeFactory<WorldParticleAttribute> {
        @Override
        public @NotNull WorldParticleAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new WorldParticleAttribute(key, new AnimationParticleAttribute.Factory().create(key, config).getValue());
        }
    }

}
