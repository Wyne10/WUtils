package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomModelDataAttribute extends MetaAttribute<Integer> {

    public CustomModelDataAttribute(Integer value) {
        super(ItemAttribute.MODEL.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        meta.setCustomModelData(getValue());
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
