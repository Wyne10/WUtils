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
 * Sets whether the potion is extended or upgraded, read-modify-write against the item's existing
 * {@link PotionData} so the base {@link PotionType} — owned by {@link PotionTypeAttribute} — is
 * preserved. Applying only one of the two attributes leaves the other at its existing value.
 * No-ops on any meta that is not a {@link PotionMeta}.
 */
public class PotionModifierAttribute extends ConfigurableAttribute<PotionModifierAttribute.PotionModifier> implements MetaAttribute {

    public PotionModifierAttribute(@NotNull String key, @NotNull PotionModifier value) {
        super(key, value);
    }

    public PotionModifierAttribute(@NotNull PotionModifier value) {
        super(ItemAttribute.POTION_MODIFIER.getKey(), value);
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void apply(@NotNull ItemMeta meta) {
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
        public @NotNull PotionModifierAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            var name = config.getString(key, "NONE");
            var modifier = ConfigUtils.getByName(name, PotionModifierAttribute.PotionModifier.class);
            Preconditions.checkNotNull(modifier, "Invalid potion modifier '" + name + "' at " + ConfigUtils.getPath(config, key));
            return new PotionModifierAttribute(key, modifier);
        }
    }

}
