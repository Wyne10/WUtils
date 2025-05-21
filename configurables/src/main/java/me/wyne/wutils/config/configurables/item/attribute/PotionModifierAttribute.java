package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class PotionModifierAttribute extends MetaAttribute<PotionModifierAttribute.PotionModifier> {

    public PotionModifierAttribute(PotionModifier value) {
        super(ItemAttribute.POTION_MODIFIER.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        if (!(meta instanceof PotionMeta pmeta)) return;
        var baseData = pmeta.getBasePotionData() == null ? new PotionData(PotionType.WATER, false, false) : pmeta.getBasePotionData();
        switch (getValue()) {
            case NONE -> pmeta.setBasePotionData(new PotionData(baseData.getType(), false, false));
            case EXTENDED -> pmeta.setBasePotionData(new PotionData(baseData.getType(), true, false));
            case UPGRADED -> pmeta.setBasePotionData(new PotionData(baseData.getType(), false, true));
        }
    }

    @Override
    public String toString() {
        return getValue().toString();
    }

    public enum PotionModifier {
        NONE,
        EXTENDED,
        UPGRADED
    }

}
