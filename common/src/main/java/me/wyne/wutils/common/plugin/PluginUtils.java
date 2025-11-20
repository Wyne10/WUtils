package me.wyne.wutils.common.plugin;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;

public final class PluginUtils {

    private static Plugin plugin = null;

    @Nonnull
    public static synchronized Plugin getPlugin() {
        if (plugin == null) {
            JavaPlugin pl = JavaPlugin.getProvidingPlugin(PluginUtils.class);
            plugin = pl;
        }

        return plugin;
    }

    public static void setPlugin(Plugin plugin) {
        PluginUtils.plugin = plugin;
    }

}
