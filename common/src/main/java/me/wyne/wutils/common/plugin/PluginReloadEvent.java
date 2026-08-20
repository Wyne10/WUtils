package me.wyne.wutils.common.plugin;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Fired synchronously on the calling thread when {@link CompositePlugin#reload()} or
 * {@link CompositeJavaPlugin#reload()} is invoked, just before the {@link StepScope#RELOAD} steps
 * run. Not fired by Bukkit itself.
 */
public class PluginReloadEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Plugin plugin;

    public PluginReloadEvent(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * The plugin being reloaded.
     */
    public @NotNull Plugin getPlugin() {
        return plugin;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
