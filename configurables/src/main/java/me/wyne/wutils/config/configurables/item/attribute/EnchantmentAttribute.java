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

public class EnchantmentAttribute extends ConfigurableAttribute<EnchantmentAttribute.EnchantmentData> implements MetaAttribute {

    public EnchantmentAttribute(String key, EnchantmentData value) {
        super(key, value);
    }

    public EnchantmentAttribute(EnchantmentData value) {
        super(ItemAttribute.ENCHANTMENT.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        meta.addEnchant(getValue().enchantment(), getValue().level(), true);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().enchantment().getKey() + " " + getValue().level()).buildNoSpace();
    }

    public record EnchantmentData(@NotNull Enchantment enchantment, int level) {}

    public static final class Factory implements CompositeAttributeFactory<EnchantmentAttribute> {
        @Override
        public EnchantmentAttribute fromSection(String key, ConfigurationSection section) {
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
        public EnchantmentAttribute fromString(String key, String string, ConfigurationSection config) {
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

