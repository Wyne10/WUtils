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

import java.util.List;

public class WorldAudience extends ConfigurableAttribute<List<String>> implements InteractionAudienceAttribute {

    public WorldAudience(String key, List<String> value) {
        super(key, value);
    }

    public WorldAudience(List<String> value) {
        super(InteractionAttribute.AUDIENCE_WORLDS.getKey(), value);
    }

    @Override
    public Audience get(CommandSender sender) {
        return Audience.audience(getValue().stream()
                .map(world -> I18n.global.getAudiences().world(Key.key(world))).toList());
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public WorldAudience create(String key, ConfigurationSection config) {
            return new WorldAudience(key, ConfigUtils.getStringList(config, key));
        }
    }

}
