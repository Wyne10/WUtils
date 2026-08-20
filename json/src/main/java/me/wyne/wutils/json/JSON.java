package me.wyne.wutils.json;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as the root of its own JSON file, managed by a {@link JsonRegistry}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JSON {

    /**
     * Path of the backing JSON file, relative to the owning {@link JsonRegistry}'s directory.
     */
    @NotNull String path();

}
