package me.wyne.wutils.config;

import me.wyne.wutils.log.Log;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

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

    public static boolean reloadConfigObjects(FileConfiguration config) {
        try
        {
            Log.info("Reloading config...");
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
            Log.info("Config is reloaded");
            return true;
        }
        catch (Exception e)
        {
            Log.error("Critical exception occurred at config reload");
            Log.error(e.getMessage());
            Log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

}
