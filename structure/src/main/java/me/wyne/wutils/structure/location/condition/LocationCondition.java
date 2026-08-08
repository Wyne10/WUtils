package me.wyne.wutils.structure.location.condition;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurables.attribute.GenericFactoryMap;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

public interface LocationCondition extends CompositeConfigSerializable {
    GenericFactoryMap<LocationCondition> FACTORY_MAP = new GenericFactoryMap<>(
            new LinkedHashMap<>() {{
                put("is-in-biome", (key, config) ->
                        new BiomeCondition(ConfigUtils.getKeyedEnumSet(config, key, Biome.class), false));
                put("is-not-in-biome", (key, config) ->
                        new BiomeCondition(ConfigUtils.getKeyedEnumSet(config, key, Biome.class), true));
                put("is-on-block", (key, config) ->
                        new BlockCondition(ConfigUtils.getMaterialEnumSet(config, key), false));
                put("is-not-on-block", (key, config) ->
                        new BlockCondition(ConfigUtils.getMaterialEnumSet(config, key), true));
                put("is-in-ocean", (key, config) ->
                        new OceanCondition(!config.getBoolean(key)));
                put("is-in-mountains", (key, config) ->
                        new MountainsCondition(!config.getBoolean(key)));
                put("altitude", (key, config) ->
                        new AltitudeCondition(ConfigUtils.getIntComparator(config, key)));
            }}
    );

    boolean isValid(@NotNull Location location);
}
