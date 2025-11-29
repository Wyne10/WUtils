package me.wyne.wutils.config.configurables.attribute;

import me.wyne.wutils.config.ConfigEntry;
import org.bukkit.configuration.ConfigurationSection;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CompositeAttribute<V extends Attribute<?>> extends ConfigurableAttribute<Set<V>> {

    public CompositeAttribute(String key, Set<V> attributes) {
        super(key, attributes);
    }

    @SuppressWarnings({"DataFlowIssue"})
    public CompositeAttribute(String key, ConfigurationSection config, AttributeFactory<V> factory) {
        super(key, new LinkedHashSet<>());
        if (!config.contains(key)) return;
        config.getConfigurationSection(key).getKeys(false).forEach(
                itemKey -> getValue().add((V) factory.create(itemKey, config.getConfigurationSection(key)))
        );
    }

    private <T> Set<T> getSet(Class<T> clazz) {
        return getValue().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        StringBuilder builder = new StringBuilder();
        builder.append(" ".repeat(depth * 2)).append(getKey()).append(":").append("\n");
        getSet(ConfigurableAttribute.class).forEach(attribute -> builder.append(attribute.toConfig(depth + 1, configEntry)));
        return builder.toString();
    }

}
