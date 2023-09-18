package me.wyne.wutils.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * Inherit to use {@link #getSetting(String)} and {@link #setSetting(String, Object)} directly for this object or use static method variants.
 * <br>Apply {@link Setting} to field to make it accessible as setting via {@link Settings}.
 */
public class Settings {

    /**
     * Get {@link Setting} value from inheritor by {@link Setting} field name or by {@link Setting} reference.
     * @param setting {@link Setting} field name or reference
     * @return {@link Setting} value
     * @param <SettingType> {@link Setting} value type
     */
    @Nullable
    public <SettingType> SettingType getSetting(@NotNull final String setting) {
        return Settings.getSetting(this, setting);
    }

    /**
     * Get {@link Setting} value from given settingsObject by {@link Setting} field name or by {@link Setting} reference.
     * @param settingsObject {@link Object} to get {@link Setting} from
     * @param setting {@link Setting} field name or reference
     * @return {@link Setting} value
     * @param <SettingType> {@link Setting} value type
     */
    @Nullable
    public static <SettingType> SettingType getSetting(@NotNull final Object settingsObject, @NotNull final String setting) {
        for (Field field : settingsObject.getClass().getDeclaredFields())
        {
            if (!field.isAnnotationPresent(Setting.class))
                continue;
            if (field.getAnnotation(Setting.class).reference().equalsIgnoreCase(setting) || field.getName().equalsIgnoreCase(setting))
            {
                field.setAccessible(true);
                try {
                    return (SettingType) field.get(settingsObject);
                } catch (Exception e) {
                    Log.error("Произошла ошибка при попытке получить настройку '" + setting + "'");
                    Log.error(e.getMessage());
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Set value to given {@link Setting} from inheritor by {@link Setting} field name or by {@link Setting} reference.
     * @param setting {@link Setting} field name of reference
     * @param newValue New {@link Setting} value
     * @return {@link Setting} setMessage
     */
    @NotNull
    public String setSetting(@NotNull final String setting, @NotNull final Object newValue) {
        return Settings.setSetting(this, setting, newValue);
    }

    /**
     * Set value to given {@link Setting} from settingsObject by {@link Setting} field name or by {@link Setting} reference.
     * @param settingsObject {@link Object} to set {@link Setting} value to
     * @param setting {@link Setting} field name of reference
     * @param newValue New {@link Setting} value
     * @return {@link Setting} setMessage
     */
    @NotNull
    public static String setSetting(@NotNull final Object settingsObject, @NotNull final String setting, @NotNull final Object newValue) {
        for (Field field : settingsObject.getClass().getDeclaredFields())
        {
            if (!field.isAnnotationPresent(Setting.class))
                continue;
            if (field.getAnnotation(Setting.class).reference().equalsIgnoreCase(setting) || field.getName().equalsIgnoreCase(setting))
            {
                try {
                    field.setAccessible(true);
                    field.set(settingsObject, newValue);
                } catch (Exception e) {
                    Log.error("Произошла ошибка при попытке установить настройку '" + setting + "'");
                    Log.error(e.getMessage());
                    return "";
                }
                return field.getAnnotation(Setting.class).setMessage();
            }
        }
        return "";
    }
}
