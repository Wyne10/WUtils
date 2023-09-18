package me.wyne.wutils.settings;

import me.wyne.wutils.log.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class Settings {

    @Nullable
    public <SettingType> SettingType getSetting(String setting) {
        return Settings.getSetting(this, setting);
    }

    @Nullable
    public static <SettingType> SettingType getSetting(Object settingsObject, String setting) {
        for (Field field : settingsObject.getClass().getDeclaredFields())
        {
            if (!field.isAnnotationPresent(Setting.class))
                continue;
            if (field.getAnnotation(Setting.class).reference().equalsIgnoreCase(setting) || field.getName().equalsIgnoreCase(setting))
            {
                field.setAccessible(true);
                try {
                    return (SettingType) field.get(settingsObject);
                } catch (IllegalAccessException e) {
                    Log.global.exception("An exception occurred while trying to get setting '" + setting + "'", e);
                    return null;
                }
            }
        }
        return null;
    }

    public String setSetting(String setting, Object newValue) {
        return Settings.setSetting(this, setting, newValue);
    }

    public static String setSetting(Object settingsObject, String setting, Object newValue) {
        for (Field field : settingsObject.getClass().getDeclaredFields())
        {
            if (!field.isAnnotationPresent(Setting.class))
                continue;
            if (field.getAnnotation(Setting.class).reference().equalsIgnoreCase(setting) || field.getName().equalsIgnoreCase(setting))
            {
                try {
                    field.setAccessible(true);
                    field.set(settingsObject, newValue);
                } catch (IllegalAccessException e) {
                    Log.global.exception("An exception occurred while trying to set setting '" + setting + "'", e);
                    return "";
                }
                return field.getAnnotation(Setting.class).setMessage();
            }
        }
        return "";
    }
}
