package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.regions.Region;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Naturalizes the top layers of the margin region (grass/dirt/stone layering) via
 * {@link EditSession#naturalizeCuboidBlocks}. The attribute's value is the margin, in blocks.
 */
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
    protected void applyEdit(@NotNull EditSession editSession, @NotNull Region region, @NotNull Region clipboardRegion) {
        try {
            editSession.naturalizeCuboidBlocks(region);
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException("Naturalize modifier '" + getKey() + "' is changing too many blocks", e);
        }
    }

    public static final class Factory implements AttributeFactory<NaturalizeEditModifier> {
        @Override
        public @NotNull NaturalizeEditModifier create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new NaturalizeEditModifier(key, config.getInt(key));
        }
    }
}
