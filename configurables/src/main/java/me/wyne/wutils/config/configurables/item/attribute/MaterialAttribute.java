package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class MaterialAttribute extends ConfigurableAttribute<Material> implements ItemStackAttribute {

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

    public static final class Factory implements AttributeFactory {
        @Override
        public MaterialAttribute create(String key, ConfigurationSection config) {
            return new MaterialAttribute(key, Material.matchMaterial(config.getString(key, "STONE")));
        }
    }

}
