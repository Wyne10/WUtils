package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.regions.Region;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class NaturalizeEditModifier extends MarginEditModifier<Integer> {

    public NaturalizeEditModifier(@NotNull String key, @NotNull Integer value) {
        super(key, value);
    }

    public NaturalizeEditModifier(@NotNull Integer value) {
        super(StructureModifier.EDIT_NATURALIZE.getKey(), value);
    }

    @Override
    protected int margin() {
        return getValue();
    }

    @Override
    protected void applyEdit(@NotNull EditSession editSession, @NotNull Region region, @NotNull Mask ringMask) {
        try {
            editSession.naturalizeCuboidBlocks(region);
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException("Naturalize modifier '" + getKey() + "' is changing too many blocks", e);
        }
    }

    public static final class Factory implements AttributeFactory<NaturalizeEditModifier> {
        @Override
        public NaturalizeEditModifier create(String key, ConfigurationSection config) {
            return new NaturalizeEditModifier(key, config.getInt(key));
        }
    }
}
