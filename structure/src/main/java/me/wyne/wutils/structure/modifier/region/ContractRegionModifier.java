package me.wyne.wutils.structure.modifier.region;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class ContractRegionModifier extends DeltaRegionModifier<DirectionalAmount> {

    public ContractRegionModifier(@NotNull String key, @NotNull DirectionalAmount value) {
        super(key, value);
    }

    public ContractRegionModifier(@NotNull DirectionalAmount value) {
        super(StructureModifier.REGION_CONTRACT.getKey(), value);
    }

    @Override
    public @NotNull ProtectedCuboidRegion apply(@NotNull ProtectedCuboidRegion region, @NotNull Region clipboardRegion) {
        var delta = getValue().delta();
        return resize(region,
                region.getMinimumPoint().subtract(negative(delta)),
                region.getMaximumPoint().subtract(positive(delta)));
    }

    public static final class Factory implements AttributeFactory<ContractRegionModifier> {
        @Override
        public ContractRegionModifier create(String key, ConfigurationSection config) {
            return new ContractRegionModifier(key, DirectionalAmount.parse(config.getString(key, "")));
        }
    }
}