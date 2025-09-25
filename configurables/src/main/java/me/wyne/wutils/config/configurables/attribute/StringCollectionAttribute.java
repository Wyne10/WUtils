package me.wyne.wutils.config.configurables.attribute;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.LinkedHashSet;

public class StringCollectionAttribute extends ConfigurableAttribute<Collection<String>> {

    public StringCollectionAttribute(String key, Collection<String> value) {
        super(key, value);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().appendCollection(depth, getKey(), getValue()).buildNoSpace();
    }

    public static final class ListFactory implements AttributeFactory {
        @Override
        public StringCollectionAttribute create(String key, ConfigurationSection config) {
            return new StringCollectionAttribute(key, config.getStringList(key));
        }
    }

    public static final class SetFactory implements AttributeFactory {
        @Override
        public StringCollectionAttribute create(String key, ConfigurationSection config) {
            return new StringCollectionAttribute(key, new LinkedHashSet<>(config.getStringList(key)));
        }
    }

}
