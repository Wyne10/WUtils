package me.wyne.wutils.config;

import com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Internal helper for resolving the owning plugin, its logger, and its server version. Not part of
 * this module's public API — package-private and duplicated with minor variations across several
 * WUtils modules.
 *
 * <p>{@link #getServerVersion()} and {@link #setPlugin(Plugin)} currently have no callers within this
 * module.</p>
 */
final class PluginUtils {

    public static final @NotNull Pattern VERSION_REGEX = Pattern.compile("(?<version>\\d+\\.\\d+)(?<patch>\\.\\d+)?");

    private static int currentServerVersion = 0;
    private static Plugin plugin = null;
    private static Logger logger = null;

    /**
     * Resolves the plugin providing this class, caching the result. Returns {@code null} if this class
     * is not being loaded as part of a plugin (e.g. in tests).
     */
    @Nullable
    public static synchronized Plugin getPlugin() {
        if (plugin == null) {
            try {
                plugin = JavaPlugin.getProvidingPlugin(PluginUtils.class);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }

        return plugin;
    }

    /**
     * Resolves the logger to use, caching the result. Uses the owning plugin's logger name if a plugin
     * is available, otherwise the given {@code fallback} class.
     *
     * <p>Because the resolved logger is cached in a static field, only the first caller's
     * {@code fallback} has any effect for the lifetime of the JVM — later callers get the same cached
     * logger regardless of the class they pass.</p>
     */
    @NotNull
    public static Logger getLogger(@NotNull Class<?> fallback) {
        if (logger == null) {
            var plugin = getPlugin();
            if (plugin != null)
                logger = LoggerFactory.getLogger(getPlugin().getLogger().getName());
            else
                logger = LoggerFactory.getLogger(fallback);
        }

        return logger;
    }

    /**
     * Parses the running server's Bukkit version (e.g. {@code 1.16.5} to {@code 1165}) into a
     * comparable integer, caching the result. Returns {@code 0} if the version string could not be
     * parsed.
     */
    public static int getServerVersion() {
        if (currentServerVersion != 0)
            return currentServerVersion;

        final Matcher matcher = VERSION_REGEX.matcher(Bukkit.getBukkitVersion());

        final StringBuilder stringBuilder = new StringBuilder();
        if (matcher.find()) {
            stringBuilder.append(matcher.group("version").replace(".", ""));
            final String patch = matcher.group("patch");
            if (patch == null) stringBuilder.append("0");
            else stringBuilder.append(patch.replace(".", ""));
        }

        //noinspection UnstableApiUsage
        final Integer version = Ints.tryParse(stringBuilder.toString());

        if (version == null)
            return 0;

        currentServerVersion = version;
        return version;
    }

    /**
     * Overrides the cached plugin instance, e.g. for tests.
     */
    public static void setPlugin(@NotNull Plugin plugin) {
        PluginUtils.plugin = plugin;
    }

}
