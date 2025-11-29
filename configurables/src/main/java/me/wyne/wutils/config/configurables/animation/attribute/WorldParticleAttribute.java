package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.data.AnimationParticle;
import me.wyne.wutils.animation.runnable.WorldParticle;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.animation.ContextAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import org.bukkit.configuration.ConfigurationSection;

public class WorldParticleAttribute extends AnimationParticleAttribute implements ContextAnimationAttribute {

    public WorldParticleAttribute(String key, AnimationParticle value) {
        super(key, value);
    }

    public WorldParticleAttribute(AnimationParticle value) {
        super(AnimationAttribute.WORLD_PARTICLE.getKey(), value);
    }

    @Override
    public AnimationRunnable create(AnimationContext context) {
        if (context.getLocation() == null) return AnimationRunnable.EMPTY;
        return new WorldParticle(context.getLocation(), getValue());
    }

    public static final class Factory implements AttributeFactory<WorldParticleAttribute> {
        @Override
        public WorldParticleAttribute create(String key, ConfigurationSection config) {
            return new WorldParticleAttribute(key, new AnimationParticleAttribute.Factory().create(key, config).getValue());
        }
    }

}
