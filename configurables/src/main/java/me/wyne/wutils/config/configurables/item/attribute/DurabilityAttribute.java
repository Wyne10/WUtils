package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class DurabilityAttribute extends MetaAttribute<Integer> {

    private ItemStack itemStack;

    public DurabilityAttribute(Integer value) {
        super(ItemAttribute.DURABILITY.getKey(), value);
    }

    @Override
    public void apply(ItemStack item) {
        this.itemStack = item;
        super.apply(item);
    }

    @Override
    public void apply(ItemMeta meta) {
        if (!(meta instanceof Damageable)) return;
        var maxDurability = itemStack.getType().getMaxDurability();
        ((Damageable)meta).setDamage(maxDurability - getValue());
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
