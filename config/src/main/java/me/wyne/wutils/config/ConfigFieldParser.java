package me.wyne.wutils.config;

import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurable.ConfigSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Resolves {@link ConfigEntry}-annotated fields, via reflection, into {@link ConfigField}s and
 * {@link ConfigSection}s ready for generation.
 */
public class ConfigFieldParser {

    /**
     * Resolves a single annotated field into a {@link ConfigField}, reading its current value via
     * reflection.
     *
     * <p>The field's lookup path is built from only the primary segment of {@link ConfigEntry#section()}
     * (before the first {@code .}, lower-cased and stripped of spaces) followed by {@code "." + path} —
     * any {@code sub} segment of {@code section()} is ignored here and only affects generation grouping
     * (see {@link #getConfigSections}).</p>
     *
     * <p>If reading the field throws {@link IllegalAccessException}, the exception is logged and the
     * resulting {@link ConfigField#value()} is left {@code null}.</p>
     */
    public static @NotNull ConfigField getConfigField(@NotNull Object holder, @NotNull Field field, @NotNull Logger logger) {
        field.setAccessible(true);
        var configEntry = field.getAnnotation(ConfigEntry.class);
        String path = configEntry.path().isEmpty() ? field.getName() : configEntry.path();
        String value = null;
        try {
            if (field.get(holder) != null && ConfigSerializable.class.isAssignableFrom(field.get(holder).getClass()))
                value = ((ConfigSerializable)field.get(holder)).toConfig(configEntry);
            else if (field.get(holder) != null && ConfigurationSerializable.class.isAssignableFrom(field.get(holder).getClass()))
                value = getConfigurationSerializableString(((ConfigurationSerializable) field.get(holder)));
            else
                value = field.get(holder) != null ? field.get(holder).toString() : "";
        } catch (IllegalAccessException e) {
            logger.error("An exception occurred trying to parse reflected field to ConfigField", e);
        }
        String comment = configEntry.comment();

        return new ConfigField(holder, field,
                configEntry.section().substring(0, configEntry.section().contains(".") ? configEntry.section().indexOf('.') : configEntry.section().length())
                        .replaceAll(" ", "").toLowerCase() + "." + path,
                value, comment, configEntry.load());
    }

    private static @NotNull String getConfigurationSerializableString(@NotNull ConfigurationSerializable configurationSerializable) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.append("==", configurationSerializable.getClass().getTypeName());
        configurationSerializable.serialize().forEach(configBuilder::append);
        return configBuilder.build();
    }

    /**
     * Resolves a field the same way as {@link #getConfigField}, pairing the result with the field's
     * raw, un-split {@link ConfigEntry#section()} value.
     */
    public static @NotNull Pair<@NotNull String, @NotNull ConfigField> getSectionedConfigField(@NotNull Object holder, @NotNull Field field, @NotNull Logger logger) {
        return new Pair<>(field.getAnnotation(ConfigEntry.class).section(), getConfigField(holder, field, logger));
    }

    /**
     * Groups registered fields by their primary section, further grouping each section's fields by
     * sub-section, ready to be rendered by {@link ConfigGenerator}.
     *
     * @param registeredConfigFields fields keyed by their raw, un-split {@link ConfigEntry#section()} value
     */
    public static @NotNull Set<@NotNull ConfigSection> getConfigSections(@NotNull Map<@NotNull String, @NotNull Set<@NotNull ConfigField>> registeredConfigFields) {
        Map<String, ConfigSection> result = new LinkedHashMap<>();

        registeredConfigFields.forEach((section, fields) -> {
            String primarySection = getPrimarySection(section);
            String subSection = getSubSection(section);
            if(!result.containsKey(primarySection))
                result.put(primarySection, new ConfigSection(primarySection));
            fields.forEach(field -> result.get(primarySection).addField(subSection, field));
        });

        return new LinkedHashSet<>(result.values());
    }

    private static @NotNull String getPrimarySection(@NotNull String section) {
        String[] sectionPath = section.split("\\.");
        return sectionPath.length > 0 ? sectionPath[0] : "";
    }

    private static @NotNull String getSubSection(@NotNull String section) {
        String[] sectionPath = section.split("\\.");
        return sectionPath.length > 1 ? sectionPath[1] : "";
    }

}
