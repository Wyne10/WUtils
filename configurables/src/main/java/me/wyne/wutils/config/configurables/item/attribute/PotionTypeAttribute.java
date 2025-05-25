package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.AttributeBase;
import me.wyne.wutils.config.configurables.item.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class PotionTypeAttribute extends AttributeBase<PotionType> implements MetaAttribute, ConfigurableAttribute<PotionType> {

    public PotionTypeAttribute(String key, PotionType value) {
        super(key, value);
    }

    public PotionTypeAttribute(PotionType value) {
        super(ItemAttribute.POTION_TYPE.getKey(), value);
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void apply(ItemMeta meta) {
        if (!(meta instanceof PotionMeta pmeta)) return;
        var baseData = pmeta.getBasePotionData() == null ? new PotionData(getValue(), false, false) : pmeta.getBasePotionData();
        pmeta.setBasePotionData(new PotionData(getValue(), baseData.isExtended(), baseData.isUpgraded()));
    }

}
