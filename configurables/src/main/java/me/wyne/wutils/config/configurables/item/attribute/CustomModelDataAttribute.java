package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/** Sets {@link ItemMeta#setCustomModelData(Integer) CustomModelData}. */
public class CustomModelDataAttribute extends ConfigurableAttribute<Integer> implements MetaAttribute {

    public CustomModelDataAttribute(@NotNull String key, @NotNull Integer value) {
        super(key, value);
    }

    public CustomModelDataAttribute(@NotNull Integer value) {
        super(ItemAttribute.MODEL.getKey(), value);
    }

    @Override
    public void apply(@NotNull ItemMeta meta) {
        meta.setCustomModelData(getValue());
    }

    public static final class Factory implements AttributeFactory<CustomModelDataAttribute> {
        @Override
        public @NotNull CustomModelDataAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new CustomModelDataAttribute(key, config.getInt(key));
        }
    }

}
