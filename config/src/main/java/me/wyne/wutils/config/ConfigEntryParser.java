package me.wyne.wutils.config;

import java.lang.reflect.Field;
import java.util.*;

public class ConfigEntryParser {

    private final Map<String, Set<ConfigField>> registeredConfigFields;
    private final Set<Object> registeredConfigObjects;
    
    public ConfigEntryParser(Map<String, Set<ConfigField>> registeredConfigFields, Set<Object> registeredConfigObjects)
    {
        this.registeredConfigFields = registeredConfigFields;
        this.registeredConfigObjects = registeredConfigObjects;
    }
    
    public Set<ConfigSection> getConfigSections() {
        Map<String, ConfigSection> result = new HashMap<>();
        
        for (Object object : registeredConfigObjects)
        {
            for (Field field : object.getClass().getDeclaredFields())
            {
                if (!field.isAnnotationPresent(ConfigEntry.class))
                    continue;
                field.setAccessible(true);
                var configEntry = field.getAnnotation(ConfigEntry.class);
                String section = getPrimarySection(configEntry.section());
                String subSection = getSubSection(configEntry.section());
                String path = configEntry.path().isEmpty() ? field.getName() : configEntry.path();
                String value = null;
                try {
                    value = configEntry.value().isEmpty() ? field.get(object).toString() : configEntry.value();
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e); // TODO Add logging
                }
                String comment = configEntry.comment();

                if(!result.containsKey(section))
                    result.put(section, new ConfigSection(section));

                result.get(section).addField(subSection, new ConfigField(path, value, comment));
            }
        }

        registeredConfigFields.forEach((section, fields) -> {
            if(!result.containsKey(section))
                result.put(section, new ConfigSection(section));
            fields.forEach(field -> result.get(section).addField(getSubSection(section), field));
        });

        return new LinkedHashSet<>(result.values());
    }

    private String getPrimarySection(String section)
    {
        String[] sectionPath = section.split("\\.");
        return sectionPath.length > 0 ? sectionPath[0] : "";
    }

    private String getSubSection(String section)
    {
        String[] sectionPath = section.split("\\.");
        return sectionPath.length > 1 ? sectionPath[1] : "";
    }

}
