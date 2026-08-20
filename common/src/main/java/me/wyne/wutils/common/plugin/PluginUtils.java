package me.wyne.wutils.common.plugin;

import com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Static helpers for locating the owning plugin and the running server version.
 */
public final class PluginUtils {

    /**
     * Matches a Bukkit version string's {@code major.minor} and optional {@code .patch}
     * segments, e.g. against {@link Bukkit#getBukkitVersion()}.
     */
    public static final @NotNull Pattern VERSION_REGEX = Pattern.compile("(?<version>\\d+\\.\\d+)(?<patch>\\.\\d+)?");

    private static int currentServerVersion = 0;
    private static Plugin plugin = null;
    private static Logger logger = null;

    /**
     * Returns the plugin that owns the classloader {@link PluginUtils} was loaded by, caching
     * the result after the first call. Delegates to {@link JavaPlugin#getProvidingPlugin}, which
     * throws if this class was not loaded from a plugin jar.
     */
    public static synchronized @NotNull Plugin getPlugin() {
        if (plugin == null) {
            plugin = JavaPlugin.getProvidingPlugin(PluginUtils.class);
        }

        return plugin;
    }

    /**
     * Returns {@link #getPlugin()}'s SLF4J logger, caching the result after the first call.
     */
    public static @NotNull Logger getLogger() {
        if (logger == null) {
            logger = getPlugin().getSLF4JLogger();
        }

        return logger;
    }

    /**
     * Parses {@link Bukkit#getBukkitVersion()} into a comparable integer combining the major,
     * minor and patch segments (e.g. {@code 1.16.5} becomes {@code 1165}), caching the result
     * after the first successful parse. Returns {@code 0} if the version string could not be
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
     * Overrides the plugin {@link #getPlugin()} returns, bypassing classloader lookup.
     */
    public static void setPlugin(@NotNull Plugin plugin) {
        PluginUtils.plugin = plugin;
    }

}
