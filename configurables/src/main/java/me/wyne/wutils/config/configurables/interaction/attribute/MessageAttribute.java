package me.wyne.wutils.config.configurables.interaction.attribute;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.interaction.ContextInteractionAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAttributeContext;
import me.wyne.wutils.config.configurables.interaction.InteractionAttribute;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class MessageAttribute extends ConfigurableAttribute<List<String>> implements ContextInteractionAttribute {

    public MessageAttribute(String key, List<String> value) {
        super(key, value);
    }

    public MessageAttribute(List<String> value) {
        super(InteractionAttribute.MESSAGE.getKey(), value);
    }

    @Override
    public void send(Audience audience, CommandSender sender, InteractionAttributeContext context) {
        getValue().stream()
                .map(s -> I18n.global.accessor(sender, s).getPlaceholderComponent(context.getPlaceholderTarget(), context.getTextReplacements()).replace(context.getComponentReplacements()))
                .forEach(component -> component.sendMessage(audience));
    }

    public static final class Factory implements AttributeFactory<MessageAttribute> {
        @Override
        public MessageAttribute create(String key, ConfigurationSection config) {
            return new MessageAttribute(key, ConfigUtils.getStringList(config, key));
        }
    }

}
