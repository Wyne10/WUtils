package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.item.AttributeBase;
import me.wyne.wutils.config.configurables.item.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

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

}
