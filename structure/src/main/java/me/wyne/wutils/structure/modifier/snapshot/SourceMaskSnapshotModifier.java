package me.wyne.wutils.structure.modifier.snapshot;

import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.world.World;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.structure.mask.MaskUtils;
import me.wyne.wutils.structure.modifier.SnapshotModifier;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class SourceMaskSnapshotModifier extends ConfigurableAttribute<String> implements SnapshotModifier {

    public SourceMaskSnapshotModifier(@NotNull String key, @NotNull String value) {
        super(key, value);
    }

    public SourceMaskSnapshotModifier(@NotNull String value) {
        super(StructureModifier.SNAPSHOT_SOURCE_MASK.getKey(), value);
    }

    @Override
    public void apply(@NotNull ForwardExtentCopy forwardExtentCopy, @NotNull World world) {
        forwardExtentCopy.setSourceMask(MaskUtils.parseMask(getValue(), world));
    }

    public static final class Factory implements AttributeFactory<SourceMaskSnapshotModifier> {
        @Override
        public SourceMaskSnapshotModifier create(String key, ConfigurationSection config) {
            return new SourceMaskSnapshotModifier(key, config.getString(key, ""));
        }
    }

}
