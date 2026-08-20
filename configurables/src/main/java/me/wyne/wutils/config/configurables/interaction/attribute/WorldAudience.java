package me.wyne.wutils.config.configurables.interaction.attribute;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAudienceAttribute;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The {@code toWorlds} audience — the union of every player in the configured worlds.
 *
 * <p>Each configured entry is parsed as a namespaced {@link Key}, not a plain world name: a bare
 * {@code world} reads as {@code minecraft:world}, and a name containing uppercase characters or
 * underscores throws {@link net.kyori.adventure.key.InvalidKeyException}.</p>
 */
public class WorldAudience extends ConfigurableAttribute<List<String>> implements InteractionAudienceAttribute {

    public WorldAudience(@NotNull String key, @NotNull List<@NotNull String> value) {
        super(key, value);
    }

    public WorldAudience(@NotNull List<@NotNull String> value) {
        super(InteractionAttribute.AUDIENCE_WORLDS.getKey(), value);
    }

    @Override
    public @NotNull Audience get(@NotNull CommandSender sender) {
        return Audience.audience(getValue().stream()
                .map(world -> I18n.global.getAudiences().world(Key.key(world))).toList());
    }

    public static final class Factory implements AttributeFactory<WorldAudience> {
        @Override
        public @NotNull WorldAudience create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new WorldAudience(key, ConfigUtils.getStringList(config, key));
        }
    }

}
