package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurables.attribute.Attribute;
import me.wyne.wutils.config.configurables.attribute.AttributeContainer;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.AttributeMap;
import me.wyne.wutils.config.configurables.item.ItemAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class AttributeConfigurable implements CompositeConfigurable {

    private final AttributeContainer attributeContainer;

    public AttributeConfigurable(AttributeContainer attributeContainer) {
        this.attributeContainer = attributeContainer;
    }

    public AttributeConfigurable(AttributeContainer attributeContainer, ConfigurationSection section) {
        this(attributeContainer);
        fromConfig(section);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return attributeContainer.toConfig(depth, configEntry);
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        attributeContainer.fromConfig(configObject);
    }

    public ItemConfigurable ignore(ItemAttribute... ignore) {
        return new ItemConfigurable(attributeContainer.ignore(Arrays.stream(ignore).map(ItemAttribute::getKey).toArray(String[]::new)));
    }

    public ItemConfigurable ignore(String... ignore) {
        return new ItemConfigurable(attributeContainer.ignore(ignore));
    }

    public ItemConfigurable with(String key, AttributeFactory factory) {
        return new ItemConfigurable(attributeContainer.with(key, factory));
    }

    public ItemConfigurable with(Map<String, AttributeFactory> keyMap) {
        return new ItemConfigurable(attributeContainer.with(keyMap));
    }

    public ItemConfigurable with(Attribute<?> attribute) {
        return new ItemConfigurable(attributeContainer.with(attribute));
    }

    public ItemConfigurable with(AttributeContainer container) {
        return new ItemConfigurable(attributeContainer.with(container));
    }

    public ItemConfigurable with(ItemConfigurable itemConfigurable) {
        return new ItemConfigurable(attributeContainer.with(itemConfigurable.getAttributeContainer()));
    }

    public ItemConfigurable copy(AttributeContainer container) {
        return new ItemConfigurable(attributeContainer.copy(container));
    }

    public ItemConfigurable copy(ItemConfigurable itemConfigurable) {
        return new ItemConfigurable(attributeContainer.copy(itemConfigurable.getAttributeContainer()));
    }

    public ItemConfigurable copy() {
        return new ItemConfigurable(attributeContainer.copy());
    }

    @Nullable
    public <T> T get(Class<T> clazz) {
        return attributeContainer.get(clazz);
    }

    @Nullable
    public <T> T get(String key) {
        return attributeContainer.get(key);
    }

    @Nullable
    public <T> T get(Class<T> clazz, T def) {
        return attributeContainer.get(clazz, def);
    }

    public <T> T get(String key, T def) {
        return attributeContainer.get(key, def);
    }

    public <T> Set<T> getSet(Class<T> clazz) {
        return attributeContainer.getSet(clazz);
    }

    @Nullable
    public <T, V> Attribute<V> getAttribute(Class<T> clazz) {
        return attributeContainer.getAttribute(clazz);
    }

    @Nullable
    public <V> Attribute<V> getAttribute(String key) {
        return attributeContainer.getAttribute(key);
    }

    @Nullable
    public <T, V> Attribute<V> getAttribute(Class<T> clazz, Attribute<V> def) {
        return attributeContainer.getAttribute(clazz, def);
    }

    public <V> Attribute<V> getAttribute(String key, Attribute<V> def) {
        return attributeContainer.getAttribute(key, def);
    }

    public <V> Set<Attribute<V>> getAttributes(Class<Attribute<V>> clazz) {
        return attributeContainer.getAttributes(clazz);
    }

    @Nullable
    public <T, V> V getValue(Class<T> clazz) {
        return attributeContainer.getValue(clazz);
    }

    @Nullable
    public <V> V getValue(String key) {
        return attributeContainer.getValue(key);
    }

    @Nullable
    public <T, V> V getValue(Class<T> clazz, V def) {
        return attributeContainer.getValue(clazz, def);
    }

    public <V> V getValue(String key, V def) {
        return attributeContainer.getValue(key, def);
    }

    public <V> Set<V> getValues(Class<V> clazz) {
        return attributeContainer.getValues(clazz);
    }

    public Map<String, Attribute<?>> getAttributes() {
        return attributeContainer.getAttributes();
    }

    public AttributeMap getAttributeMap() {
        return attributeContainer.getAttributeMap();
    }

    public AttributeContainer getAttributeContainer() {
        return attributeContainer;
    }

}
