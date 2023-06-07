package me.wyne.wutils.config;

import me.wyne.wutils.config.configFieldTypes.ConfigFieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to create pre-created {@link ConfigParameter}s for common value types.
 * <br>The return value of {@link ConfigFieldType}'s {@link ConfigParameter} getValue(FileConfiguration, String) method will be set to a field with this annotation present.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TypedConfigField {
    ConfigFieldType configFieldType();
}
