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
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public final class ConfigUtils {

    public static ConfigurationSection getConfigurationSection(ConfigurationSection section, String path) {
        if (section.isConfigurationSection(path))
            return section.getConfigurationSection(path);
        return section;
    }

    public static String getPath(@Nullable ConfigurationSection section, String path) {
        if (section == null)
            return path;
        if (section.getCurrentPath() == null)
            return path;
        if (section.getCurrentPath().isBlank())
            return path;
        return section.getCurrentPath() + "." + path;
    }

    @SuppressWarnings("DataFlowIssue")
    public static List<String> getStringList(ConfigurationSection config, String path) {
        if (!config.isList(path))
            return config.getString(path, "").isBlank() ? Collections.emptyList() : List.of(config.getString(path));
        return config.getStringList(path);
    }

    public static String getString(ConfigurationSection config, String path, String def) {
        if (config.isList(path))
            return reduceString(config.getStringList(path));
        return config.getString(path, def);
    }

    public static String reduceString(Collection<String> stringList) {
        return stringList.stream().reduce(ConfigUtils::reduceString).orElse("");
    }

    public static String reduceString(String s1, String s2) {
        return s1 + "\n" + s2;
    }

    public static Vector getVector(ConfigurationSection config, String path, Vector def) {
        return VectorUtils.getVector(config.getString(path, ""), def);
    }

    public static Vector getVectorOrZero(ConfigurationSection config, String path) {
        return getVector(config, path, VectorUtils.zero());
    }

    public static IntComparator getIntComparator(ConfigurationSection config, String path) {
        return Comparators.getIntComparator(config.getString(path));
    }

    public static DoubleComparator getDoubleComparator(ConfigurationSection config, String path) {
        return Comparators.getDoubleComparator(config.getString(path));
    }

    public static IntOperation getIntOperation(ConfigurationSection config, String path) {
        return Operations.getIntOperation(config.getString(path));
    }

    public static DoubleOperation getDoubleOperation(ConfigurationSection config, String path) {
        return Operations.getDoubleOperation(config.getString(path));
    }

    public static TimeSpan getTimeSpan(ConfigurationSection config, String path) {
        return Durations.getTimeSpan(config.getString(path));
    }

    public static TimeSpan getTimeSpan(ConfigurationSection config, String path, TimeSpan def) {
        if (config.getString(path, "").isBlank())
            return def;
        return Durations.getTimeSpan(config.getString(path));
    }

    public static long getMillis(ConfigurationSection config, String path) {
        return Durations.getMillis(config.getString(path));
    }

    public static long getTicks(ConfigurationSection config, String path) {
        return Durations.getTicks(config.getString(path));
    }

    public static long getMillis(ConfigurationSection config, String path, long def) {
        if (config.getString(path, "").isBlank())
            return def;
        return Durations.getMillis(config.getString(path));
    }

    public static long getTicks(ConfigurationSection config, String path, long def) {
        if (config.getString(path, "").isBlank())
            return def;
        return Durations.getTicks(config.getString(path));
    }

    public static TimeSpanRange getTimeSpanRange(ConfigurationSection config, String path) {
        return Durations.getTimeSpanRange(config.getString(path));
    }

    public static VectorRange getVectorRange(ConfigurationSection config, String path) {
        return VectorRange.getVectorRange(config.getString(path));
    }

    public static LocationRange getLocationRange(ConfigurationSection config, String path) {
        return LocationRange.getLocationRange(config.getString(path));
    }

    public static <E extends Enum<E>> EnumSet<E> getEnumSet(ConfigurationSection section, String key, Class<E> enumClass) {
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

    public static <E extends Enum<E>> EnumSet<E> getKeyedEnumSet(ConfigurationSection section, String key, Class<E> enumClass) {
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

    public static Set<Material> getMaterialEnumSet(ConfigurationSection section, String path) {
        return section.getStringList(path).stream()
                .map(Material::matchMaterial)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static void saveDirectoryResource(File directory) {
        saveDirectoryResource(PluginUtils.getPlugin(), directory, false);
    }

    public static void saveDirectoryResource(File directory, boolean replace) {
        saveDirectoryResource(PluginUtils.getPlugin(), directory, replace);
    }

    public static void saveDirectoryResource(Plugin plugin, File directory) {
        saveDirectoryResource(plugin, directory, false);
    }

    public static void saveDirectoryResource(Plugin plugin, File directory, boolean force) {
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

    public static String getRelativePath(File file, File base) {
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
