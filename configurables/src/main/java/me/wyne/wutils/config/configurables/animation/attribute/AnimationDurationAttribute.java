package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.TimingAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.configuration.ConfigurationSection;

public class AnimationDurationAttribute extends ConfigurableAttribute<Integer> implements TimingAnimationAttribute {

    public AnimationDurationAttribute(String key, int value) {
        super(key, value);
    }

    public AnimationDurationAttribute(int value) {
        super(AnimationAttribute.DURATION.getKey(), value);
    }

    @Override
    public int getTicks() {
        return getValue();
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public AnimationDurationAttribute create(String key, ConfigurationSection config) {
            return new AnimationDurationAttribute(key, config.getInt(key));
        }
    }

}
