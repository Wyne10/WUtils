package me.wyne.wutils.structure.location;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurables.attribute.GenericFactory;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface StructureLocation extends CompositeConfigurable {
    @NotNull Location getLocation();

    @Override
    default void fromConfig(@Nullable Object configObject) {
        throw new UnsupportedOperationException("StructureLocation is deserialized via StructureLocation.Factory");
    }

    final class Factory implements GenericFactory<StructureLocation> {
        @Override
        public StructureLocation create(String key, ConfigurationSection config) {
            if (ConfigUtils.getConfigurationSection(config, key).contains("range")) {
                return new RandomLocation.Factory().create(key, config);
            } else {
                return new SetLocation.Factory().create(key, config);
            }
        }
    }
}
