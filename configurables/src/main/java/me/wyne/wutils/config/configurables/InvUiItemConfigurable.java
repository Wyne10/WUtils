package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.AttributeContainer;
import me.wyne.wutils.config.configurables.attribute.AttributeContainerBuilder;
import me.wyne.wutils.config.configurables.attribute.AttributeMap;
import me.wyne.wutils.config.configurables.attribute.ImmutableAttributeContainer;
import me.wyne.wutils.config.configurables.invui.InvUiAttribute;
import me.wyne.wutils.config.configurables.invui.attribute.StructureKeyAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link ItemConfigurable} that additionally carries the character key used to place it in an
 * InvUI structure string. Requires the {@code InvUI} dependency.
 *
 * <p>{@link #INV_UI_ITEM_ATTRIBUTE_MAP} copies {@link ItemConfigurable#ITEM_ATTRIBUTE_MAP} into its
 * own static initialiser at class-load time — see {@link GuiConfigurable} for the same trap: a
 * factory added to {@code ITEM_ATTRIBUTE_MAP} after this class has been loaded does not reach InvUI
 * items.</p>
 */
public class InvUiItemConfigurable extends ItemConfigurable {

    public final static AttributeMap INV_UI_ITEM_ATTRIBUTE_MAP = new AttributeMap();

    static {
        INV_UI_ITEM_ATTRIBUTE_MAP.putAll(ItemConfigurable.ITEM_ATTRIBUTE_MAP.getKeyMap());
        INV_UI_ITEM_ATTRIBUTE_MAP.put(InvUiAttribute.KEY.getKey(), new StructureKeyAttribute.Factory());
    }

    public InvUiItemConfigurable() {
        super(new ImmutableAttributeContainer(INV_UI_ITEM_ATTRIBUTE_MAP));
    }

    public InvUiItemConfigurable(@NotNull ConfigurationSection section) {
        super(new ImmutableAttributeContainer(INV_UI_ITEM_ATTRIBUTE_MAP), section);
    }

    public InvUiItemConfigurable(@NotNull AttributeContainer attributeContainer) {
        super(attributeContainer);
    }

    public InvUiItemConfigurable(@NotNull AttributeContainer attributeContainer, @NotNull ConfigurationSection section) {
        super(attributeContainer, section);
    }

    public char getKey() {
        return getValue(InvUiAttribute.KEY.getKey(), '.');
    }

    public static @NotNull AttributeContainerBuilder builder() {
        return new InvUiItemConfigurable().getAttributeContainer().toBuilder();
    }

}
