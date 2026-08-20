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

/**
 * Adds an {@link AttributeModifier} (e.g. {@code generic.attack_damage}) to the item.
 *
 * <p>Config form is {@code <attribute> <amount> <operation> <slot>} or a section with
 * {@code attribute}, {@code amount}, {@code operation}, {@code slot} and optional {@code uuid}
 * keys. The string form splits on colon-or-whitespace (the default {@link Args} delimiter, not
 * {@link Args#SPACE_DELIMITER}), so a namespaced key like {@code minecraft:generic.attack_damage}
 * breaks into extra tokens and fails with a {@link NullPointerException}; only the unqualified
 * attribute name works there. Unlike {@link EnchantmentAttribute}, use the section form for a
 * namespaced key.</p>
 *
 * <p>When {@code uuid} is omitted, a fresh random {@link UUID} is generated per modifier on
 * every load — two items built from the same config after a reload carry different modifier
 * UUIDs, so they will not stack and vanilla will not treat the modifiers as equal.</p>
 *
 * <p>An unrecognized {@code attribute} aborts the whole config load (see
 * {@link EnchantmentAttribute} for why). {@code operation} and {@code slot}, by contrast, are not
 * guarded: {@link ConfigUtils#getByName} silently returns {@code null} for an unmatched value,
 * which is passed straight into {@link AttributeModifier}'s constructor uncaught.</p>
 */
public class GenericAttribute extends ConfigurableAttribute<GenericAttribute.AttributeData> implements MetaAttribute {

    public GenericAttribute(@NotNull String key, @NotNull AttributeData value) {
        super(key, value);
    }

    public GenericAttribute(@NotNull AttributeData value) {
        super(ItemAttribute.ATTRIBUTE.getKey(), value);
    }

    @Override
    public void apply(@NotNull ItemMeta meta) {
        meta.addAttributeModifier(getValue().attribute(), getValue().modifier());
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().attribute().getKey() + " " + getValue().modifier().getAmount() + " " + getValue().modifier().getOperation() + " " + getValue().modifier().getSlot()).buildNoSpace();
    }

    public record AttributeData(@NotNull Attribute attribute, @NotNull AttributeModifier modifier) {}

    public static final class Factory implements CompositeAttributeFactory<GenericAttribute> {
        @Override
        public @NotNull GenericAttribute fromSection(@NotNull String key, @NotNull ConfigurationSection section) {
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
        public @NotNull GenericAttribute fromString(@NotNull String key, @NotNull String string, @NotNull ConfigurationSection config) {
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
