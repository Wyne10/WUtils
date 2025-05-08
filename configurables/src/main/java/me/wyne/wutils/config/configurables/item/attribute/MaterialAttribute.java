package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.AttributeBase;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MaterialAttribute extends AttributeBase<Material> {

    public MaterialAttribute(Material value) {
        super(ItemAttribute.MATERIAL.getKey(), value);
    }

    @Override
    public void apply(ItemStack item) {
        item.setType(getValue());
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

}
