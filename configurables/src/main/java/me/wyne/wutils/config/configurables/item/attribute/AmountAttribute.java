package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.ItemStackAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/** Sets the stack size. Defaults to {@code 1} when unconfigured. */
public class AmountAttribute extends ConfigurableAttribute<Integer> implements ItemStackAttribute {

    public AmountAttribute(@NotNull String key, @NotNull Integer value) {
        super(key, value);
    }

    public AmountAttribute(@NotNull Integer value) {
        super(ItemAttribute.AMOUNT.getKey(), value);
    }

    @Override
    public void apply(@NotNull ItemStack item) {
        item.setAmount(getValue());
    }

    public static final class Factory implements AttributeFactory<AmountAttribute> {
        @Override
        public @NotNull AmountAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new AmountAttribute(key, config.getInt(key, 1));
        }
    }

}
