package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.Color;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

public class PotionColorAttribute extends MetaAttribute<Color> {

    public PotionColorAttribute(Color value) {
        super(ItemAttribute.POTION_COLOR.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        if (!(meta instanceof PotionMeta)) return;
        ((PotionMeta)meta).setColor(getValue());
    }

    @Override
    public String toString() {
        return String.valueOf(getValue().asRGB());
    }

}
