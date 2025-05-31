package me.wyne.wutils.config.configurables.item;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.Attribute;
import me.wyne.wutils.config.configurables.attribute.AttributeBase;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CompositeAttribute<V extends Attribute<?>> extends AttributeBase<Map<String, V>> implements ConfigurableAttribute<Map<String, V>> {

    public CompositeAttribute(String key, Map<String, V> attributes) {
        super(key, attributes);
    }

    public CompositeAttribute(String key, ConfigurationSection config, AttributeFactory factory) {
        super(key, new LinkedHashMap<>());
        config.getConfigurationSection(key).getKeys(false).forEach(
                itemKey -> getValue().put(itemKey, (V) factory.create(itemKey, config.getConfigurationSection(key)))
        );
    }

    protected <T> Set<T> getSet(Class<T> clazz) {
        return getValue().values().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        ConfigBuilder builder = new ConfigBuilder();
        getSet(ConfigurableAttribute.class).forEach(attribute -> builder.appendComposite(depth, attribute.getKey(), attribute, configEntry));
        return builder.build();
    }

}
