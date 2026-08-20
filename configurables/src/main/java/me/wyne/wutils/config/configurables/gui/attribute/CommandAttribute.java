package me.wyne.wutils.config.configurables.gui.attribute;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.gui.ContextClickEventAttribute;
import me.wyne.wutils.config.configurables.gui.GuiItemAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttributeContext;
import me.wyne.wutils.config.configurables.item.ManualAttribute;
import me.wyne.wutils.i18n.I18n;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Dispatches configured commands, resolved through {@code I18n.global.accessor(...)} for
 * placeholders, on a GUI item click. A minimal reimplementation of what
 * {@code InteractionConfigurable} already provides.
 *
 * <p>Always dispatches as the console ({@link Bukkit#getConsoleSender()}), with no player-sender
 * variant, and there is no audience selection. Also implements {@link ManualAttribute}, so its
 * commands can be fired outside a click via {@link #apply(ItemAttributeContext)} — the only
 * shipped use of that interface.</p>
 */
public class CommandAttribute extends ConfigurableAttribute<List<String>> implements ContextClickEventAttribute, ManualAttribute {

    public CommandAttribute(@NotNull String key, @NotNull List<@NotNull String> value) {
        super(key, value);
    }

    public CommandAttribute(@NotNull List<@NotNull String> value) {
        super(GuiItemAttribute.COMMAND.getKey(), value);
    }

    @Override
    public void apply(@NotNull ItemAttributeContext context) {
        getValue().stream()
                .map(s -> I18n.global.accessor(context.getPlayer(), s).getPlaceholderString(context.getPlayer(), context.getTextReplacements()).get())
                .forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }

    @Override
    public void apply(@NotNull InventoryClickEvent event, @NotNull ItemAttributeContext context) {
        getValue().stream()
                .map(s -> I18n.global.accessor(context.getPlayer(), s).getPlaceholderString(event.getWhoClicked(), context.getTextReplacements()).get())
                .forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }

    public static final class Factory implements AttributeFactory<CommandAttribute> {
        @Override
        public @NotNull CommandAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new CommandAttribute(key, ConfigUtils.getStringList(config, key));
        }
    }

}
