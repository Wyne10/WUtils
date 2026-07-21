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

public class FileScheme implements Scheme {

    private final File file;
    private Clipboard clipboard;

    public FileScheme(@NotNull String path) {
        this.file = new File(PluginUtils.getPlugin().getDataFolder(), path);
    }

    public FileScheme(@NotNull String path, @NotNull Plugin plugin) {
        this.file = new File(plugin.getDataFolder(), path);
    }

    public FileScheme(@NotNull File file) {
        this.file = file;
    }

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

    @Override
    public @NotNull Clipboard getClipboard() {
        if (clipboard == null) return loadClipboard();
        return clipboard;
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
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
