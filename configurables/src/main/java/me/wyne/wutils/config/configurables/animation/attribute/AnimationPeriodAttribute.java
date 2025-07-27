package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.TimingAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.configuration.ConfigurationSection;

public class AnimationPeriodAttribute extends ConfigurableAttribute<Integer> implements TimingAnimationAttribute {

    public AnimationPeriodAttribute(String key, int value) {
        super(key, value);
    }

    public AnimationPeriodAttribute(int value) {
        super(AnimationAttribute.PERIOD.getKey(), value);
    }

    @Override
    public int getTicks() {
        return getValue();
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public AnimationPeriodAttribute create(String key, ConfigurationSection config) {
            return new AnimationPeriodAttribute(key, config.getInt(key));
        }
    }

}
