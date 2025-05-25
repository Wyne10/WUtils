package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.AttributeBase;
import me.wyne.wutils.config.configurables.item.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class DamageAttribute extends AttributeBase<Integer> implements MetaAttribute, ConfigurableAttribute<Integer> {

    public DamageAttribute(String key, Integer value) {
        super(key, value);
    }

    public DamageAttribute(Integer value) {
        super(ItemAttribute.DAMAGE.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        if (!(meta instanceof Damageable)) return;
        ((Damageable)meta).setDamage(getValue());
    }

}
