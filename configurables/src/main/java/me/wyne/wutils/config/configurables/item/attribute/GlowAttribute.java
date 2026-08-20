package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Fakes an enchantment glint by adding a real, hidden {@link Enchantment#LURE} enchantment, since
 * Bukkit exposes no glint flag directly. No-ops if the item already has real enchantments —
 * {@code glow} means "glint unless there are real enchantments", not "glint in addition to them".
 *
 * <p>When it does apply, it unconditionally adds {@link ItemFlag#HIDE_ENCHANTS} to hide the fake
 * enchantment line — there is no way to keep the glint without also hiding enchantment lines.
 * Because {@code flags} is registered before {@code glow} in {@code ItemConfigurable} and item
 * flags are additive, a glowing item always hides its enchantment lines, including any
 * deliberately requested via {@code flags}.</p>
 */
public class GlowAttribute extends ConfigurableAttribute<Boolean> implements MetaAttribute {

    public GlowAttribute(@NotNull String key, @NotNull Boolean value) {
        super(key, value);
    }

    public GlowAttribute(@NotNull Boolean value) {
        super(ItemAttribute.GLOW.getKey(), value);
    }

    @Override
    public void apply(@NotNull ItemMeta meta) {
        if (!getValue()) return;
        if (meta.hasEnchants()) return;
        meta.addEnchant(Enchantment.LURE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    public static final class Factory implements AttributeFactory<GlowAttribute> {
        @Override
        public @NotNull GlowAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new GlowAttribute(key, config.getBoolean(key, false));
        }
    }

}
