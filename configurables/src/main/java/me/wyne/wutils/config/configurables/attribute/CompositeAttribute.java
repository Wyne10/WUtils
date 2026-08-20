package me.wyne.wutils.config.configurables.attribute;

import me.wyne.wutils.config.ConfigEntry;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@link ConfigurableAttribute} whose value is a nested collection of attributes of a single kind,
 * one YAML key holding many children — {@code EnchantmentsAttribute}, {@code PotionEffectsAttribute}
 * and similar are one-line subclasses of this. The config constructor reads the section at
 * {@code key} and builds one child attribute per child key, using the child's own name as that
 * child's attribute key; the child names are otherwise arbitrary labels that only exist so YAML can
 * hold a list of sections; nothing reads them back.
 *
 * <p>The config constructor requires the value at {@code key} to actually be a section. If it is
 * present but is a string or a list (e.g. {@code enchantments: 'minecraft:sharpness 5'} instead of a
 * section), {@link ConfigurationSection#getConfigurationSection} returns {@code null} and the
 * subsequent {@code getKeys(false)} call throws {@link NullPointerException}.</p>
 *
 * @param <V> the element attribute type
 */
public class CompositeAttribute<V extends Attribute<?>> extends ConfigurableAttribute<Set<V>> {

    public CompositeAttribute(@NotNull String key, @NotNull Set<@NotNull V> attributes) {
        super(key, attributes);
    }

    @SuppressWarnings({"DataFlowIssue"})
    public CompositeAttribute(@NotNull String key, @NotNull ConfigurationSection config, @NotNull AttributeFactory<V> factory) {
        super(key, new LinkedHashSet<>());
        if (!config.contains(key)) return;
        config.getConfigurationSection(key).getKeys(false).forEach(
                itemKey -> getValue().add((V) factory.create(itemKey, config.getConfigurationSection(key)))
        );
    }

    private <T> @NotNull Set<@NotNull T> getSet(@NotNull Class<T> clazz) {
        return getValue().stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        StringBuilder builder = new StringBuilder();
        builder.append(" ".repeat(depth * 2)).append(getKey()).append(":").append("\n");
        getSet(ConfigurableAttribute.class).forEach(attribute -> builder.append(attribute.toConfig(depth + 1, configEntry)));
        return builder.toString();
    }

}
