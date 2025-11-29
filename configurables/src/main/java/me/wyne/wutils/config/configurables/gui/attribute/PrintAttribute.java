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

import java.util.List;

// TODO Print, sound, command attributes might just reuse new interaction configurable. but i'll leave it as is for now because i don't really use gui anyway
public class PrintAttribute extends ConfigurableAttribute<List<String>> implements ContextClickEventAttribute {

    public PrintAttribute(String key, List<String> value) {
        super(key, value);
    }

    public PrintAttribute(List<String> value) {
        super(GuiItemAttribute.PRINT.getKey(), value);
    }

    @Override
    public void apply(InventoryClickEvent event, ItemAttributeContext context) {
        getValue().stream()
                .map(s -> I18n.global.accessor(context.getPlayer(), s).getPlaceholderComponent(context.getPlayer(), context.getTextReplacements()).replace(context.getComponentReplacements()))
                .forEach(component -> component.sendMessage(event.getWhoClicked()));
    }

    public static final class Factory implements AttributeFactory<PrintAttribute> {
        @Override
        public PrintAttribute create(String key, ConfigurationSection config) {
            return new PrintAttribute(key, ConfigUtils.getStringList(config, key));
        }
    }

}
