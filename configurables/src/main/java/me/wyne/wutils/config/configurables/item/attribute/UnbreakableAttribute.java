package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.inventory.meta.ItemMeta;

public class UnbreakableAttribute extends MetaAttribute<Boolean> {

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

}
