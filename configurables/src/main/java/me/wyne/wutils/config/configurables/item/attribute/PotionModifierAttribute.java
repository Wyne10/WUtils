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

public class PotionModifierAttribute extends ConfigurableAttribute<PotionModifierAttribute.PotionModifier> implements MetaAttribute {

    public PotionModifierAttribute(String key, PotionModifier value) {
        super(key, value);
    }

    public PotionModifierAttribute(PotionModifier value) {
        super(ItemAttribute.POTION_MODIFIER.getKey(), value);
    }

    @SuppressWarnings("ConstantValue")
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

    public enum PotionModifier {
        NONE,
        EXTENDED,
        UPGRADED
    }

    public static final class Factory implements AttributeFactory<PotionModifierAttribute> {
        @Override
        public PotionModifierAttribute create(String key, ConfigurationSection config) {
            return new PotionModifierAttribute(key, ConfigUtils.getByName(config.getString(key, "NONE"), PotionModifierAttribute.PotionModifier.class));
        }
    }

}
