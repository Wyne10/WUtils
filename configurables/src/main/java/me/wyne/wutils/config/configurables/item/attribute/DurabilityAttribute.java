package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class DurabilityAttribute extends ConfigurableAttribute<Integer> implements ItemStackAttribute {

    public DurabilityAttribute(String key, Integer value) {
        super(key, value);
    }

    public DurabilityAttribute(Integer value) {
        super(ItemAttribute.DURABILITY.getKey(), value);
    }

    @Override
    public void apply(ItemStack item) {
        item.editMeta(meta -> {
            if (!(meta instanceof Damageable)) return;
            var maxDurability = item.getType().getMaxDurability();
            ((Damageable)meta).setDamage(maxDurability - getValue());
        });
    }

    public static final class Factory implements AttributeFactory<DurabilityAttribute> {
        @Override
        public DurabilityAttribute create(String key, ConfigurationSection config) {
            return new DurabilityAttribute(key, config.getInt(key, 1));
        }
    }

}
