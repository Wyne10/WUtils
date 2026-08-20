package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.CompositeAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/** A section of arbitrarily-named children, each parsed as an {@link EnchantmentAttribute}. */
public class EnchantmentsAttribute extends CompositeAttribute<EnchantmentAttribute> implements MetaAttribute {

    public EnchantmentsAttribute(@NotNull String key, @NotNull Set<@NotNull EnchantmentAttribute> enchantments) {
        super(key, enchantments);
    }

    public EnchantmentsAttribute(@NotNull String key, @NotNull ConfigurationSection config) {
        super(key, config, new EnchantmentAttribute.Factory());
    }

    public EnchantmentsAttribute(@NotNull Set<@NotNull EnchantmentAttribute> enchantments) {
        super(ItemAttribute.ENCHANTMENTS.getKey(), enchantments);
    }

    public EnchantmentsAttribute(@NotNull ConfigurationSection config) {
        super(ItemAttribute.ENCHANTMENTS.getKey(), config, new EnchantmentAttribute.Factory());
    }

    @Override
    public void apply(@NotNull ItemMeta meta) {
        getValue().forEach(attribute -> attribute.apply(meta));
    }

}

