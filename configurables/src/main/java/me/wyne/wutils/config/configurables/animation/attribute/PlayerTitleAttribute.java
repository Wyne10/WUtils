package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.runnable.TitleEffect;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.animation.ContextAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.common.TitleAttribute;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.title.Title;
import org.bukkit.configuration.ConfigurationSection;

public class PlayerTitleAttribute extends TitleAttribute implements ContextAnimationAttribute {

    public PlayerTitleAttribute(String key, TitleData value) {
        super(key, value);
    }

    public PlayerTitleAttribute(TitleData value) {
        super(AnimationAttribute.PLAYER_TITLE.getKey(), value);
    }

    @Override
    public AnimationRunnable create(AnimationContext context) {
        if (context.getPlayer() == null) return AnimationRunnable.EMPTY;
        return new TitleEffect(I18n.global.getAudiences().player(context.getPlayer()), Title.title(
                I18n.global.accessor(context.getPlayer(), getValue().title()).getPlaceholderComponent(context.getPlayer(), context.getTextReplacements()).replace(context.getComponentReplacements()).get(),
                I18n.global.accessor(context.getPlayer(), getValue().subtitle()).getPlaceholderComponent(context.getPlayer(), context.getTextReplacements()).replace(context.getComponentReplacements()).get(),
                getValue().times()
        ));
    }

    public static final class Factory implements AttributeFactory<PlayerTitleAttribute> {
        @Override
        public PlayerTitleAttribute create(String key, ConfigurationSection config) {
            return new PlayerTitleAttribute(key, new TitleAttribute.Factory().create(key, config).getValue());
        }
    }

}
