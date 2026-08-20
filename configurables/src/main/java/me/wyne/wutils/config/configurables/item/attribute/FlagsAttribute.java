package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Adds {@link ItemFlag}s to the item. Registered early in {@code ItemConfigurable}, so it is
 * additive with the flag {@link GlowAttribute} adds to hide the fake glint's enchantment line.
 */
public class FlagsAttribute extends ConfigurableAttribute<Set<ItemFlag>> implements MetaAttribute {

    public FlagsAttribute(@NotNull String key, @NotNull ItemFlag... value) {
        super(key, Set.of(value));
    }

    public FlagsAttribute(@NotNull String key, @NotNull Set<@NotNull ItemFlag> value) {
        super(key, value);
    }

    public FlagsAttribute(@NotNull ItemFlag... value) {
        super(ItemAttribute.FLAGS.getKey(), Set.of(value));
    }

    public FlagsAttribute(@NotNull Set<@NotNull ItemFlag> value) {
        super(ItemAttribute.FLAGS.getKey(), value);
    }

    @Override
    public void apply(@NotNull ItemMeta meta) {
        meta.addItemFlags(getValue().toArray(ItemFlag[]::new));
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder().appendCollection(depth, getKey(), getValue().stream().map(ItemFlag::toString).toList()).buildNoSpace();
    }

    public static final class Factory implements AttributeFactory<FlagsAttribute> {
        @Override
        public @NotNull FlagsAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new FlagsAttribute(key, ConfigUtils.getEnumSet(config, key, ItemFlag.class));
        }
    }

}
