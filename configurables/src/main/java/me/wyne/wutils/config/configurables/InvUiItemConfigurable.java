package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.AttributeContainer;
import me.wyne.wutils.config.configurables.attribute.AttributeContainerBuilder;
import me.wyne.wutils.config.configurables.attribute.AttributeMap;
import me.wyne.wutils.config.configurables.attribute.ImmutableAttributeContainer;
import me.wyne.wutils.config.configurables.invui.InvUiAttribute;
import me.wyne.wutils.config.configurables.invui.attribute.StructureKeyAttribute;
import org.bukkit.configuration.ConfigurationSection;

public class InvUiItemConfigurable extends ItemConfigurable {

    public final static AttributeMap INV_UI_ITEM_ATTRIBUTE_MAP = new AttributeMap();

    static {
        INV_UI_ITEM_ATTRIBUTE_MAP.putAll(ItemConfigurable.ITEM_ATTRIBUTE_MAP.getKeyMap());
        INV_UI_ITEM_ATTRIBUTE_MAP.put(InvUiAttribute.KEY.getKey(), new StructureKeyAttribute.Factory());
    }

    public InvUiItemConfigurable() {
        super(new ImmutableAttributeContainer(INV_UI_ITEM_ATTRIBUTE_MAP));
    }

    public InvUiItemConfigurable(ConfigurationSection section) {
        super(new ImmutableAttributeContainer(INV_UI_ITEM_ATTRIBUTE_MAP), section);
    }

    public InvUiItemConfigurable(AttributeContainer attributeContainer) {
        super(attributeContainer);
    }

    public InvUiItemConfigurable(AttributeContainer attributeContainer, ConfigurationSection section) {
        super(attributeContainer, section);
    }

    public char getKey() {
        return getValue(InvUiAttribute.KEY.getKey(), '.');
    }

    public static AttributeContainerBuilder builder() {
        return new InvUiItemConfigurable().getAttributeContainer().toBuilder();
    }

}
