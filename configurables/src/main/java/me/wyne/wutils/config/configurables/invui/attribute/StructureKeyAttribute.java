package me.wyne.wutils.config.configurables.invui.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.invui.InvUiAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Names the InvUI structure character this item is placed at. Defaults to {@code '.'} when
 * {@code key} is omitted.
 *
 * <p>The factory takes {@code charAt(0)} of the configured string: a multi-character value
 * silently uses only the first character, and an empty string throws
 * {@link StringIndexOutOfBoundsException}.</p>
 */
public class StructureKeyAttribute extends ConfigurableAttribute<Character> {

    public StructureKeyAttribute(@NotNull String key, char value) {
        super(key, value);
    }

    public StructureKeyAttribute(char value) {
        super(InvUiAttribute.KEY.getKey(), value);
    }

    public static final class Factory implements AttributeFactory<StructureKeyAttribute> {
        @Override
        public @NotNull StructureKeyAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new StructureKeyAttribute(key, config.getString(key, ".").charAt(0));
        }
    }

}
