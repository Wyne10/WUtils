package me.wyne.wutils.config;

import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurable.Configurable;
import me.wyne.wutils.log.Log;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.javatuples.Pair;

import java.lang.reflect.Field;
import java.util.*;

public class ConfigFieldParser {

    public static ConfigField getConfigField(Object holder, Field field)
    {
        field.setAccessible(true);
        var configEntry = field.getAnnotation(ConfigEntry.class);
        String path = configEntry.path().isEmpty() ? field.getName() : configEntry.path();
        String value = null;
        try {
            if (Configurable.class.isAssignableFrom(field.get(holder).getClass()))
                value = ((Configurable)field.get(holder)).toConfig(configEntry);
            else if (ConfigurationSerializable.class.isAssignableFrom(field.get(holder).getClass()))
                value = getConfigurationSerializableString(((ConfigurationSerializable) field.get(holder)));
            else
                value = configEntry.value().isEmpty() ? field.get(holder) != null ? field.get(holder).toString() : "" : configEntry.value();
        } catch (IllegalAccessException e) {
            Log.global.exception("An exception occurred while trying to parse reflected field to ConfigField", e);
        }
        String comment = configEntry.comment();

        return new ConfigField(holder, field, path, value, comment);
    }

    private static String getConfigurationSerializableString(ConfigurationSerializable configurationSerializable)
    {
        ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.append(1, "==", configurationSerializable.getClass().getTypeName());
        configurationSerializable.serialize().forEach((string, o) -> configBuilder.append(1, string, o));
        return configBuilder.build();
    }

    public static Pair<String, ConfigField> getSectionedConfigField(Object holder, Field field)
    {
        return new Pair<>(field.getAnnotation(ConfigEntry.class).section(), getConfigField(holder, field));
    }
    
    public static Set<ConfigSection> getConfigSections(Map<String, Set<ConfigField>> registeredConfigFields) {
        Map<String, ConfigSection> result = new LinkedHashMap<>();

        registeredConfigFields.forEach((section, fields) -> {
            String primarySection = getPrimarySection(section);
            String subSection = getSubSection(section);
            if(!result.containsKey(primarySection))
                result.put(primarySection, new ConfigSection(primarySection));
            fields.forEach(field -> result.get(primarySection).addField(subSection, field));
        });

        return new LinkedHashSet<>(result.values());
    }

    private static String getPrimarySection(String section)
    {
        String[] sectionPath = section.split("\\.");
        return sectionPath.length > 0 ? sectionPath[0] : "";
    }

    private static String getSubSection(String section)
    {
        String[] sectionPath = section.split("\\.");
        return sectionPath.length > 1 ? sectionPath[1] : "";
    }

}
