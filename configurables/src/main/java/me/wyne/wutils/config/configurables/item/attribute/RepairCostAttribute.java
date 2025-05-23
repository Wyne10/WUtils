package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class RepairCostAttribute extends MetaAttribute<Integer> {

    public RepairCostAttribute(Integer value) {
        super(ItemAttribute.REPAIR_COST.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        if (!(meta instanceof Repairable)) return;
        ((Repairable)meta).setRepairCost(getValue());
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
