package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.CompositeAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Set;

public class PotionEffectsAttribute extends CompositeAttribute<PotionEffectAttribute> implements MetaAttribute {

    public PotionEffectsAttribute(String key, Set<PotionEffectAttribute> potionEffects) {
        super(key, potionEffects);
    }

    public PotionEffectsAttribute(String key, ConfigurationSection config) {
        super(key, config, new PotionEffectAttribute.Factory());
    }

    public PotionEffectsAttribute(Set<PotionEffectAttribute> potionEffects) {
        super(ItemAttribute.POTION_EFFECTS.getKey(), potionEffects);
    }

    public PotionEffectsAttribute(ConfigurationSection config) {
        super(ItemAttribute.POTION_EFFECTS.getKey(), config, new PotionEffectAttribute.Factory());
    }

    @Override
    public void apply(ItemMeta meta) {
        getValue().forEach(attribute -> attribute.apply(meta));
    }

}

