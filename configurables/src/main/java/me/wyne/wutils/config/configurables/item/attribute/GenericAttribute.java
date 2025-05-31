package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.common.Args;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.CompositeAttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
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

    public record AttributeData(Attribute attribute, AttributeModifier modifier) {}

    public static final class Factory implements CompositeAttributeFactory {
        @Override
        public GenericAttribute fromSection(String key, ConfigurationSection section) {
            var attribute = getByKey(NamespacedKey.fromString(section.getString("attribute", "generic.armor")));
            return new GenericAttribute(
                    key,
                    new AttributeData(
                            attribute != null ? attribute : org.bukkit.attribute.Attribute.GENERIC_ARMOR,
                            new AttributeModifier(
                                    UUID.fromString(section.getString("uuid", UUID.randomUUID().toString())),
                                    section.getString("name", "Attribute"),
                                    section.getDouble("amount", 1.0),
                                    AttributeModifier.Operation.valueOf(section.getString("operation", "ADD_NUMBER")),
                                    EquipmentSlot.valueOf(section.getString("slot", "HAND"))
                            )
                    )
            );
        }

        @Override
        public GenericAttribute fromString(String key, String string) {
            var args = new Args(string, " ");
            var attribute = getByKey(NamespacedKey.fromString(args.get(0, "generic.armor")));
            return new GenericAttribute(
                    key,
                    new AttributeData(
                            attribute != null ? attribute : org.bukkit.attribute.Attribute.GENERIC_ARMOR,
                            new AttributeModifier(
                                    UUID.randomUUID(),
                                    "Attribute",
                                    Double.parseDouble(args.get(1, "1.0")),
                                    AttributeModifier.Operation.valueOf(args.get(2, "ADD_NUMBER")),
                                    EquipmentSlot.valueOf(args.get(3, "HAND"))
                            )
                    )
            );
        }

        private Attribute getByKey(NamespacedKey key) {
            return Arrays.stream(Attribute.values()).filter(it -> it.getKey().equals(key)).findFirst().orElse(null);
        }
    }

}
