package me.wyne.wutils.common.plugin;

import javax.annotation.Nonnull;
import java.util.logging.Level;

public final class PluginLog {

    public static void info(@Nonnull String s) {
        PluginUtils.getPlugin().getLogger().info(s);
    }

    public static void warn(@Nonnull String s) {
        PluginUtils.getPlugin().getLogger().warning(s);
    }

    public static void severe(@Nonnull String s) {
        PluginUtils.getPlugin().getLogger().severe(s);
    }

    public static void warn(@Nonnull String s, Throwable t) {
        PluginUtils.getPlugin().getLogger().log(Level.WARNING, s, t);
    }

    public static void severe(@Nonnull String s, Throwable t) {
        PluginUtils.getPlugin().getLogger().log(Level.SEVERE, s, t);
    }

    private PluginLog() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}