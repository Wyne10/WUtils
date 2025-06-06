package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class DamageAttribute extends ConfigurableAttribute<Integer> implements MetaAttribute {

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

    public static final class Factory implements AttributeFactory {
        @Override
        public DamageAttribute create(String key, ConfigurationSection config) {
            return new DamageAttribute(key, config.getInt(key, 0));
        }
    }

}
