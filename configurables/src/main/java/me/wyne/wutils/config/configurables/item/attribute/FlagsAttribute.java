package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FlagsAttribute extends AttributeBase<ItemFlag[]> implements MetaAttribute, ConfigurableAttribute<ItemFlag[]> {

    public FlagsAttribute(String key, ItemFlag... value) {
        super(key, value);
    }

    public FlagsAttribute(ItemFlag[] value) {
        super(ItemAttribute.FLAGS.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        meta.addItemFlags(getValue());
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().appendCollection(depth, getKey(), Arrays.stream(getValue()).map(ItemFlag::toString).toList()).buildNoSpace();
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public ConfigurableAttribute<?> create(String key, ConfigurationSection config) {
            List<ItemFlag> flags = config.getStringList(key).stream()
                    .map(s -> {
                        try {
                            return ItemFlag.valueOf(s);
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
            return new FlagsAttribute(flags.toArray(ItemFlag[]::new));
        }
    }

}
