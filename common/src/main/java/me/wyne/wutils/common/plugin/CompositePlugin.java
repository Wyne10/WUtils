package me.wyne.wutils.common.plugin;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A {@link Plugin} decorator that adds the {@link Step}/{@link StepScope} pipeline to an existing
 * plugin instance.
 *
 * <p>Every {@link Plugin} method other than the lifecycle callbacks below is delegated verbatim to
 * the wrapped {@code T} passed to the constructor. Unlike {@link CompositeJavaPlugin}, this class
 * does not extend {@link org.bukkit.plugin.java.JavaPlugin}, so it is not itself a Bukkit plugin
 * main class that the server loader can instantiate — whoever constructs it is responsible for
 * calling {@link #onLoad()}, {@link #onEnable()} and {@link #onDisable()} at the appropriate
 * times, and steps run against the wrapped {@code T}, not against this object.</p>
 *
 * <p>{@link #onLoad()} collects both steps added via {@link #addStep}/{@link #addSteps} and
 * methods annotated {@link Step @Step} declared directly on the subclass, then runs every
 * {@link StepScope#LOAD} step. {@link #onEnable()} and {@link #onDisable()} run the
 * {@code ENABLE} and {@code DISABLE} steps respectively. {@link #reload()} is not part of the
 * {@link Plugin} interface and is never called by Bukkit; call it to fire a
 * {@link PluginReloadEvent} and run the {@code RELOAD} steps.</p>
 *
 * @param <T> the wrapped plugin type
 */
public class CompositePlugin<T extends Plugin> implements Plugin {

    private final T plugin;
    private final Set<PluginStep<T>> steps = new LinkedHashSet<>();

    public CompositePlugin(@NotNull T plugin) {
        this.plugin = plugin;
    }

    private void loadAnnotations() {
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Step.class))
                addStep(new AnnotationStep<>(method));
        }
    }

    /**
     * Hook invoked once during {@link #onLoad()}, after annotated steps are collected but before
     * any step runs. Override to register additional steps or perform other one-time setup.
     */
    public void init() {}

    @Override
    public void onLoad() {
        loadAnnotations();
        init();
        runSteps(StepScope.LOAD);
    }

    @Override
    public void onEnable() {
        runSteps(StepScope.ENABLE);
    }

    @Override
    public void onDisable() {
        runSteps(StepScope.DISABLE);
    }

    /**
     * Fires a {@link PluginReloadEvent} and runs every {@link StepScope#RELOAD} step. Not invoked
     * automatically; call this from a reload command or similar.
     */
    public void reload() {
        new PluginReloadEvent(this).callEvent();
        runSteps(StepScope.RELOAD);
    }

    /**
     * Registers a step to run when its scope is reached.
     */
    public void addStep(@NotNull PluginStep<T> step) {
        steps.add(step);
    }

    public void addSteps(@NotNull PluginStep<T>... steps) {
        this.steps.addAll(Set.of(steps));
    }

    private void runSteps(StepScope scope) {
        steps.stream()
                .filter(step -> step.getScope() == scope)
                .sorted(Comparator.comparingInt(PluginStep::getPriority))
                .forEachOrdered(step -> step.run(plugin));
    }

    @Override
    public @NotNull File getDataFolder() {
        return plugin.getDataFolder();
    }

    @Override
    public @NotNull PluginDescriptionFile getDescription() {
        return plugin.getDescription();
    }

    @Override
    public @NotNull FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    @Override
    public @Nullable InputStream getResource(@NotNull String filename) {
        return plugin.getResource(filename);
    }

    @Override
    public void saveConfig() {
        plugin.saveConfig();
    }

    @Override
    public void saveDefaultConfig() {
        plugin.saveDefaultConfig();
    }

    @Override
    public void saveResource(@NotNull String resourcePath, boolean replace) {
        plugin.saveResource(resourcePath, replace);
    }

    @Override
    public void reloadConfig() {
        plugin.reloadConfig();
    }

    @Override
    public @NotNull PluginLoader getPluginLoader() {
        return plugin.getPluginLoader();
    }

    @Override
    public @NotNull Server getServer() {
        return plugin.getServer();
    }

    @Override
    public boolean isEnabled() {
        return plugin.isEnabled();
    }

    @Override
    public boolean isNaggable() {
        return plugin.isNaggable();
    }

    @Override
    public void setNaggable(boolean canNag) {
        plugin.setNaggable(canNag);
    }

    @Override
    public @Nullable ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, @Nullable String id) {
        return plugin.getDefaultWorldGenerator(worldName, id);
    }

    @Override
    public @NotNull Logger getLogger() {
        return plugin.getLogger();
    }

    @Override
    public @NotNull String getName() {
        return plugin.getName();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return plugin.onCommand(sender, command, label, args);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return plugin.onTabComplete(sender, command, alias, args);
    }

}
