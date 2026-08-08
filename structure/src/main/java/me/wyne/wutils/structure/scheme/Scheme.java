package me.wyne.wutils.structure.scheme;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Location;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurables.attribute.GenericFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public interface Scheme extends CompositeConfigSerializable {
    @NotNull Clipboard getClipboard();

    static @NotNull Region toWorld(@NotNull Clipboard clipboard, @NotNull Location location, @NotNull Transform transform) {
        var region = clipboard.getRegion();
        var origin = clipboard.getOrigin();
        var to = location.toVector().toBlockPoint();
        var min = region.getMinimumPoint();
        var max = region.getMaximumPoint();

        BlockVector3 worldMin = null;
        BlockVector3 worldMax = null;
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    var corner = BlockVector3.at(
                            x == 0 ? min.getX() : max.getX(),
                            y == 0 ? min.getY() : max.getY(),
                            z == 0 ? min.getZ() : max.getZ());
                    var mapped = ClipboardScan.toWorld(corner, origin, to, transform);
                    worldMin = worldMin == null ? mapped : worldMin.getMinimum(mapped);
                    worldMax = worldMax == null ? mapped : worldMax.getMaximum(mapped);
                }
            }
        }
        return new CuboidRegion(worldMin, worldMax);
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
