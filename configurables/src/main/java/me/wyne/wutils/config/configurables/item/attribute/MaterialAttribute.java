package me.wyne.wutils.config.configurables.item.attribute;

import com.google.common.base.Preconditions;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Sets the stack's type. Defaults to {@code STONE} when {@code material} is omitted.
 */
public class MaterialAttribute extends ConfigurableAttribute<Material> implements ItemStackAttribute {

    public MaterialAttribute(@NotNull String key, @NotNull Material value) {
        super(key, value);
    }

    public MaterialAttribute(@NotNull Material value) {
        super(ItemAttribute.MATERIAL.getKey(), value);
    }

    @Override
    public void apply(@NotNull ItemStack item) {
        item.setType(getValue());
    }

    public static final class Factory implements AttributeFactory<MaterialAttribute> {
        @Override
        public @NotNull MaterialAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            var name = config.getString(key, "STONE");
            var material = Material.matchMaterial(name);
            Preconditions.checkNotNull(material, "Invalid material '" + name + "' at " + ConfigUtils.getPath(config, key));
            return new MaterialAttribute(key, material);
        }
    }

}
