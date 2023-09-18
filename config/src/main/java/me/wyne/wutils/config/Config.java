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

    public static void registerConfigObject(@NotNull final Object object)
    {
        registeredConfigObjects.add(object);
    }

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
                        if (field.isAnnotationPresent(TypedConfigField.class))
                            field.set(object, field.getAnnotation(TypedConfigField.class).configFieldType().getConfigParameter().getValue(config, field.getAnnotation(ConfigField.class).path()));
                        else if (ConfigParameter.class.isAssignableFrom(field.getType()))
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
            Log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

}
