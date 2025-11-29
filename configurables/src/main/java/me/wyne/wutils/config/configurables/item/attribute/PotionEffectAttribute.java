package me.wyne.wutils.config.configurables.item.attribute;

import com.google.common.base.Preconditions;
import me.wyne.wutils.common.Args;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.CompositeAttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectAttribute extends ConfigurableAttribute<PotionEffect> implements MetaAttribute {

    public PotionEffectAttribute(String key, PotionEffect value) {
        super(key, value);
    }

    public PotionEffectAttribute(PotionEffect value) {
        super(ItemAttribute.POTION_EFFECT.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        if (!(meta instanceof PotionMeta)) return;
        ((PotionMeta)meta).addCustomEffect(getValue(), false);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().getType().getName() + " " + getValue().getDuration() + " " + getValue().getAmplifier() + " " + getValue().isAmbient() + " " + getValue().hasParticles() + " " + getValue().hasIcon()).buildNoSpace();
    }

    public static final class Factory implements CompositeAttributeFactory<PotionEffectAttribute> {
        @Override
        public PotionEffectAttribute fromSection(String key, ConfigurationSection section) {
            var typeKey = Preconditions.checkNotNull(section.getString("type"), "No potion type provided for " + section.getCurrentPath());
            PotionEffectType type = PotionEffectType.getByName(typeKey);
            Preconditions.checkNotNull(type, "Invalid potion type at " + section.getCurrentPath());
            return new PotionEffectAttribute(
                    key,
                    new PotionEffect(
                            type,
                            section.getInt("duration", 20),
                            section.getInt("amplifier", 0),
                            section.getBoolean("ambient", true),
                            section.getBoolean("particles", true),
                            section.getBoolean("icon", true)
                    )
            );
        }

        @Override
        public PotionEffectAttribute fromString(String key, String string, ConfigurationSection config) {
            var args = new Args(string);
            var typeKey = Preconditions.checkNotNull(args.getNullable(0), "No potion type provided for " + ConfigUtils.getPath(config, key));
            PotionEffectType type = PotionEffectType.getByName(typeKey);
            Preconditions.checkNotNull(type, "Invalid potion type at " + ConfigUtils.getPath(config, key));
            return new PotionEffectAttribute(
                    key,
                    new PotionEffect(
                            type,
                            Integer.parseInt(args.get(1, "20")),
                            Integer.parseInt(args.get(2, "0")),
                            Boolean.parseBoolean(args.get(3, "true")),
                            Boolean.parseBoolean(args.get(4, "true")),
                            Boolean.parseBoolean(args.get(5, "true"))
                    )
            );
        }
    }

}
