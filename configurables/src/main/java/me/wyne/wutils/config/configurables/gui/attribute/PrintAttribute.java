package me.wyne.wutils.config.configurables.gui.attribute;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.gui.ContextClickEventAttribute;
import me.wyne.wutils.config.configurables.gui.GuiItemAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttributeContext;
import me.wyne.wutils.i18n.I18n;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// TODO Print, sound, command attributes might just reuse new interaction configurable. but i'll leave it as is for now because i don't really use gui anyway
/**
 * Sends configured message lines, resolved through {@code I18n.global.accessor(...)}, to the
 * clicking player on a GUI item click. A minimal reimplementation of what
 * {@code InteractionConfigurable} already provides (see the TODO above); output always goes to
 * the clicker, with no other audience selection.
 */
public class PrintAttribute extends ConfigurableAttribute<List<String>> implements ContextClickEventAttribute {

    public PrintAttribute(@NotNull String key, @NotNull List<@NotNull String> value) {
        super(key, value);
    }

    public PrintAttribute(@NotNull List<@NotNull String> value) {
        super(GuiItemAttribute.PRINT.getKey(), value);
    }

    @Override
    public void apply(@NotNull InventoryClickEvent event, @NotNull ItemAttributeContext context) {
        getValue().stream()
                .map(s -> I18n.global.accessor(context.getPlayer(), s).getPlaceholderComponent(context.getPlayer(), context.getTextReplacements()).replace(context.getComponentReplacements()))
                .forEach(component -> component.sendMessage(event.getWhoClicked()));
    }

    public static final class Factory implements AttributeFactory<PrintAttribute> {
        @Override
        public @NotNull PrintAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new PrintAttribute(key, ConfigUtils.getStringList(config, key));
        }
    }

}
