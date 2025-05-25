package me.wyne.wutils.config.configurables.item.attribute;

import com.destroystokyo.paper.profile.ProfileProperty;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class Skull64Attribute extends AttributeBase<String> implements MetaAttribute, ConfigurableAttribute<String> {

    public Skull64Attribute(String key, String value) {
        super(key, value);
    }

    public Skull64Attribute(String value) {
        super(ItemAttribute.SKULL64.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        if (!(meta instanceof SkullMeta)) return;
        var profile = Bukkit.createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", getValue()));
        ((SkullMeta)meta).setPlayerProfile(profile);
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public ConfigurableAttribute<?> create(String key, ConfigurationSection config) {
            return new Skull64Attribute(config.getString(key));
        }
    }

}
