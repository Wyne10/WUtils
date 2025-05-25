package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;

public class UnbreakableAttribute extends AttributeBase<Boolean> implements MetaAttribute, ConfigurableAttribute<Boolean> {

    public UnbreakableAttribute(String key, Boolean value) {
        super(key, value);
    }

    public UnbreakableAttribute(Boolean value) {
        super(ItemAttribute.UNBREAKABLE.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        meta.setUnbreakable(getValue());
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public ConfigurableAttribute<?> create(String key, ConfigurationSection config) {
            return new UnbreakableAttribute(config.getBoolean(key, false));
        }
    }

}
