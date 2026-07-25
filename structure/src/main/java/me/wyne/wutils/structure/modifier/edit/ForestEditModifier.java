package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.function.GroundFunction;
import com.sk89q.worldedit.function.generator.ForestGenerator;
import com.sk89q.worldedit.function.mask.ExistingBlockMask;
import com.sk89q.worldedit.function.mask.Mask;
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

public class ForestEditModifier extends MarginEditModifier<ForestSettings> {

    public ForestEditModifier(@NotNull String key, @NotNull ForestSettings value) {
        super(key, value);
    }

    public ForestEditModifier(@NotNull ForestSettings value) {
        super(StructureModifier.EDIT_FOREST.getKey(), value);
    }

    @Override
    protected int margin() {
        return getValue().margin();
    }

    @Override
    protected void applyEdit(@NotNull EditSession editSession, @NotNull Region region, @NotNull Region clipboardRegion, @NotNull Mask ringMask) {
        ForestGenerator generator = new ForestGenerator(editSession, getValue().type());
        GroundFunction ground = new GroundFunction(new ExistingBlockMask(editSession), generator);
        LayerVisitor visitor = new LayerVisitor(
                Regions.asFlatRegion(region),
                Regions.minimumBlockY(region),
                Regions.maximumBlockY(region),
                ground);
        visitor.setMask(new MaskIntersection2D(
                new NoiseFilter2D(new RandomNoise(), getValue().density() / 100),
                outsideFootprint(clipboardRegion)));
        try {
            Operations.completeLegacy(visitor);
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException("Forest modifier '" + getKey() + "' is changing too many blocks", e);
        }
    }

    public static final class Factory implements AttributeFactory<ForestEditModifier> {
        @Override
        public ForestEditModifier create(String key, ConfigurationSection config) {
            return new ForestEditModifier(key, ForestSettings.parse(config.getString(key, "")));
        }
    }
}
