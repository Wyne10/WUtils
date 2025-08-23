package me.wyne.wutils.config.configurables;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import me.wyne.wutils.config.configurables.attribute.*;
import me.wyne.wutils.config.configurables.gui.ClickEventAttribute;
import me.wyne.wutils.config.configurables.gui.ContextClickEventAttribute;
import me.wyne.wutils.config.configurables.gui.GuiItemAttribute;
import me.wyne.wutils.config.configurables.gui.attribute.*;
import me.wyne.wutils.config.configurables.item.ItemAttributeContext;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;

public class GuiConfigurableBase extends ItemConfigurableBase {

    public final static AttributeMap GUI_ITEM_ATTRIBUTE_MAP = new AttributeMap(new LinkedHashMap<>());

    static {
        GUI_ITEM_ATTRIBUTE_MAP.putAll(ImmutableItemConfigurable.ITEM_ATTRIBUTE_MAP.getKeyMap());
        GUI_ITEM_ATTRIBUTE_MAP.put(GuiItemAttribute.SLOT.getKey(), new SlotAttribute.Factory());
        GUI_ITEM_ATTRIBUTE_MAP.put(GuiItemAttribute.PRINT.getKey(), new PrintAttribute.Factory());
        GUI_ITEM_ATTRIBUTE_MAP.put(GuiItemAttribute.SOUND.getKey(), new SoundAttribute.Factory());
        GUI_ITEM_ATTRIBUTE_MAP.put(GuiItemAttribute.COMMAND.getKey(), new CommandAttribute.Factory());
        GUI_ITEM_ATTRIBUTE_MAP.put(GuiItemAttribute.COMMANDS.getKey(), CommandsAttribute::new);
    }

    public GuiConfigurableBase(AttributeContainer attributeContainer) {
        super(attributeContainer);
    }

    public GuiConfigurableBase(AttributeContainer attributeContainer, ConfigurationSection section) {
        super(attributeContainer, section);
    }

    public GuiItem buildGuiItem(ItemAttributeContext context) {
        var itemStack = build(context);
        var actions = getAttributeContainer().getSet(ClickEventAttribute.class);
        return ItemBuilder.from(itemStack)
                .asGuiItem(e -> actions.forEach(attribute -> {
                    if (attribute instanceof ContextClickEventAttribute)
                        ((ContextClickEventAttribute) attribute).apply(e, context);
                    else
                        attribute.apply(e);
                }));
    }

    public GuiItem buildGuiItem(TextReplacement... textReplacements) {
        var context = new ItemAttributeContext(null, textReplacements, new ComponentReplacement[]{});
        return buildGuiItem(context);
    }

    public GuiItem buildGuiItem(Player player, TextReplacement... textReplacements) {
        var context = new ItemAttributeContext(player, textReplacements, new ComponentReplacement[]{});
        return buildGuiItem(context);
    }

    public GuiItem buildGuiItemComponent(ComponentReplacement... componentReplacements) {
        var context = new ItemAttributeContext(null, new TextReplacement[]{}, componentReplacements);
        return buildGuiItem(context);
    }

    public GuiItem buildGuiItemComponent(Player player, ComponentReplacement... componentReplacements) {
        var context = new ItemAttributeContext(player, new TextReplacement[]{}, componentReplacements);
        return buildGuiItem(context);
    }

    public GuiConfigurableBuilder toGuiBuilder() {
        return new GuiConfigurableBuilder(this);
    }

    public static GuiConfigurableBuilder guiBuilder() {
        return new GuiConfigurableBuilder();
    }

}
