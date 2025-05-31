package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class DurabilityAttribute extends ConfigurableAttribute<Integer> implements MetaAttribute {

    private ItemStack itemStack;

    public DurabilityAttribute(String key, Integer value) {
        super(key, value);
    }

    public DurabilityAttribute(Integer value) {
        super(ItemAttribute.DURABILITY.getKey(), value);
    }

    @Override
    public void apply(ItemStack item) {
        this.itemStack = item;
        MetaAttribute.super.apply(item);
    }

    @Override
    public void apply(ItemMeta meta) {
        if (!(meta instanceof Damageable)) return;
        var maxDurability = itemStack.getType().getMaxDurability();
        ((Damageable)meta).setDamage(maxDurability - getValue());
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public DurabilityAttribute create(String key, ConfigurationSection config) {
            return new DurabilityAttribute(key, config.getInt(key, 1));
        }
    }

}
