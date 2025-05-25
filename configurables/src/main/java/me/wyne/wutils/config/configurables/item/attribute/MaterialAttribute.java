package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.AttributeBase;
import me.wyne.wutils.config.configurables.item.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.ItemStackAttribute;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MaterialAttribute extends AttributeBase<Material> implements ItemStackAttribute, ConfigurableAttribute<Material> {

    public MaterialAttribute(String key, Material value) {
        super(key, value);
    }

    public MaterialAttribute(Material value) {
        super(ItemAttribute.MATERIAL.getKey(), value);
    }

    @Override
    public void apply(ItemStack item) {
        item.setType(getValue());
    }

}
