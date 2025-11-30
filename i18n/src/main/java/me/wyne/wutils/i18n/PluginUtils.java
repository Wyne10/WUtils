package me.wyne.wutils.i18n;

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

final class PluginUtils {

    public static final Pattern VERSION_REGEX = Pattern.compile("(?<version>\\d+\\.\\d+)(?<patch>\\.\\d+)?");

    private static int currentServerVersion = 0;
    private static Plugin plugin = null;
    private static Logger logger = null;

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

    @NotNull
    public static Logger getLogger(Class<?> fallback) {
        if (logger == null) {
            var plugin = getPlugin();
            if (plugin != null)
                logger = LoggerFactory.getLogger(getPlugin().getLogger().getName());
            else
                logger = LoggerFactory.getLogger(fallback);
        }

        return logger;
    }

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

    public static void setPlugin(Plugin plugin) {
        PluginUtils.plugin = plugin;
    }

}
