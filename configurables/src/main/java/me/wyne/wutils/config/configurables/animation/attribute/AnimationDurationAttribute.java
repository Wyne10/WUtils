package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationTimings;
import me.wyne.wutils.config.configurables.animation.TimingsAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.configuration.ConfigurationSection;

public class AnimationDurationAttribute extends ConfigurableAttribute<Long> implements TimingsAnimationAttribute {

    public AnimationDurationAttribute(String key, long value) {
        super(key, value);
    }

    public AnimationDurationAttribute(long value) {
        super(AnimationAttribute.DURATION.getKey(), value);
    }

    @Override
    public void apply(AnimationTimings timings) {
        timings.duration = getValue();
    }

    public static final class Factory implements AttributeFactory<AnimationDurationAttribute> {
        @Override
        public AnimationDurationAttribute create(String key, ConfigurationSection config) {
            return new AnimationDurationAttribute(key, ConfigUtils.getTicks(config, key));
        }
    }

}
