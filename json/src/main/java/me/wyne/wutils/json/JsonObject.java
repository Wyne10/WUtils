package me.wyne.wutils.json;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * A single {@link JsonRegistry} registration: the object declaring the field, the field
 * itself, and the type Gson uses to (de)serialize its value.
 *
 * @param holder object instance that owns {@code field}
 * @param field  the field being (de)serialized
 * @param type   type used for Gson (de)serialization of {@code field}'s value; usually
 *               {@code field}'s own generic type, but may be overridden at registration
 */
public record JsonObject(@NotNull Object holder, @NotNull Field field, @NotNull Type type) {}
