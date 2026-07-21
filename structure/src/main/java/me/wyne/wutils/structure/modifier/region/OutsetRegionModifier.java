package me.wyne.wutils.structure.modifier.region;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class OutsetRegionModifier extends DeltaRegionModifier<ScopedAmount> {

    public OutsetRegionModifier(@NotNull String key, @NotNull ScopedAmount value) {
        super(key, value);
    }

    public OutsetRegionModifier(@NotNull ScopedAmount value) {
        super(StructureModifier.REGION_OUTSET.getKey(), value);
    }

    @Override
    public @NotNull ProtectedCuboidRegion apply(@NotNull ProtectedCuboidRegion region, @NotNull Region clipboardRegion) {
        int h = getValue().horizontalAmount();
        int v = getValue().verticalAmount();
        return resize(region,
                region.getMinimumPoint().subtract(h, v, h),
                region.getMaximumPoint().add(h, v, h));
    }

    public static final class Factory implements AttributeFactory<OutsetRegionModifier> {
        @Override
        public OutsetRegionModifier create(String key, ConfigurationSection config) {
            return new OutsetRegionModifier(key, ScopedAmount.parse(config.getString(key, "")));
        }
    }
}