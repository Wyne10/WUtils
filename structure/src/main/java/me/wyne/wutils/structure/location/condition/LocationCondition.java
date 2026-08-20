package me.wyne.wutils.structure.location.condition;

import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurables.attribute.GenericFactoryMap;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

/**
 * Validates a candidate placement {@link Location} against a structure-config-declared rule.
 *
 * <p>{@link #FACTORY_MAP} is the YAML condition vocabulary: each key is the literal config key
 * a structure's {@code conditions} section may use. Note that {@code is-in-ocean} and
 * {@code is-in-mountains} store the <em>inverted</em> boolean they read &mdash; the record's
 * {@code invert} flag means "must not be", so {@code is-in-ocean: true} is stored as
 * {@code invert = false} and means "must be in ocean".</p>
 */
public interface LocationCondition extends CompositeConfigSerializable {
    @NotNull GenericFactoryMap<@NotNull LocationCondition> FACTORY_MAP = new GenericFactoryMap<>(
            new LinkedHashMap<>() {{
                put("is-in-biome", (key, config) ->
                        new BiomeCondition(ConfigUtils.getKeyedEnumSet(config, key, Biome.class), false));
                put("is-not-in-biome", (key, config) ->
                        new BiomeCondition(ConfigUtils.getKeyedEnumSet(config, key, Biome.class), true));
                put("is-in-biome-preset", (key, config) ->
                        BiomePresetCondition.of(config.getStringList(key), false));
                put("is-not-in-biome-preset", (key, config) ->
                        BiomePresetCondition.of(config.getStringList(key), true));
                put("is-on-block", (key, config) ->
                        new BlockCondition(ConfigUtils.getMaterialEnumSet(config, key), false));
                put("is-not-on-block", (key, config) ->
                        new BlockCondition(ConfigUtils.getMaterialEnumSet(config, key), true));
                // inverted: "is-in-ocean: true" must store invert = false
                put("is-in-ocean", (key, config) ->
                        new OceanCondition(!config.getBoolean(key)));
                // inverted: "is-in-mountains: true" must store invert = false
                put("is-in-mountains", (key, config) ->
                        new MountainsCondition(!config.getBoolean(key)));
                put("altitude", (key, config) ->
                        new AltitudeCondition(ConfigUtils.getIntComparator(config, key)));
                put("temperature", (key, config) ->
                        new TemperatureCondition(ConfigUtils.getDoubleComparator(config, key)));
            }}
    );

    /**
     * Returns whether the candidate {@code location} satisfies this condition.
     */
    boolean isValid(@NotNull Location location);
}
