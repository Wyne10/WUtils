package me.wyne.wutils.config.configurables.gui.attribute;

import dev.triumphteam.gui.components.GuiAction;
import me.wyne.wutils.config.configurables.attribute.AttributeBase;
import me.wyne.wutils.config.configurables.gui.ClickEventAttribute;
import me.wyne.wutils.config.configurables.gui.GuiItemAttribute;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Wraps an arbitrary triumph-gui {@link GuiAction} as an attribute. Code-only: it cannot be
 * parsed from YAML (a lambda has no config form) and it extends plain {@link AttributeBase}
 * rather than {@code ConfigurableAttribute}, so it never appears in generated config. Attach one
 * through an accessor, e.g. {@code base.getImmutableAccessor().with(new GuiActionAttribute(...))}.
 */
public class GuiActionAttribute extends AttributeBase<GuiAction<InventoryClickEvent>> implements ClickEventAttribute {

    public GuiActionAttribute(@NotNull String key, @NotNull GuiAction<InventoryClickEvent> value) {
        super(key, value);
    }

    public GuiActionAttribute(@NotNull GuiAction<InventoryClickEvent> value) {
        super(GuiItemAttribute.CLICK.getKey(), value);
    }

    @Override
    public void apply(@NotNull InventoryClickEvent event) {
        getValue().execute(event);
    }

}
