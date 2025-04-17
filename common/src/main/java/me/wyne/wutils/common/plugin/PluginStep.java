package me.wyne.wutils.common.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public interface PluginStep {
    void run(JavaPlugin plugin);
    StepScope getScope();
    default int getPriority() {
        return 0;
    }
}
