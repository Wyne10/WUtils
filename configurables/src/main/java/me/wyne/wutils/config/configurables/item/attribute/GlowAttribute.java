package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

public class GlowAttribute extends MetaAttribute<Boolean> {

    public GlowAttribute(Boolean value) {
        super(ItemAttribute.GLOW.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        if (meta.hasEnchants()) return;
        if (!getValue()) return;
        meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

}
