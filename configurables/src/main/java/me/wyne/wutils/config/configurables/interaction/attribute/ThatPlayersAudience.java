package me.wyne.wutils.config.configurables.interaction.attribute;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAudienceAttribute;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class ThatPlayersAudience extends ConfigurableAttribute<List<String>> implements InteractionAudienceAttribute {

    public ThatPlayersAudience(String key, List<String> value) {
        super(key, value);
    }

    public ThatPlayersAudience(List<String> value) {
        super(InteractionAttribute.AUDIENCE_THAT_PLAYERS.getKey(), value);
    }

    @Override
    public Audience get(CommandSender sender) {
        return I18n.global.getAudiences().filter(player -> getValue().contains(player.getName()));
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public ThatPlayersAudience create(String key, ConfigurationSection config) {
            return new ThatPlayersAudience(key, ConfigUtils.getStringList(config, key));
        }
    }

}
