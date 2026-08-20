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
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Sets a {@link SkullMeta} owner profile from a fixed base64 texture value. No-ops on any meta
 * that is not a {@link SkullMeta}.
 *
 * <p>Resolved {@link PlayerProfile}s are cached in a static map keyed by the base64 string that
 * never evicts — fine for a fixed set of config-declared heads, not for textures built from user
 * input.</p>
 */
public class Skull64Attribute extends ConfigurableAttribute<String> implements MetaAttribute {

    private static final Map<String, PlayerProfile> CACHED_PROFILES = new HashMap<>();

    public Skull64Attribute(@NotNull String key, @NotNull String value) {
        super(key, value);
    }

    public Skull64Attribute(@NotNull String value) {
        super(ItemAttribute.SKULL64.getKey(), value);
    }

    @Override
    public void apply(@NotNull ItemMeta meta) {
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
        public @NotNull Skull64Attribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new Skull64Attribute(key, config.getString(key, ""));
        }
    }

}
