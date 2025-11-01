package me.wyne.wutils.config.configurables.interaction.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAudienceAttribute;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class PlayersAudience extends ConfigurableAttribute<Boolean> implements InteractionAudienceAttribute {

    public PlayersAudience(String key) {
        super(key, true);
    }

    public PlayersAudience() {
        super(InteractionAttribute.AUDIENCE_PLAYERS.getKey(), true);
    }

    @Override
    public Audience get(CommandSender sender) {
        return I18n.global.getAudiences().players();
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public PlayersAudience create(String key, ConfigurationSection config) {
            return new PlayersAudience(key);
        }
    }

}
