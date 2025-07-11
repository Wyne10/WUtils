package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.item.CompositeAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttributeContext;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class PotionEffectsAttribute extends CompositeAttribute<PotionEffectAttribute> implements MetaAttribute {

    public PotionEffectsAttribute(String key, Map<String, PotionEffectAttribute> potionEffects) {
        super(key, potionEffects);
    }

    public PotionEffectsAttribute(String key, ConfigurationSection config) {
        super(key, config, new GenericAttribute.Factory());
    }

    public PotionEffectsAttribute(Map<String, PotionEffectAttribute> potionEffects) {
        super(ItemAttribute.POTION_EFFECTS.getKey(), potionEffects);
    }

    public PotionEffectsAttribute(ConfigurationSection config) {
        super(ItemAttribute.POTION_EFFECTS.getKey(), config, new PotionEffectAttribute.Factory());
    }

    @Override
    public void apply(ItemMeta meta) {
        getValue().values().forEach(attribute -> attribute.apply(meta));
    }

    @Override
    public void apply(ItemMeta meta, ItemAttributeContext context) {
        getValue().values().forEach(attribute -> attribute.apply(meta, context));
    }

}

