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
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The {@code message} payload — sends each configured line as a chat message to the audience.
 */
public class MessageAttribute extends ConfigurableAttribute<List<String>> implements ContextInteractionAttribute {

    public MessageAttribute(@NotNull String key, @NotNull List<@NotNull String> value) {
        super(key, value);
    }

    public MessageAttribute(@NotNull List<@NotNull String> value) {
        super(InteractionAttribute.MESSAGE.getKey(), value);
    }

    @Override
    public void send(@NotNull Audience audience, @NotNull CommandSender sender, @NotNull InteractionAttributeContext context) {
        getValue().stream()
                .map(s -> I18n.global.accessor(sender, s).getPlaceholderComponent(context.getPlaceholderTarget(), context.getTextReplacements()).replace(context.getComponentReplacements()))
                .forEach(component -> component.sendMessage(audience));
    }

    public static final class Factory implements AttributeFactory<MessageAttribute> {
        @Override
        public @NotNull MessageAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new MessageAttribute(key, ConfigUtils.getStringList(config, key));
        }
    }

}
