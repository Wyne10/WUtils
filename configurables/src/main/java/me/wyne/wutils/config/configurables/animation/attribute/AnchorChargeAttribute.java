package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.runnable.AnchorCharge;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.animation.ContextAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code anchorCharge} effect — sets the respawn anchor charge level at the context location.
 * Needs a location; returns {@link AnimationRunnable#EMPTY} when the context has none.
 */
public class AnchorChargeAttribute extends ConfigurableAttribute<Integer> implements ContextAnimationAttribute<AnimationContext> {

    public AnchorChargeAttribute(@NotNull String key, @NotNull Integer value) {
        super(key, value);
    }

    public AnchorChargeAttribute(@NotNull Integer value) {
        super(AnimationAttribute.ANCHOR_CHARGE.getKey(), value);
    }

    @Override
    public @NotNull AnimationRunnable create(@NotNull AnimationContext context) {
        if (context.getLocation() == null) return AnimationRunnable.EMPTY;
        return new AnchorCharge(context.getLocation(), getValue());
    }

    public static final class Factory implements AttributeFactory<AnchorChargeAttribute> {
        @Override
        public @NotNull AnchorChargeAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new AnchorChargeAttribute(key, config.getInt(key));
        }
    }

}
