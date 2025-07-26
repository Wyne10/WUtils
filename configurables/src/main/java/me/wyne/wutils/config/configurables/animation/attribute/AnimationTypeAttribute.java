package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.AnimationStep;
import me.wyne.wutils.animation.BlockingAnimationStep;
import me.wyne.wutils.animation.ParallelAnimationStep;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.configuration.ConfigurationSection;

public class AnimationTypeAttribute extends ConfigurableAttribute<AnimationTypeAttribute.AnimationType> {

    public AnimationTypeAttribute(String key, AnimationType value) {
        super(key, value);
    }

    public AnimationTypeAttribute(AnimationType value) {
        super(AnimationAttribute.TYPE.getKey(), value);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().name()).buildNoSpace();
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public AnimationTypeAttribute create(String key, ConfigurationSection config) {
            return new AnimationTypeAttribute(key, AnimationType.valueOf(config.getString(key, "BLOCKING")));
        }
    }

    @FunctionalInterface
    public interface AnimationStepFactory {
        AnimationStep create(AnimationRunnable runnable, int delay, int period, int duration);
    }

    public enum AnimationType {
        BLOCKING(BlockingAnimationStep::new),
        PARALLEL(ParallelAnimationStep::new);

        private final AnimationStepFactory factory;

        AnimationType(AnimationStepFactory factory) {
            this.factory = factory;
        }

        public AnimationStep create(AnimationRunnable runnable, int delay, int period, int duration) {
            return factory.create(runnable, delay, period, duration);
        }
    }

}
