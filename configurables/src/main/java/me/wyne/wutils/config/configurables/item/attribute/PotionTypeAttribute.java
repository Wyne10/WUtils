package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class PotionTypeAttribute extends ConfigurableAttribute<PotionType> implements MetaAttribute {

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

    public static final class Factory implements AttributeFactory<PotionTypeAttribute> {
        @Override
        public PotionTypeAttribute create(String key, ConfigurationSection config) {
            return new PotionTypeAttribute(key, ConfigUtils.getByName(config.getString(key, "WATER"), PotionType.class));
        }
    }

}
