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
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The {@code toThatPlayers} audience — every online player whose name is in the configured list.
 */
public class ThatPlayersAudience extends ConfigurableAttribute<List<String>> implements InteractionAudienceAttribute {

    public ThatPlayersAudience(@NotNull String key, @NotNull List<@NotNull String> value) {
        super(key, value);
    }

    public ThatPlayersAudience(@NotNull List<@NotNull String> value) {
        super(InteractionAttribute.AUDIENCE_THAT_PLAYERS.getKey(), value);
    }

    @Override
    public @NotNull Audience get(@NotNull CommandSender sender) {
        return I18n.global.getAudiences().filter(player -> getValue().contains(player.getName()));
    }

    public static final class Factory implements AttributeFactory<ThatPlayersAudience> {
        @Override
        public @NotNull ThatPlayersAudience create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new ThatPlayersAudience(key, ConfigUtils.getStringList(config, key));
        }
    }

}
