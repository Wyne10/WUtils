package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.function.GroundFunction;
import com.sk89q.worldedit.function.generator.FloraGenerator;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.mask.Mask2D;
import com.sk89q.worldedit.function.mask.MaskIntersection2D;
import com.sk89q.worldedit.function.mask.NoiseFilter2D;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.visitor.LayerVisitor;
import com.sk89q.worldedit.math.noise.RandomNoise;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.Regions;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Scatters flora across the margin region's surface via WorldEdit's {@link FloraGenerator}, with
 * per-column probability driven by {@link FloraSettings#density()}.
 */
public class FloraEditModifier extends MarginEditModifier<FloraSettings> {

    public FloraEditModifier(@NotNull String key, @NotNull FloraSettings value) {
        super(key, value);
    }

    public FloraEditModifier(@NotNull FloraSettings value) {
        super(StructureModifier.EDIT_FLORA.getKey(), value);
    }

    @Override
    protected int margin() {
        return getValue().margin();
    }

    @Override
    protected boolean excludeFootprint() {
        return !getValue().includeClipboard();
    }

    @Override
    protected void applyEdit(@NotNull EditSession editSession, @NotNull Region region, @NotNull Region clipboardRegion) {
        FloraGenerator generator = new FloraGenerator(editSession);
        GroundFunction ground = new GroundFunction(new ExistingBlockMask(editSession), generator);
        LayerVisitor visitor = new LayerVisitor(
                Regions.asFlatRegion(region),
                Regions.minimumBlockY(region),
                Regions.maximumBlockY(region),
                ground);
        Mask2D density = new NoiseFilter2D(new RandomNoise(), getValue().density() / 100);
        visitor.setMask(getValue().includeClipboard()
                ? density
                : new MaskIntersection2D(density, outsideFootprint(clipboardRegion)));
        try {
            Operations.completeLegacy(visitor);
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException("Flora modifier '" + getKey() + "' is changing too many blocks", e);
        }
    }

    public static final class Factory implements AttributeFactory<FloraEditModifier> {
        @Override
        public @NotNull FloraEditModifier create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new FloraEditModifier(key, FloraSettings.parse(config.getString(key, "")));
        }
    }
}
