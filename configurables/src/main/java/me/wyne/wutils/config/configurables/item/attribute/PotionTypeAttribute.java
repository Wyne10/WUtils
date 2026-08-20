package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.common.config.ConfigUtils;
import com.google.common.base.Preconditions;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

/**
 * Sets the base {@link PotionType}, read-modify-write against the item's existing
 * {@link PotionData} so the extended/upgraded flags — owned by {@link PotionModifierAttribute} —
 * are preserved. Applying only one of the two attributes leaves the other at its existing value.
 * No-ops on any meta that is not a {@link PotionMeta}.
 */
public class PotionTypeAttribute extends ConfigurableAttribute<PotionType> implements MetaAttribute {

    public PotionTypeAttribute(@NotNull String key, @NotNull PotionType value) {
        super(key, value);
    }

    public PotionTypeAttribute(@NotNull PotionType value) {
        super(ItemAttribute.POTION_TYPE.getKey(), value);
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void apply(@NotNull ItemMeta meta) {
        if (!(meta instanceof PotionMeta pmeta)) return;
        var baseData = pmeta.getBasePotionData() == null ? new PotionData(getValue(), false, false) : pmeta.getBasePotionData();
        pmeta.setBasePotionData(new PotionData(getValue(), baseData.isExtended(), baseData.isUpgraded()));
    }

    public static final class Factory implements AttributeFactory<PotionTypeAttribute> {
        @Override
        public @NotNull PotionTypeAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            var name = config.getString(key, "WATER");
            var type = ConfigUtils.getByName(name, PotionType.class);
            Preconditions.checkNotNull(type, "Invalid potion type '" + name + "' at " + ConfigUtils.getPath(config, key));
            return new PotionTypeAttribute(key, type);
        }
    }

}
