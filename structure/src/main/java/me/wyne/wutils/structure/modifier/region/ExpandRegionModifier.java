package me.wyne.wutils.structure.modifier.region;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class ExpandRegionModifier extends DeltaRegionModifier<DirectionalAmount> {

    public ExpandRegionModifier(@NotNull String key, @NotNull DirectionalAmount value) {
        super(key, value);
    }

    public ExpandRegionModifier(@NotNull DirectionalAmount value) {
        super(StructureModifier.REGION_EXPAND.getKey(), value);
    }

    @Override
    public @NotNull ProtectedCuboidRegion apply(@NotNull ProtectedCuboidRegion region, @NotNull Region clipboardRegion) {
        var delta = getValue().delta();
        return resize(region,
                region.getMinimumPoint().add(negative(delta)),
                region.getMaximumPoint().add(positive(delta)));
    }

    public static final class Factory implements AttributeFactory<ExpandRegionModifier> {
        @Override
        public ExpandRegionModifier create(String key, ConfigurationSection config) {
            return new ExpandRegionModifier(key, DirectionalAmount.parse(config.getString(key, "")));
        }
    }
}