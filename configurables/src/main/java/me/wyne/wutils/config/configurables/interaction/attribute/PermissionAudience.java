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

public class PermissionAudience extends ConfigurableAttribute<List<String>> implements InteractionAudienceAttribute {

    public PermissionAudience(String key, List<String> value) {
        super(key, value);
    }

    public PermissionAudience(List<String> value) {
        super(InteractionAttribute.AUDIENCE_PERMISSIONS.getKey(), value);
    }

    @Override
    public Audience get(CommandSender sender) {
        return Audience.audience(getValue().stream()
                .map(permission -> I18n.global.getAudiences().permission(permission)).toList());
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public PermissionAudience create(String key, ConfigurationSection config) {
            return new PermissionAudience(key, ConfigUtils.getStringList(config, key));
        }
    }

}
