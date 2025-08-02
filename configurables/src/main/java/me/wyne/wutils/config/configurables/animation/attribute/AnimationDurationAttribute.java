package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationTimings;
import me.wyne.wutils.config.configurables.animation.TimingsAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.configuration.ConfigurationSection;

public class AnimationDurationAttribute extends ConfigurableAttribute<Integer> implements TimingsAnimationAttribute {

    public AnimationDurationAttribute(String key, int value) {
        super(key, value);
    }

    public AnimationDurationAttribute(int value) {
        super(AnimationAttribute.DURATION.getKey(), value);
    }

    @Override
    public void apply(AnimationTimings timings) {
        timings.duration = getValue();
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public AnimationDurationAttribute create(String key, ConfigurationSection config) {
            return new AnimationDurationAttribute(key, config.getInt(key));
        }
    }

}
