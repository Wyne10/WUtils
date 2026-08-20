package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.CompositeAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/** A section of arbitrarily-named children, each parsed as a {@link PotionEffectAttribute}. */
public class PotionEffectsAttribute extends CompositeAttribute<PotionEffectAttribute> implements MetaAttribute {

    public PotionEffectsAttribute(@NotNull String key, @NotNull Set<@NotNull PotionEffectAttribute> potionEffects) {
        super(key, potionEffects);
    }

    public PotionEffectsAttribute(@NotNull String key, @NotNull ConfigurationSection config) {
        super(key, config, new PotionEffectAttribute.Factory());
    }

    public PotionEffectsAttribute(@NotNull Set<@NotNull PotionEffectAttribute> potionEffects) {
        super(ItemAttribute.POTION_EFFECTS.getKey(), potionEffects);
    }

    public PotionEffectsAttribute(@NotNull ConfigurationSection config) {
        super(ItemAttribute.POTION_EFFECTS.getKey(), config, new PotionEffectAttribute.Factory());
    }

    @Override
    public void apply(@NotNull ItemMeta meta) {
        getValue().forEach(attribute -> attribute.apply(meta));
    }

}

