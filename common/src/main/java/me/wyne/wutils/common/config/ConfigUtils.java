package me.wyne.wutils.common.config;

import me.wyne.wutils.common.comparator.Comparators;
import me.wyne.wutils.common.comparator.DoubleComparator;
import me.wyne.wutils.common.comparator.IntComparator;
import me.wyne.wutils.common.duration.Durations;
import me.wyne.wutils.common.duration.TimeSpan;
import me.wyne.wutils.common.operation.DoubleOperation;
import me.wyne.wutils.common.operation.IntOperation;
import me.wyne.wutils.common.operation.Operations;
import me.wyne.wutils.common.plugin.PluginUtils;
import me.wyne.wutils.common.range.LocationRange;
import me.wyne.wutils.common.range.TimeSpanRange;
import me.wyne.wutils.common.range.VectorRange;
import me.wyne.wutils.common.vector.VectorUtils;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Static helpers for reading typed, defaultable values out of Bukkit {@link ConfigurationSection}s,
 * layering parsing and fallback-default support on top of the raw Bukkit accessors.
 */
public final class ConfigUtils {

    /**
     * Returns the nested section at {@code path}, or {@code section} itself if {@code path}
     * does not point to a configuration section.
     */
    public static @NotNull ConfigurationSection getConfigurationSection(@NotNull ConfigurationSection section, @NotNull String path) {
        if (section.isConfigurationSection(path))
            return section.getConfigurationSection(path);
        return section;
    }

    /**
     * Builds a dotted config path by prefixing {@code path} with the current path of {@code section},
     * for use in log messages. Returns {@code path} unchanged when {@code section} is null or has no
     * current path.
     */
    public static @NotNull String getPath(@Nullable ConfigurationSection section, @NotNull String path) {
        if (section == null)
            return path;
        if (section.getCurrentPath() == null)
            return path;
        if (section.getCurrentPath().isBlank())
            return path;
        return section.getCurrentPath() + "." + path;
    }

    /**
     * Reads a string list at {@code path}, treating a single scalar string value as a
     * one-element list instead of failing.
     */
    @SuppressWarnings("DataFlowIssue")
    public static @NotNull List<@NotNull String> getStringList(@NotNull ConfigurationSection config, @NotNull String path) {
        if (!config.isList(path))
            return config.getString(path, "").isBlank() ? Collections.emptyList() : List.of(config.getString(path));
        return config.getStringList(path);
    }

    /**
     * Reads the string at {@code path}, joining a YAML list value into a single
     * newline-separated string via {@link #reduceString(Collection)} instead of failing.
     */
    public static @NotNull String getString(@NotNull ConfigurationSection config, @NotNull String path, @NotNull String def) {
        if (config.isList(path))
            return reduceString(config.getStringList(path));
        return config.getString(path, def);
    }

    /** Joins the given strings with newlines. */
    public static @NotNull String reduceString(@NotNull Collection<@NotNull String> stringList) {
        return stringList.stream().reduce(ConfigUtils::reduceString).orElse("");
    }

    public static @NotNull String reduceString(@NotNull String s1, @NotNull String s2) {
        return s1 + "\n" + s2;
    }

    /**
     * Parses a comma-separated vector (e.g. {@code "1,2,3"}) at {@code path}. Any blank
     * component falls back to the matching component of {@code def} rather than the whole
     * default being substituted.
     */
    public static @NotNull Vector getVector(@NotNull ConfigurationSection config, @NotNull String path, @NotNull Vector def) {
        return VectorUtils.getVector(config.getString(path, ""), def);
    }

    /** Same as {@link #getVector}, defaulting to the zero vector. */
    public static @NotNull Vector getVectorOrZero(@NotNull ConfigurationSection config, @NotNull String path) {
        return getVector(config, path, VectorUtils.zero());
    }

    /** Parses a comparator expression (e.g. {@code "<5"}, {@code ">=3"}) at {@code path}. */
    public static @NotNull IntComparator getIntComparator(@NotNull ConfigurationSection config, @NotNull String path) {
        return Comparators.getIntComparator(config.getString(path));
    }

    /** Parses a comparator expression (e.g. {@code "<5.5"}, {@code ">=3.0"}) at {@code path}. */
    public static @NotNull DoubleComparator getDoubleComparator(@NotNull ConfigurationSection config, @NotNull String path) {
        return Comparators.getDoubleComparator(config.getString(path));
    }

    /** Parses an operation expression (e.g. {@code "+5"}, {@code "*2"}) at {@code path}. */
    public static @NotNull IntOperation getIntOperation(@NotNull ConfigurationSection config, @NotNull String path) {
        return Operations.getIntOperation(config.getString(path));
    }

    /** Parses an operation expression (e.g. {@code "+5.5"}, {@code "*2.0"}) at {@code path}. */
    public static @NotNull DoubleOperation getDoubleOperation(@NotNull ConfigurationSection config, @NotNull String path) {
        return Operations.getDoubleOperation(config.getString(path));
    }

    /** Parses a duration expression (e.g. {@code "5s"}, {@code "200ms"}, {@code "10t"}) at {@code path}. */
    public static @NotNull TimeSpan getTimeSpan(@NotNull ConfigurationSection config, @NotNull String path) {
        return Durations.getTimeSpan(config.getString(path));
    }

    /** Same as {@link #getTimeSpan(ConfigurationSection, String)}, returning {@code def} when the value is blank. */
    public static @NotNull TimeSpan getTimeSpan(@NotNull ConfigurationSection config, @NotNull String path, @NotNull TimeSpan def) {
        if (config.getString(path, "").isBlank())
            return def;
        return Durations.getTimeSpan(config.getString(path));
    }

    /** Parses a duration expression at {@code path} and returns its length in milliseconds. */
    public static long getMillis(@NotNull ConfigurationSection config, @NotNull String path) {
        return Durations.getMillis(config.getString(path));
    }

    /** Parses a duration expression at {@code path} and returns its length in ticks. */
    public static long getTicks(@NotNull ConfigurationSection config, @NotNull String path) {
        return Durations.getTicks(config.getString(path));
    }

    /** Same as {@link #getMillis(ConfigurationSection, String)}, returning {@code def} when the value is blank. */
    public static long getMillis(@NotNull ConfigurationSection config, @NotNull String path, long def) {
        if (config.getString(path, "").isBlank())
            return def;
        return Durations.getMillis(config.getString(path));
    }

    /** Same as {@link #getTicks(ConfigurationSection, String)}, returning {@code def} when the value is blank. */
    public static long getTicks(@NotNull ConfigurationSection config, @NotNull String path, long def) {
        if (config.getString(path, "").isBlank())
            return def;
        return Durations.getTicks(config.getString(path));
    }

    /** Parses a duration range (e.g. {@code "5s..10s"}) at {@code path}. */
    public static @NotNull TimeSpanRange getTimeSpanRange(@NotNull ConfigurationSection config, @NotNull String path) {
        return Durations.getTimeSpanRange(config.getString(path));
    }

    /** Parses a vector range at {@code path}. */
    public static @NotNull VectorRange getVectorRange(@NotNull ConfigurationSection config, @NotNull String path) {
        return VectorRange.getVectorRange(config.getString(path));
    }

    /** Parses a world-bound location range at {@code path}. */
    public static @NotNull LocationRange getLocationRange(@NotNull ConfigurationSection config, @NotNull String path) {
        return LocationRange.getLocationRange(config.getString(path));
    }

    /**
     * Reads a set of enum constants at {@code key}. A boolean value expands to {@link EnumSet#allOf}
     * (true) or {@link EnumSet#noneOf} (false); a string/list value is matched by enum name, and
     * unmatched entries are skipped with a warning rather than failing the read.
     */
    public static <E extends Enum<E>> @NotNull EnumSet<E> getEnumSet(@NotNull ConfigurationSection section, @NotNull String key, @NotNull Class<E> enumClass) {
        if (section.isBoolean(key)) {
            return section.getBoolean(key)
                    ? EnumSet.allOf(enumClass)
                    : EnumSet.noneOf(enumClass);
        }

        List<String> values = getStringList(section, key);
        EnumSet<E> result = EnumSet.noneOf(enumClass);

        for (String value : values) {
            try {
                result.add(Enum.valueOf(enumClass, value.toUpperCase(Locale.ENGLISH)));
            } catch (IllegalArgumentException e) {
                PluginUtils.getLogger().warn("Skipping illegal enum '{}' at '{}'", value, getPath(section, key));
            }
        }

        return result;
    }

    /**
     * Same as {@link #getEnumSet}, but for enums implementing {@link Keyed}: entries that don't
     * match an enum name are retried as a {@link NamespacedKey} against each constant's key. Falls
     * back to {@link #getEnumSet} entirely when {@code enumClass} does not implement {@link Keyed}.
     */
    public static <E extends Enum<E>> @NotNull EnumSet<E> getKeyedEnumSet(@NotNull ConfigurationSection section, @NotNull String key, @NotNull Class<E> enumClass) {
        if (!Keyed.class.isAssignableFrom(enumClass))
            return getEnumSet(section, key, enumClass);
        if (section.isBoolean(key)) {
            return section.getBoolean(key)
                    ? EnumSet.allOf(enumClass)
                    : EnumSet.noneOf(enumClass);
        }

        Map<String, E> keyMap = new HashMap<>();
        for (E e : enumClass.getEnumConstants()) {
            keyMap.put(((Keyed) e).getKey().toString(), e);
        }
        List<String> values = getStringList(section, key);
        EnumSet<E> result = EnumSet.noneOf(enumClass);

        for (String value : values) {
            try {
                result.add(Enum.valueOf(enumClass, value.toUpperCase(Locale.ENGLISH)));
                continue;
            } catch (IllegalArgumentException ignored) {}

            NamespacedKey valueKey = NamespacedKey.fromString(value);
            if (valueKey == null) {
                PluginUtils.getLogger().warn("Skipping illegal enum/key '{}' at '{}'", value, getPath(section, key));
                continue;
            }
            E enumValue = keyMap.get(valueKey.toString());
            if (enumValue == null) {
                PluginUtils.getLogger().warn("Skipping illegal enum/key '{}' at '{}'", value, getPath(section, key));
                continue;
            }
            result.add(enumValue);
        }

        return result;
    }

    @Nullable
    public static <E extends Enum<E>> E getByName(@Nullable String name, Class<E> enumClass) {
        if (name == null) return null;

        try {
            return Enum.valueOf(enumClass, name.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Nullable
    public static <E extends Enum<E>> E getByKeyOrName(@Nullable String key, Class<E> enumClass) {
        if (key == null) return null;

        try {
            return Enum.valueOf(enumClass, key.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException ignored) {}

        if (!Keyed.class.isAssignableFrom(enumClass))
            return null;

        Map<String, E> keyMap = new HashMap<>();
        for (E e : enumClass.getEnumConstants()) {
            keyMap.put(((Keyed) e).getKey().toString(), e);
        }

        NamespacedKey valueKey = NamespacedKey.fromString(key);
        if (valueKey == null) return null;

        return keyMap.get(valueKey.toString());
    }

    @Nullable
    public static <E extends Enum<E>> E getByKeyOrName(ConfigurationSection section, String path, Class<E> enumClass) {
        String value = section.getString(path);
        if (value == null) return null;
        return getByKeyOrName(value, enumClass);
    }

    /** Reads a string list at {@code path} and matches each entry to a {@link Material}, silently dropping unmatched entries. */
    public static @NotNull Set<@NotNull Material> getMaterialEnumSet(@NotNull ConfigurationSection section, @NotNull String path) {
        return section.getStringList(path).stream()
                .map(Material::matchMaterial)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    /** Same as {@link #saveDirectoryResource(Plugin, File, boolean)}, using {@link PluginUtils#getPlugin()} and not overwriting an existing directory. */
    public static void saveDirectoryResource(@NotNull File directory) {
        saveDirectoryResource(PluginUtils.getPlugin(), directory, false);
    }

    /** Same as {@link #saveDirectoryResource(Plugin, File, boolean)}, using {@link PluginUtils#getPlugin()}. */
    public static void saveDirectoryResource(@NotNull File directory, boolean replace) {
        saveDirectoryResource(PluginUtils.getPlugin(), directory, replace);
    }

    /** Same as {@link #saveDirectoryResource(Plugin, File, boolean)}, not overwriting an existing directory. */
    public static void saveDirectoryResource(@NotNull Plugin plugin, @NotNull File directory) {
        saveDirectoryResource(plugin, directory, false);
    }

    /**
     * Extracts a directory bundled inside the plugin jar into the plugin's data folder, preserving
     * its path relative to the jar root. Does nothing if {@code directory} already exists and
     * {@code force} is false, or if {@code directory} is not located inside the plugin's data
     * folder. Failures while reading or writing the jar are logged and swallowed rather than thrown.
     */
    public static void saveDirectoryResource(@NotNull Plugin plugin, @NotNull File directory, boolean force) {
        if (!force && directory.exists()) {
            return;
        }

        String resourcePath = getRelativePath(directory, plugin.getDataFolder());
        if (resourcePath == null) {
            return;
        }

        try {
            File jarFile = new File(plugin.getClass().getProtectionDomain()
                    .getCodeSource().getLocation().toURI());

            try (JarFile jar = new JarFile(jarFile)) {
                String prefix = resourcePath + "/";
                Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (!entry.isDirectory() && entry.getName().startsWith(prefix)) {
                        File target = new File(plugin.getDataFolder(), entry.getName());
                        if (target.exists()) {
                            continue;
                        }

                        if (target.getParentFile() != null) {
                            target.getParentFile().mkdirs();
                        }

                        try (InputStream input = jar.getInputStream(entry);
                             OutputStream output = new FileOutputStream(target)) {
                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            while ((bytesRead = input.read(buffer)) != -1) {
                                output.write(buffer, 0, bytesRead);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            PluginUtils.getLogger().error("Failed to save resource {}", resourcePath, e);
        }
    }

    /**
     * Returns {@code file}'s path relative to {@code base}, with {@code /} separators regardless
     * of platform. Returns {@code null} if either path cannot be resolved to a canonical form.
     */
    public static @Nullable String getRelativePath(@NotNull File file, @NotNull File base) {
        try {
            Path filePath = file.getCanonicalFile().toPath();
            Path basePath = base.getCanonicalFile().toPath();
            return basePath.relativize(filePath).toString()
                    .replace(File.separator, "/");
        } catch (IOException e) {
            return null;
        }
    }

}
