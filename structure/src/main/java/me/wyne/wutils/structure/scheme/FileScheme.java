package me.wyne.wutils.structure.scheme;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.common.plugin.PluginUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.GenericFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * A {@link Scheme} backed by a single schematic file, resolved by {@link ClipboardFormats} from its
 * extension.
 */
public class FileScheme implements Scheme {

    private final File file;
    private Clipboard clipboard;

    /**
     * Resolves {@code path} against the owning plugin's data folder, from {@link PluginUtils}.
     */
    public FileScheme(@NotNull String path) {
        this.file = new File(PluginUtils.getPlugin().getDataFolder(), path);
        preloadClipboard();
    }

    /**
     * Resolves {@code path} against {@code plugin}'s data folder.
     */
    public FileScheme(@NotNull String path, @NotNull Plugin plugin) {
        this.file = new File(plugin.getDataFolder(), path);
        preloadClipboard();
    }

    public FileScheme(@NotNull File file) {
        this.file = file;
        preloadClipboard();
    }

    /**
     * Loads (or reloads) the clipboard from disk.
     *
     * @throws IllegalArgumentException if the file's format cannot be detected
     * @throws RuntimeException         if reading the file fails
     */
    public @NotNull Clipboard loadClipboard() {
        var format = ClipboardFormats.findByFile(this.file);
        if (format == null) throw new IllegalArgumentException("Unable to detect scheme file format " + file.getName());
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load scheme file " + file.getName(), e);
        }
        return clipboard;
    }

    // Best-effort eager load so construction fails loudly in logs when the file is missing or
    // malformed at startup; a failure here is swallowed and simply retried lazily from
    // getClipboard(), rather than propagated out of the constructor.
    private void preloadClipboard() {
        if (!file.exists()) return;
        try {
            loadClipboard();
        } catch (Exception e) {
            PluginUtils.getLogger().warn("Failed to preload scheme {}, will retry lazily", file.getName());
        }
    }

    @Override
    public @NotNull Clipboard getClipboard() {
        if (clipboard == null) return loadClipboard();
        return clipboard;
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        var dataFolder = PluginUtils.getPlugin().getDataFolder();
        var path = dataFolder.toPath().relativize(file.toPath()).toString();
        return new ConfigBuilder()
                .append(depth, "scheme", path)
                .buildNoTrail();
    }

    public static final class Factory implements GenericFactory<Scheme> {
        @Override
        public Scheme create(String key, ConfigurationSection config) {
            var section = ConfigUtils.getConfigurationSection(config, key);
            return new FileScheme(section.getString("scheme", "scheme.schem"));
        }
    }

}
