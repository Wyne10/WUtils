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
 * The {@code toAll} audience — every player and the console.
 *
 * <p>Its presence in a section, not its configured value, is what counts: the boolean value is
 * always {@code true} regardless of what config says (see {@link Factory}), so
 * {@code toAll: false} still sends to everybody.</p>
 */
public class AllAudience extends ConfigurableAttribute<Boolean> implements InteractionAudienceAttribute {

    public AllAudience(@NotNull String key) {
        super(key, true);
    }

    public AllAudience() {
        super(InteractionAttribute.AUDIENCE_ALL.getKey(), true);
    }

    @Override
    public @NotNull Audience get(@NotNull CommandSender sender) {
        return I18n.global.getAudiences().all();
    }

    /**
     * Always resolves to {@code true} — {@code toAll: false} still enables this audience because
     * only the key's presence is read, never the configured value.
     */
    public static final class Factory implements AttributeFactory<AllAudience> {
        @Override
        public @NotNull AllAudience create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new AllAudience(key);
        }
    }

}
