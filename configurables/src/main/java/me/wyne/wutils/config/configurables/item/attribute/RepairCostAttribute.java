package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;

/** Sets the anvil prior-work repair cost. No-ops on any meta that is not {@link Repairable}. */
public class RepairCostAttribute extends ConfigurableAttribute<Integer> implements MetaAttribute {

    public RepairCostAttribute(@NotNull String key, @NotNull Integer value) {
        super(key, value);
    }

    public RepairCostAttribute(@NotNull Integer value) {
        super(ItemAttribute.REPAIR_COST.getKey(), value);
    }

    @Override
    public void apply(@NotNull ItemMeta meta) {
        if (!(meta instanceof Repairable)) return;
        ((Repairable)meta).setRepairCost(getValue());
    }

    public static final class Factory implements AttributeFactory<RepairCostAttribute> {
        @Override
        public @NotNull RepairCostAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new RepairCostAttribute(key, config.getInt(key, 0));
        }
    }

}
