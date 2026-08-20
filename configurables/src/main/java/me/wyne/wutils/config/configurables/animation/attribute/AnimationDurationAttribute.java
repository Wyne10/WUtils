package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationTimings;
import me.wyne.wutils.config.configurables.animation.TimingsAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code duration} timing — total ticks the step runs for, parsed via {@link ConfigUtils#getTicks}
 * so it accepts duration expressions ({@code 20}, {@code 20t}, {@code 1s}, {@code 1500ms}, ...).
 */
public class AnimationDurationAttribute extends ConfigurableAttribute<Long> implements TimingsAnimationAttribute {

    public AnimationDurationAttribute(@NotNull String key, long value) {
        super(key, value);
    }

    public AnimationDurationAttribute(long value) {
        super(AnimationAttribute.DURATION.getKey(), value);
    }

    @Override
    public void apply(@NotNull AnimationTimings timings) {
        timings.duration = getValue();
    }

    public static final class Factory implements AttributeFactory<AnimationDurationAttribute> {
        @Override
        public @NotNull AnimationDurationAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new AnimationDurationAttribute(key, ConfigUtils.getTicks(config, key));
        }
    }

}
