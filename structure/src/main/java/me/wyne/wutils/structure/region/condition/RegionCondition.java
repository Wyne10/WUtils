package me.wyne.wutils.structure.region.condition;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurables.attribute.GenericFactoryMap;
import me.wyne.wutils.structure.IntermediateStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Set;

public interface RegionCondition extends CompositeConfigurable {
    GenericFactoryMap<RegionCondition> FACTORY_MAP = new GenericFactoryMap<>(
            new LinkedHashMap<>() {{
                put("region-whitelist", (key, config) ->
                        new RegionWhitelistCondition(Set.copyOf(config.getStringList(key))));
                put("altitude-difference", (key, config) ->
                        new AltitudeDifferenceCondition(ConfigUtils.getIntComparator(config, key)));
            }}
    );

    boolean isValid(@NotNull IntermediateStructure intermediateStructure, @NotNull ProtectedCuboidRegion region);

    @Override
    default void fromConfig(@Nullable Object configObject) {
        throw new UnsupportedOperationException("RegionCondition is deserialized via RegionCondition.FACTORY_MAP");
    }
}
