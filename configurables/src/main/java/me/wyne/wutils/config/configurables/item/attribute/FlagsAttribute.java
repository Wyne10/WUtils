package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.ListConfigurable;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class FlagsAttribute extends MetaAttribute<ItemFlag[]> {

    public FlagsAttribute(ItemFlag[] value) {
        super(ItemAttribute.FLAGS.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        meta.addItemFlags(getValue());
    }

    @Override
    public String toString() {
        return new ListConfigurable<>(Arrays.stream(getValue()).map(ItemFlag::toString).toList()).toConfig(0, null);
    }

}
