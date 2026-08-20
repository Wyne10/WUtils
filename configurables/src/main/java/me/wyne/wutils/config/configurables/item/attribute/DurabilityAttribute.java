package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

/**
 * Sets the item's <em>remaining</em> durability, computed as {@code maxDurability - value}
 * against the material already applied to the stack. {@code material} is registered before
 * {@code durability} in {@code ItemConfigurable} for exactly this reason — a stack with no
 * configured material is still {@code STONE} at this point, whose max durability is {@code 0}.
 * No-ops on any meta that is not {@link Damageable}.
 */
public class DurabilityAttribute extends ConfigurableAttribute<Integer> implements ItemStackAttribute {

    public DurabilityAttribute(@NotNull String key, @NotNull Integer value) {
        super(key, value);
    }

    public DurabilityAttribute(@NotNull Integer value) {
        super(ItemAttribute.DURABILITY.getKey(), value);
    }

    @Override
    public void apply(@NotNull ItemStack item) {
        item.editMeta(meta -> {
            if (!(meta instanceof Damageable)) return;
            var maxDurability = item.getType().getMaxDurability();
            ((Damageable)meta).setDamage(maxDurability - getValue());
        });
    }

    public static final class Factory implements AttributeFactory<DurabilityAttribute> {
        @Override
        public @NotNull DurabilityAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new DurabilityAttribute(key, config.getInt(key, 1));
        }
    }

}
