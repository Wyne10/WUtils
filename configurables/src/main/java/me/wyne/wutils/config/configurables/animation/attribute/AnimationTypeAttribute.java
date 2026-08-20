package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.AnimationStep;
import me.wyne.wutils.animation.BlockingAnimationStep;
import me.wyne.wutils.animation.ParallelAnimationStep;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import com.google.common.base.Preconditions;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code type} attribute — picks whether an animation step is {@link AnimationType#BLOCKING}
 * (the default, the animation waits for it to finish) or {@link AnimationType#PARALLEL} (it starts
 * and the animation moves on).
 */
public class AnimationTypeAttribute extends ConfigurableAttribute<AnimationTypeAttribute.AnimationType> {

    public AnimationTypeAttribute(@NotNull String key, @NotNull AnimationType value) {
        super(key, value);
    }

    public AnimationTypeAttribute(@NotNull AnimationType value) {
        super(AnimationAttribute.TYPE.getKey(), value);
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().name()).buildNoSpace();
    }

    public static final class Factory implements AttributeFactory<AnimationTypeAttribute> {
        @Override
        public @NotNull AnimationTypeAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            var name = config.getString(key, "BLOCKING");
            var type = ConfigUtils.getByName(name, AnimationType.class);
            Preconditions.checkNotNull(type, "Invalid animation type '" + name + "' at " + ConfigUtils.getPath(config, key));
            return new AnimationTypeAttribute(key, type);
        }
    }

    @FunctionalInterface
    public interface AnimationStepFactory {
        @NotNull AnimationStep create(@NotNull AnimationRunnable runnable, long delay, long period, long duration);
    }

    public enum AnimationType {
        BLOCKING(BlockingAnimationStep::new),
        PARALLEL(ParallelAnimationStep::new);

        private final AnimationStepFactory factory;

        AnimationType(@NotNull AnimationStepFactory factory) {
            this.factory = factory;
        }

        public @NotNull AnimationStep create(@NotNull AnimationRunnable runnable, long delay, long period, long duration) {
            return factory.create(runnable, delay, period, duration);
        }
    }

}
