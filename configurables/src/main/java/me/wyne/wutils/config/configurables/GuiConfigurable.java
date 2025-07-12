package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.configurables.attribute.*;
import me.wyne.wutils.config.configurables.gui.GuiItemAttribute;
import me.wyne.wutils.config.configurables.gui.attribute.*;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

public class GuiConfigurable extends ItemConfigurable {

    public final static AttributeMap GUI_ITEM_ATTRIBUTE_MAP = new AttributeMap(new LinkedHashMap<>());

    static {
        GUI_ITEM_ATTRIBUTE_MAP.putAll(ItemConfigurable.ITEM_ATTRIBUTE_MAP.getKeyMap());
        GUI_ITEM_ATTRIBUTE_MAP.put(GuiItemAttribute.SLOT.getKey(), new SlotAttribute.Factory());
        GUI_ITEM_ATTRIBUTE_MAP.put(GuiItemAttribute.PRINT.getKey(), new PrintAttribute.Factory());
        GUI_ITEM_ATTRIBUTE_MAP.put(GuiItemAttribute.SOUND.getKey(), new SoundAttribute.Factory());
        GUI_ITEM_ATTRIBUTE_MAP.put(GuiItemAttribute.COMMAND.getKey(), new CommandAttribute.Factory());
        GUI_ITEM_ATTRIBUTE_MAP.put(GuiItemAttribute.COMMANDS.getKey(), CommandsAttribute::new);
    }

    public GuiConfigurable() {
        super(new ImmutableAttributeContainer(GUI_ITEM_ATTRIBUTE_MAP, new LinkedHashMap<>()));
    }

    public GuiConfigurable(ConfigurationSection section) {
        this();
        fromConfig(section);
    }

    public GuiConfigurable(AttributeContainer attributeContainer) {
        super(attributeContainer);
    }

    public GuiConfigurable(AttributeContainer attributeContainer, ConfigurationSection section) {
        super(attributeContainer);
        fromConfig(section);
    }

    public GuiConfigurable ignore(GuiItemAttribute... ignore) {
        for (GuiItemAttribute ignoreAttribute : ignore)
            attributeContainer.ignore(ignoreAttribute.getKey());
        return this;
    }

    public GuiConfigurable with(GuiConfigurable guiConfigurable) {
        attributeContainer.with(guiConfigurable.getAttributeContainer());
        return this;
    }

    public GuiConfigurable copy(GuiConfigurable guiConfigurable) {
        attributeContainer.copy(guiConfigurable.getAttributeContainer());
        return this;
    }

    @Override
    public GuiConfigurable copy() {
        return new GuiConfigurable(attributeContainer.copy());
    }

    @Nullable
    public <T> T get(GuiItemAttribute attribute) {
        return attributeContainer.get(attribute.getKey());
    }

    public <T> T get(GuiItemAttribute attribute, T def) {
        return attributeContainer.get(attribute.getKey(), def);
    }

    @Nullable
    public <V> Attribute<V> getAttribute(GuiItemAttribute attribute) {
        return attributeContainer.getAttribute(attribute.getKey());
    }

    public <V> Attribute<V> getAttribute(GuiItemAttribute attribute, Attribute<V> def) {
        return attributeContainer.getAttribute(attribute.getKey(), def);
    }

    @Nullable
    public <V> V getValue(GuiItemAttribute attribute) {
        return attributeContainer.getValue(attribute.getKey());
    }

    public <V> V getValue(GuiItemAttribute attribute, V def) {
        return attributeContainer.getValue(attribute.getKey(), def);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder extends ItemConfigurable.Builder {

        public Builder() {
            super(new ImmutableAttributeContainer(GUI_ITEM_ATTRIBUTE_MAP, new LinkedHashMap<>()));
        }

        public Builder(AttributeContainer attributeContainer) {
            super(attributeContainer);
        }

        public Builder(GuiConfigurable guiConfigurable) {
            super(guiConfigurable.getAttributeContainer());
        }

        public Builder ignore(GuiItemAttribute... ignore) {
            for (GuiItemAttribute ignoreAttribute : ignore)
                attributeContainerBuilder.ignore(ignoreAttribute.getKey());
            return this;
        }

        public Builder copy(GuiConfigurable guiConfigurable) {
            attributeContainerBuilder.copy(guiConfigurable.getAttributeContainer());
            return this;
        }

        public GuiConfigurable build() {
            return new GuiConfigurable(attributeContainerBuilder.build());
        }

    }

}
