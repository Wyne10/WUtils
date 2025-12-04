package me.wyne.wutils.config.configurables.interaction.attribute;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.interaction.ContextInteractionAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAttributeContext;
import me.wyne.wutils.config.configurables.interaction.InteractionAttribute;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class PlayerCommandAttribute extends ConfigurableAttribute<List<String>> implements ContextInteractionAttribute {

    public PlayerCommandAttribute(String key, List<String> value) {
        super(key, value);
    }

    public PlayerCommandAttribute(List<String> value) {
        super(InteractionAttribute.PLAYER_COMMAND.getKey(), value);
    }

    @Override
    public void send(Audience audience, CommandSender sender, InteractionAttributeContext context) {
        getValue().stream()
                .map(s -> I18n.global.accessor(sender, s).getPlaceholderString(context.getPlaceholderTarget(), context.getTextReplacements()).get())
                .forEach(command -> Bukkit.dispatchCommand(sender, command));
    }

    public static final class Factory implements AttributeFactory<PlayerCommandAttribute> {
        @Override
        public PlayerCommandAttribute create(String key, ConfigurationSection config) {
            return new PlayerCommandAttribute(key, ConfigUtils.getStringList(config, key));
        }
    }

}
