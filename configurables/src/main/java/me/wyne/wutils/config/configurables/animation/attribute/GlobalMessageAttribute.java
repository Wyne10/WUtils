package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.runnable.MessageEffect;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.animation.ContextAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class GlobalMessageAttribute extends ConfigurableAttribute<List<String>> implements ContextAnimationAttribute {

    public GlobalMessageAttribute(String key, List<String> value) {
        super(key, value);
    }

    public GlobalMessageAttribute(List<String> value) {
        super(AnimationAttribute.GLOBAL_MESSAGE.getKey(), value);
    }

    @Override
    public AnimationRunnable create(AnimationContext context) {
        if (context.getPlayer() == null) return AnimationRunnable.EMPTY;
        return new MessageEffect(
                I18n.global.getAudiences().players(),
                getValue().stream()
                        .map(s -> I18n.global.accessor(context.getPlayer(), s).getPlaceholderComponent(context.getPlayer(), context.getTextReplacements()).replace(context.getComponentReplacements()).get())
                        .reduce(I18n::reduceRawComponent).orElse(Component.empty())
        );
    }

    public static final class Factory implements AttributeFactory<GlobalMessageAttribute> {
        @Override
        public GlobalMessageAttribute create(String key, ConfigurationSection config) {
            return new GlobalMessageAttribute(key, ConfigUtils.getStringList(config, key));
        }
    }

}
