package me.wyne.wutils.config.configurables.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class MetaAttribute<V> extends AttributeBase<V> {

    public MetaAttribute(String key, V value) {
        super(key, value);
    }

    @Override
    public void apply(ItemStack item) {
        item.editMeta(this::apply);
    }

    abstract public void apply(ItemMeta meta);

}
