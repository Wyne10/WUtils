package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.AttributeBase;
import me.wyne.wutils.config.configurables.item.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomModelDataAttribute extends AttributeBase<Integer> implements MetaAttribute, ConfigurableAttribute<Integer> {

    public CustomModelDataAttribute(String key, Integer value) {
        super(key, value);
    }

    public CustomModelDataAttribute(Integer value) {
        super(ItemAttribute.MODEL.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        meta.setCustomModelData(getValue());
    }

}
