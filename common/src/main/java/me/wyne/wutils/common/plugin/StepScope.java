package me.wyne.wutils.common.plugin;

/**
 * The lifecycle phase a {@link Step} runs in.
 *
 * <p>{@code LOAD}, {@code ENABLE} and {@code DISABLE} correspond directly to
 * {@link org.bukkit.plugin.Plugin#onLoad()}, {@link org.bukkit.plugin.Plugin#onEnable()} and
 * {@link org.bukkit.plugin.Plugin#onDisable()} and fire whenever Bukkit calls those methods.
 * {@code RELOAD} is not driven by Bukkit; it only fires when the plugin's own {@code reload()}
 * method is invoked, typically from a reload command.</p>
 */
public enum StepScope {
    /** Runs during {@link org.bukkit.plugin.Plugin#onLoad()}. */
    LOAD,
    /** Runs during {@link org.bukkit.plugin.Plugin#onEnable()}. */
    ENABLE,
    /** Runs when the plugin's {@code reload()} is invoked; not triggered by Bukkit itself. */
    RELOAD,
    /** Runs during {@link org.bukkit.plugin.Plugin#onDisable()}. */
    DISABLE
}
