package me.wyne.wutils.config.configurables.interaction.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAudienceAttribute;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class ConsoleAudience extends ConfigurableAttribute<Boolean> implements InteractionAudienceAttribute {

    public ConsoleAudience(String key) {
        super(key, true);
    }

    public ConsoleAudience() {
        super(InteractionAttribute.AUDIENCE_CONSOLE.getKey(), true);
    }

    @Override
    public Audience get(CommandSender sender) {
        return I18n.global.getAudiences().console();
    }

    public static final class Factory implements AttributeFactory<ConsoleAudience> {
        @Override
        public ConsoleAudience create(String key, ConfigurationSection config) {
            return new ConsoleAudience(key);
        }
    }

}
