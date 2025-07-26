package me.wyne.wutils.config.configurables;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import me.wyne.wutils.config.configurables.attribute.*;
import me.wyne.wutils.config.configurables.gui.ClickEventAttribute;
import me.wyne.wutils.config.configurables.gui.ContextClickEventAttribute;
import me.wyne.wutils.config.configurables.gui.GuiItemAttribute;
import me.wyne.wutils.config.configurables.gui.attribute.*;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import me.wyne.wutils.config.configurables.item.ItemAttributeContext;
import me.wyne.wutils.i18n.language.replacement.ComponentReplacement;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public GuiConfigurable ignore(ItemAttribute... ignore) {
        return new GuiConfigurable(getAttributeContainer().ignore(Arrays.stream(ignore).map(ItemAttribute::getKey).toArray(String[]::new)));
    }

    public GuiConfigurable ignore(GuiItemAttribute... ignore) {
        return new GuiConfigurable(getAttributeContainer().ignore(Arrays.stream(ignore).map(GuiItemAttribute::getKey).toArray(String[]::new)));
    }

    public GuiConfigurable ignore(String... ignore) {
        return new GuiConfigurable(getAttributeContainer().ignore(ignore));
    }

    public GuiConfigurable with(String key, AttributeFactory factory) {
        return new GuiConfigurable(getAttributeContainer().with(key, factory));
    }

    public GuiConfigurable with(Map<String, AttributeFactory> keyMap) {
        return new GuiConfigurable(getAttributeContainer().with(keyMap));
    }

    public GuiConfigurable with(Attribute<?> attribute) {
        return new GuiConfigurable(getAttributeContainer().with(attribute));
    }

    public GuiConfigurable with(AttributeContainer container) {
        return new GuiConfigurable(getAttributeContainer().with(container));
    }

    public GuiConfigurable with(ItemConfigurable itemConfigurable) {
        return new GuiConfigurable(getAttributeContainer().with(itemConfigurable.getAttributeContainer()));
    }

    public GuiConfigurable with(GuiConfigurable guiConfigurable) {
        return new GuiConfigurable(getAttributeContainer().with(guiConfigurable.getAttributeContainer()));
    }

    public GuiConfigurable copy(AttributeContainer container) {
        return new GuiConfigurable(getAttributeContainer().copy(container));
    }

    public GuiConfigurable copy(ItemConfigurable itemConfigurable) {
        return new GuiConfigurable(getAttributeContainer().copy(itemConfigurable.getAttributeContainer()));
    }

    public GuiConfigurable copy(GuiConfigurable guiConfigurable) {
        return new GuiConfigurable(getAttributeContainer().copy(guiConfigurable.getAttributeContainer()));
    }

    public GuiConfigurable copy() {
        return new GuiConfigurable(getAttributeContainer().copy());
    }

    @Nullable
    public <T> T get(GuiItemAttribute attribute) {
        return getAttributeContainer().get(attribute.getKey());
    }

    public <T> T get(GuiItemAttribute attribute, T def) {
        return getAttributeContainer().get(attribute.getKey(), def);
    }

    @Nullable
    public <V> Attribute<V> getAttribute(GuiItemAttribute attribute) {
        return getAttributeContainer().getAttribute(attribute.getKey());
    }

    public <V> Attribute<V> getAttribute(GuiItemAttribute attribute, Attribute<V> def) {
        return getAttributeContainer().getAttribute(attribute.getKey(), def);
    }

    @Nullable
    public <V> V getValue(GuiItemAttribute attribute) {
        return getAttributeContainer().getValue(attribute.getKey());
    }

    public <V> V getValue(GuiItemAttribute attribute, V def) {
        return getAttributeContainer().getValue(attribute.getKey(), def);
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

        public Builder ignore(ItemAttribute... ignore) {
            for (ItemAttribute ignoreAttribute : ignore)
                attributeContainerBuilder.ignore(ignoreAttribute.getKey());
            return this;
        }

        public Builder ignore(GuiItemAttribute... ignore) {
            for (GuiItemAttribute ignoreAttribute : ignore)
                attributeContainerBuilder.ignore(ignoreAttribute.getKey());
            return this;
        }

        public Builder ignore(String... ignore) {
            attributeContainerBuilder.ignore(ignore);
            return this;
        }

        public Builder with(String key, AttributeFactory factory) {
            attributeContainerBuilder.with(key, factory);
            return this;
        }

        public Builder with(Map<String, AttributeFactory> keyMap) {
            attributeContainerBuilder.with(keyMap);
            return this;
        }

        public Builder with(Attribute<?> attribute) {
            attributeContainerBuilder.with(attribute);
            return this;
        }

        public Builder with(AttributeContainer container) {
            attributeContainerBuilder.with(container);
            return this;
        }

        public Builder copy(AttributeContainer container) {
            attributeContainerBuilder.copy(container);
            return this;
        }

        public Builder copy(ItemConfigurable itemConfigurable) {
            attributeContainerBuilder.copy(itemConfigurable.getAttributeContainer());
            return this;
        }

        public Builder copy(GuiConfigurable guiConfigurable) {
            attributeContainerBuilder.copy(guiConfigurable.getAttributeContainer());
            return this;
        }

        public GuiConfigurable build() {
            return new GuiConfigurable(attributeContainerBuilder.buildImmutable());
        }

    }

}
