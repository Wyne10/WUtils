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
 * The {@code delay} timing — ticks before the step's first run, parsed via {@link ConfigUtils#getTicks}
 * so it accepts duration expressions ({@code 20}, {@code 20t}, {@code 1s}, {@code 1500ms}, ...).
 */
public class AnimationDelayAttribute extends ConfigurableAttribute<Long> implements TimingsAnimationAttribute {

    public AnimationDelayAttribute(@NotNull String key, long value) {
        super(key, value);
    }

    public AnimationDelayAttribute(long value) {
        super(AnimationAttribute.DELAY.getKey(), value);
    }

    @Override
    public void apply(@NotNull AnimationTimings timings) {
        timings.delay = getValue();
    }

    public static final class Factory implements AttributeFactory<AnimationDelayAttribute> {
        @Override
        public @NotNull AnimationDelayAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new AnimationDelayAttribute(key, ConfigUtils.getTicks(config, key));
        }
    }

}
