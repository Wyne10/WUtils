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
 * The {@code toConsole} audience — the server console only.
 *
 * <p>Its presence in a section, not its configured value, is what counts: the boolean value is
 * always {@code true} regardless of what config says (see {@link Factory}), so
 * {@code toConsole: false} still enables it.</p>
 */
public class ConsoleAudience extends ConfigurableAttribute<Boolean> implements InteractionAudienceAttribute {

    public ConsoleAudience(@NotNull String key) {
        super(key, true);
    }

    public ConsoleAudience() {
        super(InteractionAttribute.AUDIENCE_CONSOLE.getKey(), true);
    }

    @Override
    public @NotNull Audience get(@NotNull CommandSender sender) {
        return I18n.global.getAudiences().console();
    }

    /**
     * Always resolves to {@code true} — {@code toConsole: false} still enables this audience because
     * only the key's presence is read, never the configured value.
     */
    public static final class Factory implements AttributeFactory<ConsoleAudience> {
        @Override
        public @NotNull ConsoleAudience create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new ConsoleAudience(key);
        }
    }

}
