package me.wyne.wutils.structure.scheme;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.common.plugin.PluginUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.GenericFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class RandomScheme implements Scheme {

    private final List<FileScheme> schemes = new ArrayList<>();
    @Nullable
    private final String path;

    public RandomScheme(@RegExp @NotNull String path) {
        this.path = path;
        this.schemes.addAll(listSchemes(path));
    }

    public RandomScheme(@NotNull Collection<@NotNull FileScheme> schemes) {
        this.path = null;
        this.schemes.addAll(schemes);
    }

    private @NotNull List<@NotNull FileScheme> listSchemes(@RegExp @NotNull String path) {
        var lastSlash = path.lastIndexOf(File.separatorChar);

        var directory = path.substring(0, lastSlash);
        var pattern = path.substring(lastSlash + 1);

        var regex = Pattern.compile(pattern);

        var schemeFiles = new File(PluginUtils.getPlugin().getDataFolder(), directory)
                .listFiles(file -> file.isFile() && regex.matcher(file.getName()).matches());
        if (schemeFiles == null) return new ArrayList<>();
        return Arrays.stream(schemeFiles)
                .map(file -> new FileScheme(directory + File.separator + file.getName()))
                .toList();
    }

    @Override
    public @NotNull Clipboard getClipboard() {
        if (schemes.isEmpty()) throw new IllegalStateException("Random scheme list is empty");
        return schemes.get(ThreadLocalRandom.current().nextInt(schemes.size())).getClipboard();
    }

    public static RandomScheme ofPaths(@NotNull Collection<@NotNull String> paths) {
        return new RandomScheme(paths.stream().map(FileScheme::new).toList());
    }

    public static RandomScheme ofFiles(@NotNull Collection<@NotNull File> files) {
        return new RandomScheme(files.stream().map(FileScheme::new).toList());
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder()
                .append(depth, "schemes", path)
                .buildNoTrail();
    }

    public static final class Factory implements GenericFactory<Scheme> {
        @Override
        public Scheme create(String key, ConfigurationSection config) {
            var section = ConfigUtils.getConfigurationSection(config, key);
            return new RandomScheme(section.getString("schemes", "scheme.schem"));
        }
    }

}
