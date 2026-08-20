package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.configurables.attribute.CompositeAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.MetaAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/** A section of arbitrarily-named children, each parsed as a {@link GenericAttribute}. */
public class GenericsAttribute extends CompositeAttribute<GenericAttribute> implements MetaAttribute {

    public GenericsAttribute(@NotNull String key, @NotNull Set<@NotNull GenericAttribute> attributes) {
        super(key, attributes);
    }

    public GenericsAttribute(@NotNull String key, @NotNull ConfigurationSection config) {
        super(key, config, new GenericAttribute.Factory());
    }

    public GenericsAttribute(@NotNull Set<@NotNull GenericAttribute> attributes) {
        super(ItemAttribute.ATTRIBUTES.getKey(), attributes);
    }

    public GenericsAttribute(@NotNull ConfigurationSection config) {
        super(ItemAttribute.ATTRIBUTES.getKey(), config, new GenericAttribute.Factory());
    }

    @Override
    public void apply(@NotNull ItemMeta meta) {
        getValue().forEach(attribute -> attribute.apply(meta));
    }

}

