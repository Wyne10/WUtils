package me.wyne.wutils.structure.location;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurables.attribute.GenericFactory;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Supplies a candidate placement point for a structure.
 *
 * <p>{@link #getLocation()} returns a candidate only: the caller snaps it to the highest
 * solid block and re-validates it against the structure's location conditions before it is
 * used, so implementations are not required to produce a final, ready-to-paste location.</p>
 */
public interface StructureLocation extends CompositeConfigSerializable {

    /**
     * Returns a candidate placement point. The caller snaps it to the highest solid block
     * and re-validates it before use; this is not necessarily a valid final placement.
     */
    @NotNull Location getLocation();

    /**
     * Builds a {@link StructureLocation} from config, dispatching on which keys are present
     * in the section at {@code key}, in this order:
     * <ol>
     *     <li>{@code near-biome}, {@code far-biome}, or {@code biome-preset} &mdash; {@link BiomeLocation}</li>
     *     <li>{@code near-structure} or {@code far-structure} &mdash; {@link NearestStructureLocation}</li>
     *     <li>{@code range} &mdash; {@link RandomLocation}</li>
     *     <li>otherwise &mdash; {@link SetLocation}</li>
     * </ol>
     */
    final class Factory implements GenericFactory<StructureLocation> {
        @Override
        public @NotNull StructureLocation create(@NotNull String key, @NotNull ConfigurationSection config) {
            var section = ConfigUtils.getConfigurationSection(config, key);
            if (section.contains("near-biome") || section.contains("far-biome") || section.contains("biome-preset")) {
                return new BiomeLocation.Factory().create(key, config);
            } else if (section.contains("near-structure") || section.contains("far-structure")) {
                return new NearestStructureLocation.Factory().create(key, config);
            } else if (section.contains("range")) {
                return new RandomLocation.Factory().create(key, config);
            } else {
                return new SetLocation.Factory().create(key, config);
            }
        }
    }
}
