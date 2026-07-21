package me.wyne.wutils.structure.modifier.edit;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.convolution.GaussianKernel;
import com.sk89q.worldedit.math.convolution.HeightMap;
import com.sk89q.worldedit.math.convolution.HeightMapFilter;
import com.sk89q.worldedit.math.convolution.Kernel;
import com.sk89q.worldedit.regions.Region;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.mask.MaskUtils;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class SmoothEditModifier extends MarginEditModifier<SmoothSettings> {

    private static final Kernel KERNEL = new GaussianKernel(5, 1.0);

    public SmoothEditModifier(@NotNull String key, @NotNull SmoothSettings value) {
        super(key, value);
    }

    public SmoothEditModifier(@NotNull SmoothSettings value) {
        super(StructureModifier.EDIT_SMOOTH.getKey(), value);
    }

    @Override
    protected int margin() {
        return getValue().margin();
    }

    @Override
    protected void applyEdit(@NotNull EditSession editSession, @NotNull Region region, @NotNull Mask ringMask) {
        var settings = getValue();
        Preconditions.checkNotNull(region.getWorld(), "Smooth modifier region world is null");
        Mask mask = settings.mask() == null ? null : MaskUtils.parseMask(settings.mask(), region.getWorld());
        HeightMap heightMap = new HeightMap(editSession, region, mask);
        try {
            heightMap.applyFilter(new HeightMapFilter(KERNEL), settings.iterations());
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException("Smooth modifier '" + getKey() + "' is changing too many blocks", e);
        }
    }

    public static final class Factory implements AttributeFactory<SmoothEditModifier> {
        @Override
        public SmoothEditModifier create(String key, ConfigurationSection config) {
            return new SmoothEditModifier(key, SmoothSettings.parse(config.getString(key, "")));
        }
    }
}
