package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.runnable.MessageEffect;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.animation.ContextAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.i18n.I18n;
import org.bukkit.configuration.ConfigurationSection;

public class PlayerMessageAttribute extends ConfigurableAttribute<String> implements ContextAnimationAttribute {

    public PlayerMessageAttribute(String key, String value) {
        super(key, value);
    }

    public PlayerMessageAttribute(String value) {
        super(AnimationAttribute.PLAYER_MESSAGE.getKey(), value);
    }

    @Override
    public AnimationRunnable create(AnimationContext context) {
        if (context.getPlayer() == null) return AnimationRunnable.Companion.getEMPTY();
        return new MessageEffect(
                I18n.global.getAudiences().player(context.getPlayer()),
                I18n.global.accessor(context.getPlayer(), getValue()).getPlaceholderComponent(context.getPlayer(), context.getTextReplacements()).replace(context.getComponentReplacements()).get()
        );
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public PlayerMessageAttribute create(String key, ConfigurationSection config) {
            return new PlayerMessageAttribute(key, config.getString(key));
        }
    }

}
