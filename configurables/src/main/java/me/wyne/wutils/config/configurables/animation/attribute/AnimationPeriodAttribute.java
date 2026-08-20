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
 * The {@code period} timing — ticks between repeats, parsed via {@link ConfigUtils#getTicks} so it
 * accepts duration expressions ({@code 20}, {@code 20t}, {@code 1s}, {@code 1500ms}, ...). {@code 0}
 * means the step runs once.
 */
public class AnimationPeriodAttribute extends ConfigurableAttribute<Long> implements TimingsAnimationAttribute {

    public AnimationPeriodAttribute(@NotNull String key, long value) {
        super(key, value);
    }

    public AnimationPeriodAttribute(long value) {
        super(AnimationAttribute.PERIOD.getKey(), value);
    }

    @Override
    public void apply(@NotNull AnimationTimings timings) {
        timings.period = getValue();
    }

    public static final class Factory implements AttributeFactory<AnimationPeriodAttribute> {
        @Override
        public @NotNull AnimationPeriodAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new AnimationPeriodAttribute(key, ConfigUtils.getTicks(config, key));
        }
    }

}
