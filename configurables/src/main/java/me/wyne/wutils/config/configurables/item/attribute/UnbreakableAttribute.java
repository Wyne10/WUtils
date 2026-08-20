package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/** Sets the item's unbreakable flag. */
public class UnbreakableAttribute extends ConfigurableAttribute<Boolean> implements MetaAttribute {

    public UnbreakableAttribute(@NotNull String key, @NotNull Boolean value) {
        super(key, value);
    }

    public UnbreakableAttribute(@NotNull Boolean value) {
        super(ItemAttribute.UNBREAKABLE.getKey(), value);
    }

    @Override
    public void apply(@NotNull ItemMeta meta) {
        meta.setUnbreakable(getValue());
    }

    public static final class Factory implements AttributeFactory<UnbreakableAttribute> {
        @Override
        public @NotNull UnbreakableAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new UnbreakableAttribute(key, config.getBoolean(key, false));
        }
    }

}
