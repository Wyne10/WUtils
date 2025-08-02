package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationTimings;
import me.wyne.wutils.config.configurables.animation.TimingsAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.configuration.ConfigurationSection;

public class AnimationPeriodAttribute extends ConfigurableAttribute<Integer> implements TimingsAnimationAttribute {

    public AnimationPeriodAttribute(String key, int value) {
        super(key, value);
    }

    public AnimationPeriodAttribute(int value) {
        super(AnimationAttribute.PERIOD.getKey(), value);
    }

    @Override
    public void apply(AnimationTimings timings) {
        timings.period = getValue();
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public AnimationPeriodAttribute create(String key, ConfigurationSection config) {
            return new AnimationPeriodAttribute(key, config.getInt(key));
        }
    }

}
