package me.wyne.wutils.common.plugin;

import com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PluginUtils {

    public static final Pattern VERSION_REGEX = Pattern.compile("(?<version>\\d+\\.\\d+)(?<patch>\\.\\d+)?");

    private static int currentServerVersion = 0;
    private static Plugin plugin = null;

    @Nonnull
    public static synchronized Plugin getPlugin() {
        if (plugin == null) {
            JavaPlugin pl = JavaPlugin.getProvidingPlugin(PluginUtils.class);
            plugin = pl;
        }

        return plugin;
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
