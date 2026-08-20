package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.common.ColorAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Sets potion liquid color. No-ops on any meta that is not a {@link PotionMeta}.
 *
 * <p>Delegates to {@link ColorAttribute}'s factory, like {@link ArmorColorAttribute}, so the
 * config value accepts hex ({@code '#RRGGBB'}, with or without the {@code #}), a named
 * {@link Color} constant, or a {@code red}/{@code green}/{@code blue} section.</p>
 */
public class PotionColorAttribute extends ColorAttribute implements MetaAttribute {

    public PotionColorAttribute(@NotNull String key, @NotNull Color value) {
        super(key, value);
    }

    public PotionColorAttribute(@NotNull Color value) {
        super(ItemAttribute.POTION_COLOR.getKey(), value);
    }

    @Override
    public void apply(@NotNull ItemMeta meta) {
        if (!(meta instanceof PotionMeta)) return;
        ((PotionMeta)meta).setColor(getValue());
    }

    public static final class Factory implements AttributeFactory<PotionColorAttribute> {
        @Override
        public @NotNull PotionColorAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new PotionColorAttribute(key, new ColorAttribute.Factory().create(key, config).getValue());
        }
    }

}
