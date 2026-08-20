package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Sets the item's raw damage value directly. No-ops on any meta that is not
 * {@link Damageable}. {@code damage} is registered after {@code durability} in
 * {@code ItemConfigurable}, so if both are configured this attribute wins.
 */
public class DamageAttribute extends ConfigurableAttribute<Integer> implements MetaAttribute {

    public DamageAttribute(@NotNull String key, @NotNull Integer value) {
        super(key, value);
    }

    public DamageAttribute(@NotNull Integer value) {
        super(ItemAttribute.DAMAGE.getKey(), value);
    }

    @Override
    public void apply(@NotNull ItemMeta meta) {
        if (!(meta instanceof Damageable)) return;
        ((Damageable)meta).setDamage(getValue());
    }

    public static final class Factory implements AttributeFactory<DamageAttribute> {
        @Override
        public @NotNull DamageAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new DamageAttribute(key, config.getInt(key, 0));
        }
    }

}
