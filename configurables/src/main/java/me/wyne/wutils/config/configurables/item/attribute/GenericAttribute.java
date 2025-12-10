package me.wyne.wutils.config.configurables.item.attribute;

import com.google.common.base.Preconditions;
import me.wyne.wutils.common.Args;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.CompositeAttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GenericAttribute extends ConfigurableAttribute<GenericAttribute.AttributeData> implements MetaAttribute {

    public GenericAttribute(String key, AttributeData value) {
        super(key, value);
    }

    public GenericAttribute(AttributeData value) {
        super(ItemAttribute.ATTRIBUTE.getKey(), value);
    }

    @Override
    public void apply(ItemMeta meta) {
        meta.addAttributeModifier(getValue().attribute(), getValue().modifier());
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().attribute().getKey() + " " + getValue().modifier().getAmount() + " " + getValue().modifier().getOperation() + " " + getValue().modifier().getSlot()).buildNoSpace();
    }

    public record AttributeData(@NotNull Attribute attribute, @NotNull AttributeModifier modifier) {}

    public static final class Factory implements CompositeAttributeFactory<GenericAttribute> {
        @Override
        public GenericAttribute fromSection(String key, ConfigurationSection section) {
            var attributeKey = Preconditions.checkNotNull(section.getString("attribute"), "No attribute provided for " + section.getCurrentPath());
            var attribute = ConfigUtils.getByKeyOrName(attributeKey,  Attribute.class);
            Preconditions.checkNotNull(attribute, "Invalid attribute at " + section.getCurrentPath());
            return new GenericAttribute(
                    key,
                    new AttributeData(
                            attribute,
                            new AttributeModifier(
                                    UUID.fromString(section.getString("uuid", UUID.randomUUID().toString())),
                                    section.getString("name", attributeKey),
                                    section.getDouble("amount", 1.0),
                                    ConfigUtils.getByName(section.getString("operation", "ADD_NUMBER"), AttributeModifier.Operation.class),
                                    ConfigUtils.getByName(section.getString("slot"), EquipmentSlot.class)
                            )
                    )
            );
        }

        @Override
        public GenericAttribute fromString(String key, String string, ConfigurationSection config) {
            var args = new Args(string);
            var attributeKey = Preconditions.checkNotNull(args.getNullable(0), "No attribute provided for " + ConfigUtils.getPath(config, key));
            var attribute = ConfigUtils.getByKeyOrName(attributeKey,  Attribute.class);
            Preconditions.checkNotNull(attribute, "Invalid attribute at " + ConfigUtils.getPath(config, key));
            return new GenericAttribute(
                    key,
                    new AttributeData(
                            attribute,
                            new AttributeModifier(
                                    UUID.randomUUID(),
                                    attributeKey,
                                    Double.parseDouble(args.get(1, "1.0")),
                                    ConfigUtils.getByName(args.get(2, "ADD_NUMBER"), AttributeModifier.Operation.class),
                                    ConfigUtils.getByName(args.getNullable(3), EquipmentSlot.class)
                            )
                    )
            );
        }
    }

}
