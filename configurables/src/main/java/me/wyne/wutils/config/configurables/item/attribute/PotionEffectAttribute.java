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
import org.jetbrains.annotations.NotNull;

/**
 * Adds one custom {@link PotionEffect} to the item. No-ops on any meta that is not a
 * {@link PotionMeta}.
 *
 * <p>Config form is {@code <type> <duration> <amplifier> <ambient> <particles> <icon>} or a
 * section with the same keys. The string form splits on the default {@link Args} delimiter
 * (colon-or-whitespace, not {@link Args#SPACE_DELIMITER}).</p>
 *
 * <p>An unrecognized potion type aborts the whole config load: both factory methods
 * {@code Preconditions.checkNotNull} the lookup result, so a bad type name throws a
 * {@link NullPointerException} out of {@code fromConfig} rather than skipping just this entry —
 * the same failure mode as {@link EnchantmentAttribute} and {@link SkullAttribute}.</p>
 */
public class PotionEffectAttribute extends ConfigurableAttribute<PotionEffect> implements MetaAttribute {

    public PotionEffectAttribute(@NotNull String key, @NotNull PotionEffect value) {
        super(key, value);
    }

    public PotionEffectAttribute(@NotNull PotionEffect value) {
        super(ItemAttribute.POTION_EFFECT.getKey(), value);
    }

    @Override
    public void apply(@NotNull ItemMeta meta) {
        if (!(meta instanceof PotionMeta)) return;
        ((PotionMeta)meta).addCustomEffect(getValue(), false);
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().getType().getName() + " " + getValue().getDuration() + " " + getValue().getAmplifier() + " " + getValue().isAmbient() + " " + getValue().hasParticles() + " " + getValue().hasIcon()).buildNoSpace();
    }

    public static final class Factory implements CompositeAttributeFactory<PotionEffectAttribute> {
        @Override
        public @NotNull PotionEffectAttribute fromSection(@NotNull String key, @NotNull ConfigurationSection section) {
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
        public @NotNull PotionEffectAttribute fromString(@NotNull String key, @NotNull String string, @NotNull ConfigurationSection config) {
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
