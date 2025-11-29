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

import java.util.*;

public class FlagsAttribute extends ConfigurableAttribute<Set<ItemFlag>> implements MetaAttribute {

    public FlagsAttribute(String key, ItemFlag... value) {
        super(key, Set.of(value));
    }

    public FlagsAttribute(String key, Set<ItemFlag> value) {
        super(key, value);
    }

    public FlagsAttribute(ItemFlag... value) {
        super(ItemAttribute.FLAGS.getKey(), Set.of(value));
    }

    public FlagsAttribute(Set<ItemFlag> value) {
        super(ItemAttribute.FLAGS.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        meta.addItemFlags(getValue().toArray(ItemFlag[]::new));
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().appendCollection(depth, getKey(), getValue().stream().map(ItemFlag::toString).toList()).buildNoSpace();
    }

    public static final class Factory implements AttributeFactory<FlagsAttribute> {
        @Override
        public FlagsAttribute create(String key, ConfigurationSection config) {
            return new FlagsAttribute(key, ConfigUtils.getEnumSet(config, key, ItemFlag.class));
        }
    }

}
