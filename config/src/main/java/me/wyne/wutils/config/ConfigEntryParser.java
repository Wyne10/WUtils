package me.wyne.wutils.config;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigEntryParser {

    private final Set<Object> registeredConfigObjects;
    
    public ConfigEntryParser(Set<Object> registeredConfigObjects)
    {
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
                String[] sectionPath = configEntry.section().split("\\.");
                String section = sectionPath.length > 0 ? sectionPath[0] : "";
                String subSection = sectionPath.length > 1 ? sectionPath[1] : "";
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

        return new LinkedHashSet<>(result.values());
    }

}
