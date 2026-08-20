package me.wyne.wutils.config.configurables.gui.attribute;

import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.gui.GuiItemAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Carries the configured inventory slot as inert data — nothing in this module places the item;
 * {@code GuiConfigurable.getSlot()} exists so the consumer's GUI-building code can. Defaults to
 * {@code 0}, so an unconfigured button reads back as slot 0, not as "no slot".
 */
public class SlotAttribute extends ConfigurableAttribute<Integer> {

    public SlotAttribute(@NotNull String key, @NotNull Integer value) {
        super(key, value);
    }

    public SlotAttribute(@NotNull Integer value) {
        super(GuiItemAttribute.SLOT.getKey(), value);
    }

    public static final class Factory implements AttributeFactory<SlotAttribute> {
        @Override
        public @NotNull SlotAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new SlotAttribute(key, config.getInt(key, 0));
        }
    }

}
