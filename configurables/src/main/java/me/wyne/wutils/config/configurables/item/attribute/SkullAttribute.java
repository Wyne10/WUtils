package me.wyne.wutils.config.configurables.item.attribute;

import com.google.common.base.Preconditions;
import me.wyne.wutils.common.config.ConfigUtils;
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
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Sets a {@link SkullMeta} owner by player name. No-ops on any meta that is not a
 * {@link SkullMeta}.
 *
 * <p>The name must be one the server has already seen: {@link Bukkit#getPlayerUniqueId}
 * returning {@code null} for an unknown name aborts the whole config load via
 * {@code Preconditions.checkNotNull} — the message does name the config path. Prefer
 * {@link Skull64Attribute} for a fixed texture that does not depend on server lookups.</p>
 */
public class SkullAttribute extends ConfigurableAttribute<OfflinePlayer> implements MetaAttribute {

    public SkullAttribute(@NotNull String key, @NotNull OfflinePlayer value) {
        super(key, value);
    }

    public SkullAttribute(@NotNull OfflinePlayer value) {
        super(ItemAttribute.SKULL.getKey(), value);
    }

    @Override
    public void apply(@NotNull ItemMeta meta) {
        if (!(meta instanceof SkullMeta)) return;
        ((SkullMeta)meta).setOwningPlayer(getValue());
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().getName()).buildNoSpace();
    }

    public static final class Factory implements AttributeFactory<SkullAttribute> {
        @Override
        public @NotNull SkullAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            UUID uuid = Bukkit.getPlayerUniqueId(config.getString(key, ""));
            Preconditions.checkNotNull(uuid, "Invalid UUID at " + ConfigUtils.getPath(config, key));
            return new SkullAttribute(key, Bukkit.getOfflinePlayer(uuid));
        }
    }

}
