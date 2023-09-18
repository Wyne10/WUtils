package me.wyne.wutils.config;

import me.wyne.wutils.config.configFieldTypes.ConfigFieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TypedConfigField {
    ConfigFieldType configFieldType();
}
