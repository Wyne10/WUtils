package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.AttributeBase;
import me.wyne.wutils.config.configurables.item.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class DurabilityAttribute extends AttributeBase<Integer> implements MetaAttribute, ConfigurableAttribute<Integer> {

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

}
