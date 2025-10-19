package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.runnable.AnchorCharge;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.animation.ContextAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.configuration.ConfigurationSection;

public class AnchorChargeAttribute extends ConfigurableAttribute<Integer> implements ContextAnimationAttribute {

    public AnchorChargeAttribute(String key, Integer value) {
        super(key, value);
    }

    public AnchorChargeAttribute(Integer value) {
        super(AnimationAttribute.ANCHOR_CHARGE.getKey(), value);
    }

    @Override
    public AnimationRunnable create(AnimationContext context) {
        if (context.getLocation() == null) return AnimationRunnable.EMPTY;
        return new AnchorCharge(context.getLocation(), getValue());
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public AnchorChargeAttribute create(String key, ConfigurationSection config) {
            return new AnchorChargeAttribute(key, config.getInt(key, 1));
        }
    }

}
