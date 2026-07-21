package me.wyne.wutils.structure.scheme;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.util.Location;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurables.attribute.GenericFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Scheme extends CompositeConfigurable {
    @NotNull Clipboard getClipboard();

    @Override
    default void fromConfig(@Nullable Object configObject) {
        throw new UnsupportedOperationException("Scheme is deserialized via Scheme.Factory");
    }

    static @NotNull Region toWorld(@NotNull Clipboard clipboard, @NotNull Location location) {
        var region = clipboard.getRegion();
        try {
            region.shift(location.toVector().toBlockPoint().subtract(clipboard.getOrigin()));
        } catch (RegionOperationException e) {
            throw new RuntimeException("Can't perform toWorld region operation", e);
        }
        return region;
    }

    final class Factory implements GenericFactory<Scheme> {
        @Override
        public Scheme create(String key, ConfigurationSection config) {
            if (ConfigUtils.getConfigurationSection(config, key).contains("scheme")) {
                return new FileScheme.Factory().create(key, config);
            } else {
                return new RandomScheme.Factory().create(key, config);
            }
        }
    }
}
