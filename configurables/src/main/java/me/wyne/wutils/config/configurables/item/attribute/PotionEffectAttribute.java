package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.common.Args;
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
        return new ConfigBuilder().append(depth, getKey(), getValue().getType() + " " + getValue().getDuration() + " " + getValue().getAmplifier() + " " + getValue().isAmbient() + " " + getValue().hasParticles() + " " + getValue().hasIcon()).buildNoSpace();
    }

    public static final class Factory implements CompositeAttributeFactory {
        @Override
        public PotionEffectAttribute fromSection(String key, ConfigurationSection section) {
            PotionEffectType type = PotionEffectType.getByName(section.getString("type", "SPEED"));
            return new PotionEffectAttribute(
                    key,
                    new PotionEffect(
                            type != null ? type : PotionEffectType.SPEED,
                            section.getInt("duration", 20),
                            section.getInt("amplifier", 1),
                            section.getBoolean("ambient", false),
                            section.getBoolean("particles", false),
                            section.getBoolean("icon", true)
                    )
            );
        }

        @Override
        public PotionEffectAttribute fromString(String key, String string) {
            var args = new Args(string, " ");
            PotionEffectType type = PotionEffectType.getByName(args.get(0, "SPEED"));
            return new PotionEffectAttribute(
                    key,
                    new PotionEffect(
                            type != null ? type : PotionEffectType.SPEED,
                            Integer.parseInt(args.get(1, "20")),
                            Integer.parseInt(args.get(2, "1")),
                            Boolean.parseBoolean(args.get(3, "false")),
                            Boolean.parseBoolean(args.get(4, "false")),
                            Boolean.parseBoolean(args.get(5, "true"))
                    )
            );
        }
    }

}
