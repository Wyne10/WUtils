package me.wyne.wutils.structure.location;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurables.attribute.GenericFactory;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public interface StructureLocation extends CompositeConfigSerializable {
    @NotNull Location getLocation();

    final class Factory implements GenericFactory<StructureLocation> {
        @Override
        public StructureLocation create(String key, ConfigurationSection config) {
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
