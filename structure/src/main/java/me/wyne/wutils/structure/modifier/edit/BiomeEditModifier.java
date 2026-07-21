package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.RegionMaskingFilter;
import com.sk89q.worldedit.function.biome.BiomeReplace;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.regions.Region;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class BiomeEditModifier extends MarginEditModifier<BiomeSettings> {

    public BiomeEditModifier(@NotNull String key, @NotNull BiomeSettings value) {
        super(key, value);
    }

    public BiomeEditModifier(@NotNull BiomeSettings value) {
        super(StructureModifier.EDIT_BIOME.getKey(), value);
    }

    @Override
    protected int margin() {
        return getValue().margin();
    }

    @Override
    protected void applyEdit(@NotNull EditSession editSession, @NotNull Region region, @NotNull Mask ringMask) {
        RegionFunction replace = new RegionMaskingFilter(ringMask, new BiomeReplace(editSession, getValue().biome()));
        RegionVisitor visitor = new RegionVisitor(region, replace);
        try {
            Operations.completeLegacy(visitor);
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException("Biome modifier '" + getKey() + "' is changing too many blocks", e);
        }
    }

    public static final class Factory implements AttributeFactory<BiomeEditModifier> {
        @Override
        public BiomeEditModifier create(String key, ConfigurationSection config) {
            return new BiomeEditModifier(key, BiomeSettings.parse(config.getString(key, "")));
        }
    }
}
