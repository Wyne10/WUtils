package me.wyne.wutils.config.configurables.attribute.common;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.LinkedHashSet;

public class StringCollectionAttribute extends ConfigurableAttribute<Collection<String>> {

    public StringCollectionAttribute(String key, Collection<String> value) {
        super(key, value);
    }

    public static final class ListFactory implements AttributeFactory<StringCollectionAttribute> {
        @Override
        public StringCollectionAttribute create(String key, ConfigurationSection config) {
            return new StringCollectionAttribute(key, ConfigUtils.getStringList(config, key));
        }
    }

    public static final class SetFactory implements AttributeFactory<StringCollectionAttribute> {
        @Override
        public StringCollectionAttribute create(String key, ConfigurationSection config) {
            return new StringCollectionAttribute(key, new LinkedHashSet<>(ConfigUtils.getStringList(config, key)));
        }
    }

}
