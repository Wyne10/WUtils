package me.wyne.wutils.structure.region.condition;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurables.attribute.GenericFactoryMap;
import me.wyne.wutils.structure.IntermediateStructure;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Validates a structure's final protected region against a structure-config-declared rule.
 *
 * <p>{@link #FACTORY_MAP} is the YAML condition vocabulary: each key is the literal config
 * key a structure's {@code conditions} section may use. Region conditions are evaluated
 * <em>after</em> region modifiers have run, against the final {@link ProtectedCuboidRegion}
 * &mdash; unlike {@link me.wyne.wutils.structure.location.condition.LocationCondition}, which
 * runs before any placement modifier.</p>
 */
public interface RegionCondition extends CompositeConfigSerializable {
    @NotNull GenericFactoryMap<@NotNull RegionCondition> FACTORY_MAP = new GenericFactoryMap<>(
            new LinkedHashMap<>() {{
                put("region-whitelist", (key, config) ->
                        new RegionWhitelistCondition(Set.copyOf(config.getStringList(key))));
                put("altitude-difference", (key, config) ->
                        new AltitudeDifferenceCondition(ConfigUtils.getIntComparator(config, key)));
            }}
    );

    /**
     * Returns whether {@code region}, the final protected region built for
     * {@code intermediateStructure}, satisfies this condition.
     */
    boolean isValid(@NotNull IntermediateStructure intermediateStructure, @NotNull ProtectedCuboidRegion region);
}
