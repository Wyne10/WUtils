package me.wyne.wutils.config.configurables.item.attribute;

import com.google.common.base.Preconditions;
import me.wyne.wutils.common.Args;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.CompositeAttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Adds one enchantment, ignoring level restrictions and conflicts (so e.g. {@code Sharpness 10}
 * or incompatible enchantment pairs are both accepted).
 *
 * <p>Config form is {@code <key> <level>} (a namespaced key, e.g. {@code minecraft:sharpness 5})
 * or a section with {@code enchantment} and {@code level} keys. The string form splits on
 * whitespace only ({@link Args#SPACE_DELIMITER}), unlike {@link GenericAttribute}'s string form,
 * so namespaced keys work here.</p>
 *
 * <p>An unrecognized enchantment key aborts the whole config load: both factory methods
 * {@code Preconditions.checkNotNull} the lookup result, so a bad key throws a
 * {@link NullPointerException} out of {@code fromConfig} rather than skipping just this entry.</p>
 */
public class EnchantmentAttribute extends ConfigurableAttribute<EnchantmentAttribute.EnchantmentData> implements MetaAttribute {

    public EnchantmentAttribute(@NotNull String key, @NotNull EnchantmentData value) {
        super(key, value);
    }

    public EnchantmentAttribute(@NotNull EnchantmentData value) {
        super(ItemAttribute.ENCHANTMENT.getKey(), value);
    }

    @Override
    public void apply(@NotNull ItemMeta meta) {
        meta.addEnchant(getValue().enchantment(), getValue().level(), true);
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().enchantment().getKey() + " " + getValue().level()).buildNoSpace();
    }

    public record EnchantmentData(@NotNull Enchantment enchantment, int level) {}

    public static final class Factory implements CompositeAttributeFactory<EnchantmentAttribute> {
        @Override
        public @NotNull EnchantmentAttribute fromSection(@NotNull String key, @NotNull ConfigurationSection section) {
            var enchantmentKey = Preconditions.checkNotNull(section.getString("enchantment"), "No enchantment provided for " + section.getCurrentPath());
            var enchantment = Enchantment.getByKey(NamespacedKey.fromString(enchantmentKey));
            Preconditions.checkNotNull(enchantment, "Invalid enchantment at " + section.getCurrentPath());
            return new EnchantmentAttribute(
                    key,
                    new EnchantmentData(
                            enchantment,
                            section.getInt("level", 1)
                    )
            );
        }

        @Override
        public @NotNull EnchantmentAttribute fromString(@NotNull String key, @NotNull String string, @NotNull ConfigurationSection config) {
            var args = new Args(string, Args.SPACE_DELIMITER);
            var enchantmentKey = NamespacedKey.fromString(Preconditions.checkNotNull(args.getNullable(0), "No enchantment provided for " + ConfigUtils.getPath(config, key)));
            var enchantment = Enchantment.getByKey(enchantmentKey);
            Preconditions.checkNotNull(enchantment, "Invalid enchantment at " + ConfigUtils.getPath(config, key));
            return new EnchantmentAttribute(
                    key,
                    new EnchantmentData(
                            enchantment,
                            Integer.parseInt(args.get(1, "1"))
                    )
            );
        }
    }

}

