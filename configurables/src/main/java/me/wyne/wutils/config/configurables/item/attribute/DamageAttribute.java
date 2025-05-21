package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class DamageAttribute extends MetaAttribute<Integer> {

    public DamageAttribute(Integer value) {
        super(ItemAttribute.DAMAGE.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        if (!(meta instanceof Damageable)) return;
        ((Damageable)meta).setDamage(getValue());
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
