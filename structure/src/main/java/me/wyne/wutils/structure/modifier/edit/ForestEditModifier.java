package me.wyne.wutils.structure.modifier.edit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.regions.Region;
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
    protected void applyEdit(@NotNull EditSession editSession, @NotNull Region region, @NotNull Mask ringMask) {
        try {
            editSession.makeForest(region, getValue().density() / 100, getValue().type());
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
