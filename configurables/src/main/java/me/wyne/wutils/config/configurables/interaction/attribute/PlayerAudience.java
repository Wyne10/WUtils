package me.wyne.wutils.config.configurables.interaction.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAudienceAttribute;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code toPlayer} audience — resolves to {@code sender} itself, i.e. whoever the interaction
 * was sent to. This is the same audience {@code InteractionConfigurable} falls back to when no
 * audience attribute is declared at all.
 *
 * <p>Its presence in a section, not its configured value, is what counts: the boolean value is
 * always {@code true} regardless of what config says (see {@link #Factory}).</p>
 */
public class PlayerAudience extends ConfigurableAttribute<Boolean> implements InteractionAudienceAttribute {

    public PlayerAudience(@NotNull String key) {
        super(key, true);
    }

    public PlayerAudience() {
        super(InteractionAttribute.AUDIENCE_PLAYER.getKey(), true);
    }

    @Override
    public @NotNull Audience get(@NotNull CommandSender sender) {
        return I18n.global.getAudiences().sender(sender);
    }

    /**
     * Always resolves to {@code true} — {@code toPlayer: false} still enables this audience because
     * only the key's presence is read, never the configured value.
     */
    public static final class Factory implements AttributeFactory<PlayerAudience> {
        @Override
        public @NotNull PlayerAudience create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new PlayerAudience(key);
        }
    }

}
