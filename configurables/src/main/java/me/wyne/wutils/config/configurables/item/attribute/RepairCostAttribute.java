package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class RepairCostAttribute extends ConfigurableAttribute<Integer> implements MetaAttribute {

    public RepairCostAttribute(String key, Integer value) {
        super(key, value);
    }

    public RepairCostAttribute(Integer value) {
        super(ItemAttribute.REPAIR_COST.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        if (!(meta instanceof Repairable)) return;
        ((Repairable)meta).setRepairCost(getValue());
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public RepairCostAttribute create(String key, ConfigurationSection config) {
            return new RepairCostAttribute(key, config.getInt(key, 1));
        }
    }

}
