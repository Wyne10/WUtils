package me.wyne.wutils.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class Config {

    private static final Set<Object> registeredConfigObjects = new HashSet<>();

    public Config()
    {
        Config.registerConfigObject(this);
    }

    public static void registerConfigObject(Object object)
    {
        registeredConfigObjects.add(object);
    }

    public static void reloadConfigObjects(FileConfiguration config) throws IllegalAccessException {
        for (Object object : registeredConfigObjects)
        {
            for(Field field  : object.getClass().getDeclaredFields())
            {
                if (field.isAnnotationPresent(ConfigField.class))
                {
                    field.setAccessible(true);
                    String path = field.getAnnotation(ConfigField.class).path().isEmpty() ? field.getName() : field.getAnnotation(ConfigField.class).path();
                    if (field.isAnnotationPresent(TypedConfigField.class))
                        field.set(object, field.getAnnotation(TypedConfigField.class).configFieldType().getConfigParameter().getValue(config, path));
                    else if (ConfigParameter.class.isAssignableFrom(field.getType()))
                        ((ConfigParameter)field.get(object)).getValue(config, path);
                    else
                        field.set(object, config.get(path));
                }
            }
        }
    }
}
