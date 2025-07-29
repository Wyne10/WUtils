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

public class GlobalMessageAttribute extends ConfigurableAttribute<String> implements ContextAnimationAttribute {

    public GlobalMessageAttribute(String key, String value) {
        super(key, value);
    }

    public GlobalMessageAttribute(String value) {
        super(AnimationAttribute.GLOBAL_MESSAGE.getKey(), value);
    }

    @Override
    public AnimationRunnable create(AnimationContext context) {
        if (context.getPlayer() == null) return AnimationRunnable.Companion.getEMPTY();
        return new MessageEffect(
                I18n.global.audiences.players(),
                I18n.global.getPlaceholderComponent(I18n.toLocale(context.getPlayer()), context.getPlayer(), getValue(), context.getTextReplacements()).replace(context.getComponentReplacements()).get()
        );
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public GlobalMessageAttribute create(String key, ConfigurationSection config) {
            return new GlobalMessageAttribute(key, config.getString(key));
        }
    }

}
