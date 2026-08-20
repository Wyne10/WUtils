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
 * The {@code toPermissions} audience — the union of every player holding any of the configured
 * permission nodes.
 */
public class PermissionAudience extends ConfigurableAttribute<List<String>> implements InteractionAudienceAttribute {

    public PermissionAudience(@NotNull String key, @NotNull List<@NotNull String> value) {
        super(key, value);
    }

    public PermissionAudience(@NotNull List<@NotNull String> value) {
        super(InteractionAttribute.AUDIENCE_PERMISSIONS.getKey(), value);
    }

    @Override
    public @NotNull Audience get(@NotNull CommandSender sender) {
        return Audience.audience(getValue().stream()
                .map(permission -> I18n.global.getAudiences().permission(permission)).toList());
    }

    public static final class Factory implements AttributeFactory<PermissionAudience> {
        @Override
        public @NotNull PermissionAudience create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new PermissionAudience(key, ConfigUtils.getStringList(config, key));
        }
    }

}
