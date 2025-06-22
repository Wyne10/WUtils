package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.ItemStackAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class AmountAttribute extends ConfigurableAttribute<Integer> implements ItemStackAttribute {

    public AmountAttribute(String key, Integer value) {
        super(key, value);
    }

    public AmountAttribute(Integer value) {
        super(ItemAttribute.AMOUNT.getKey(), value);
    }

    @Override
    public void apply(ItemStack item) {
        item.setAmount(getValue());
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public AmountAttribute create(String key, ConfigurationSection config) {
            return new AmountAttribute(key, config.getInt(key, 1));
        }
    }

}
