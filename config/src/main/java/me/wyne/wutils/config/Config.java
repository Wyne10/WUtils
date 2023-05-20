package me.wyne.wutils.config;

import me.wyne.wutils.log.Log;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Inherit to automatically register config {@link Object} or use static {@link #registerConfigObject(Object)} method.
 * <br>Apply {@link ConfigField} to field to tie it up with value from config.
 */
public class Config {

    private static final Set<Object> registeredConfigObjects = new HashSet<>();

    public Config()
    {
        Config.registerConfigObject(this);
    }

    /**
     * Registered objects will be reloaded on {@link #reloadConfigObjects(FileConfiguration)}.
     * @param object {@link Object} to register
     */
    public static void registerConfigObject(@NotNull final Object object)
    {
        registeredConfigObjects.add(object);
    }

    /**
     * Load data from {@link FileConfiguration} to registered objects.
     * @return True if reloaded successfully
     * @param config {@link FileConfiguration} to load data from
     */
    public static boolean reloadConfigObjects(@NotNull final FileConfiguration config) {
        try
        {
            Log.info("Перезагрузка конфига...");
            for (Object object : registeredConfigObjects)
            {
                for(Field field  : object.getClass().getDeclaredFields())
                {
                    if (field.isAnnotationPresent(ConfigField.class))
                    {
                        field.setAccessible(true);
                        if (field.getType().isAssignableFrom(ConfigParameter.class))
                            ((ConfigParameter)field.get(object)).getValue(config, field.getAnnotation(ConfigField.class).path());
                        else
                            field.set(object, config.get(field.getAnnotation(ConfigField.class).path()));
                    }
                }
            }
            Log.info("Конфиг перезагружен");
            return true;
        }
        catch (Exception e)
        {
            Log.error("Произошла ошибка при перезагрузке конфига");
            Log.error(e.getMessage());
            return false;
        }
    }

}
