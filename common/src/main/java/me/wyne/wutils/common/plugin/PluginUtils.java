package me.wyne.wutils.common.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;

public final class PluginUtils {

    private static JavaPlugin plugin = null;

    @Nonnull
    public static synchronized JavaPlugin getPlugin() {
        if (plugin == null) {
            JavaPlugin pl = JavaPlugin.getProvidingPlugin(PluginUtils.class);
            plugin = pl;
        }

        return plugin;
    }

}
