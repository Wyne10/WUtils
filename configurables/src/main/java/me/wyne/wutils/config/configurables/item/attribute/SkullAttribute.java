package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class SkullAttribute extends ConfigurableAttribute<OfflinePlayer> implements MetaAttribute {

    public SkullAttribute(String key, OfflinePlayer value) {
        super(key, value);
    }

    public SkullAttribute(OfflinePlayer value) {
        super(ItemAttribute.SKULL.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        if (!(meta instanceof SkullMeta)) return;
        ((SkullMeta)meta).setOwningPlayer(getValue());
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().getName()).buildNoSpace();
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public SkullAttribute create(String key, ConfigurationSection config) {
            UUID uuid = Bukkit.getPlayerUniqueId(config.getString(key));
            return new SkullAttribute(key, Bukkit.getOfflinePlayer(uuid));
        }
    }

}
