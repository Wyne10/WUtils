package me.wyne.wutils.structure.modifier.snapshot;

import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.world.World;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.structure.modifier.SnapshotModifier;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class BiomesSnapshotModifier extends ConfigurableAttribute<Boolean> implements SnapshotModifier {

    public BiomesSnapshotModifier(@NotNull String key, @NotNull Boolean value) {
        super(key, value);
    }

    public BiomesSnapshotModifier(@NotNull Boolean value) {
        super(StructureModifier.SNAPSHOT_BIOMES.getKey(), value);
    }

    @Override
    public void apply(@NotNull ForwardExtentCopy forwardExtentCopy, @NotNull World world) {
        forwardExtentCopy.setCopyingBiomes(getValue());
    }

    public static final class Factory implements AttributeFactory<BiomesSnapshotModifier> {
        @Override
        public BiomesSnapshotModifier create(String key, ConfigurationSection config) {
            return new BiomesSnapshotModifier(key, config.getBoolean(key, false));
        }
    }

}
