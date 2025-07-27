package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.TimingAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.configuration.ConfigurationSection;

public class AnimationDelayAttribute extends ConfigurableAttribute<Integer> implements TimingAnimationAttribute {

    public AnimationDelayAttribute(String key, int value) {
        super(key, value);
    }

    public AnimationDelayAttribute(int value) {
        super(AnimationAttribute.DELAY.getKey(), value);
    }

    @Override
    public int getTicks() {
        return getValue();
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public AnimationDelayAttribute create(String key, ConfigurationSection config) {
            return new AnimationDelayAttribute(key, config.getInt(key));
        }
    }

}
