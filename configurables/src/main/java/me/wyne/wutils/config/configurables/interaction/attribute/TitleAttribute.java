package me.wyne.wutils.config.configurables.interaction.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.interaction.ContextInteractionAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAttributeContext;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class TitleAttribute extends me.wyne.wutils.config.configurables.attribute.common.TitleAttribute implements ContextInteractionAttribute {

    public TitleAttribute(String key, me.wyne.wutils.config.configurables.attribute.common.TitleAttribute.TitleData value) {
        super(key, value);
    }

    public TitleAttribute(me.wyne.wutils.config.configurables.attribute.common.TitleAttribute.TitleData value) {
        super(InteractionAttribute.TITLE.getKey(), value);
    }

    @Override
    public void send(Audience audience, CommandSender sender, InteractionAttributeContext context) {
        audience.showTitle(Title.title(
                I18n.global.accessor(sender, getValue().title()).getPlaceholderComponent(context.getPlaceholderTarget(), context.getTextReplacements()).replace(context.getComponentReplacements()).get(),
                I18n.global.accessor(sender, getValue().subtitle()).getPlaceholderComponent(context.getPlaceholderTarget(), context.getTextReplacements()).replace(context.getComponentReplacements()).get(),
                getValue().times()
        ));
    }

    public static final class Factory implements AttributeFactory<me.wyne.wutils.config.configurables.attribute.common.TitleAttribute> {
        @Override
        public TitleAttribute create(String key, ConfigurationSection config) {
            return new TitleAttribute(key, new me.wyne.wutils.config.configurables.attribute.common.TitleAttribute.Factory().create(key, config).getValue());
        }
    }

}
