package me.wyne.wutils.config.configurables.item.attribute;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Skull64Attribute extends ConfigurableAttribute<String> implements MetaAttribute {

    private static final Map<String, PlayerProfile> CACHED_PROFILES = new HashMap<>();

    public Skull64Attribute(String key, String value) {
        super(key, value);
    }

    public Skull64Attribute(String value) {
        super(ItemAttribute.SKULL64.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        if (!(meta instanceof SkullMeta)) return;
        if (CACHED_PROFILES.containsKey(getValue())) {
            ((SkullMeta)meta).setPlayerProfile(CACHED_PROFILES.get(getValue()));
            return;
        }
        var profile = Bukkit.createProfile(UUID.nameUUIDFromBytes(getValue().getBytes(StandardCharsets.UTF_8)));
        profile.setProperty(new ProfileProperty("textures", getValue()));
        CACHED_PROFILES.put(getValue(), profile);
        ((SkullMeta)meta).setPlayerProfile(profile);
    }

    public static final class Factory implements AttributeFactory<Skull64Attribute> {
        @Override
        public Skull64Attribute create(String key, ConfigurationSection config) {
            return new Skull64Attribute(key, config.getString(key, ""));
        }
    }

}
