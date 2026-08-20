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
import org.jetbrains.annotations.NotNull;

/**
 * An {@link ItemConfigurable} that additionally builds a {@code triumph-gui} {@link GuiItem} — the
 * item plus its {@link ClickEventAttribute} click handlers. Requires the {@code triumph-gui}
 * dependency.
 *
 * <p>{@link #GUI_ITEM_ATTRIBUTE_MAP} copies {@link ItemConfigurable#ITEM_ATTRIBUTE_MAP} into its own
 * static initialiser at class-load time. A factory added to {@code ITEM_ATTRIBUTE_MAP} after this
 * class has been loaded does not reach GUI items — register to both maps explicitly if item
 * attributes need to apply everywhere.</p>
 *
 * <p>{@link GuiItemAttribute#CLICK} is defined but deliberately never registered here, since a raw
 * click-handling lambda cannot be expressed in YAML; the built {@link GuiItem}'s click handler never
 * cancels the click event itself.</p>
 */
public class GuiConfigurable extends ItemConfigurable {

    public final static AttributeMap GUI_ITEM_ATTRIBUTE_MAP = new AttributeMap();

    static {
        GUI_ITEM_ATTRIBUTE_MAP.putAll(ItemConfigurable.ITEM_ATTRIBUTE_MAP.getKeyMap());
        GUI_ITEM_ATTRIBUTE_MAP.put(GuiItemAttribute.SLOT.getKey(), new SlotAttribute.Factory());
        GUI_ITEM_ATTRIBUTE_MAP.put(GuiItemAttribute.PRINT.getKey(), new PrintAttribute.Factory());
        GUI_ITEM_ATTRIBUTE_MAP.put(GuiItemAttribute.SOUND.getKey(), new SoundAttribute.Factory());
        GUI_ITEM_ATTRIBUTE_MAP.put(GuiItemAttribute.COMMAND.getKey(), new CommandAttribute.Factory());
    }

    public GuiConfigurable() {
        super(new ImmutableAttributeContainer(GUI_ITEM_ATTRIBUTE_MAP));
    }

    public GuiConfigurable(@NotNull ConfigurationSection section) {
        super(new ImmutableAttributeContainer(GUI_ITEM_ATTRIBUTE_MAP), section);
    }

    public GuiConfigurable(@NotNull AttributeContainer attributeContainer) {
        super(attributeContainer);
    }

    public GuiConfigurable(@NotNull AttributeContainer attributeContainer, @NotNull ConfigurationSection section) {
        super(attributeContainer, section);
    }

    public @NotNull GuiItem buildGuiItem(@NotNull ItemAttributeContext context) {
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

    public @NotNull GuiItem buildGuiItem(@NotNull TextReplacement... textReplacements) {
        var context = new ItemAttributeContext(null, textReplacements, new ComponentReplacement[]{});
        return buildGuiItem(context);
    }

    public @NotNull GuiItem buildGuiItem(@NotNull Player player, @NotNull TextReplacement... textReplacements) {
        var context = new ItemAttributeContext(player, textReplacements, new ComponentReplacement[]{});
        return buildGuiItem(context);
    }

    public @NotNull GuiItem buildGuiItemComponent(@NotNull ComponentReplacement... componentReplacements) {
        var context = new ItemAttributeContext(null, new TextReplacement[]{}, componentReplacements);
        return buildGuiItem(context);
    }

    public @NotNull GuiItem buildGuiItemComponent(@NotNull Player player, @NotNull ComponentReplacement... componentReplacements) {
        var context = new ItemAttributeContext(player, new TextReplacement[]{}, componentReplacements);
        return buildGuiItem(context);
    }

    public int getSlot() {
        return getValue(GuiItemAttribute.SLOT.getKey(), 0);
    }

    public static @NotNull AttributeContainerBuilder builder() {
        return new GuiConfigurable().getAttributeContainer().toBuilder();
    }

}
