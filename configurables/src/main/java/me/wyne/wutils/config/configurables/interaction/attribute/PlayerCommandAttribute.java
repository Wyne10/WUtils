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
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The {@code command} payload — dispatches each configured command as {@code sender}.
 *
 * <p>Not audience-aware: it iterates the configured list and dispatches once per entry as the
 * single {@code sender} passed to {@link #send}, ignoring the resolved {@code audience} entirely —
 * it never runs once per audience member.</p>
 */
public class PlayerCommandAttribute extends ConfigurableAttribute<List<String>> implements ContextInteractionAttribute {

    public PlayerCommandAttribute(@NotNull String key, @NotNull List<@NotNull String> value) {
        super(key, value);
    }

    public PlayerCommandAttribute(@NotNull List<@NotNull String> value) {
        super(InteractionAttribute.PLAYER_COMMAND.getKey(), value);
    }

    @Override
    public void send(@NotNull Audience audience, @NotNull CommandSender sender, @NotNull InteractionAttributeContext context) {
        getValue().stream()
                .map(s -> I18n.global.accessor(sender, s).getPlaceholderString(context.getPlaceholderTarget(), context.getTextReplacements()).get())
                .forEach(command -> Bukkit.dispatchCommand(sender, command));
    }

    public static final class Factory implements AttributeFactory<PlayerCommandAttribute> {
        @Override
        public @NotNull PlayerCommandAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new PlayerCommandAttribute(key, ConfigUtils.getStringList(config, key));
        }
    }

}
