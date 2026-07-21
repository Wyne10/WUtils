package me.wyne.wutils.structure.modifier.edit;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.regions.Region;
import me.wyne.wutils.common.Args;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.structure.modifier.EditSessionModifier;
import me.wyne.wutils.structure.modifier.LazyMaskPatternPair;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class SetEditModifier extends ConfigurableAttribute<LazyMaskPatternPair> implements EditSessionModifier {

    public SetEditModifier(@NotNull String key, @NotNull LazyMaskPatternPair value) {
        super(key, value);
    }

    public SetEditModifier(@NotNull LazyMaskPatternPair value) {
        super(StructureModifier.EDIT_SET.getKey(), value);
    }

    @Override
    public void apply(@NotNull EditSession editSession, @NotNull Region region) {
        var previousMask = editSession.getMask();
        try {
            Preconditions.checkNotNull(region.getWorld(), "Set modifier region world is null");
            editSession.setMask(getValue().getMask(region.getWorld()));
            editSession.setBlocks(region, getValue().getPattern(region.getWorld()));
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException("Set modifier '" + getKey() + "' is changing too many blocks", e);
        } finally {
            editSession.setMask(previousMask);
        }
    }

    public static final class Factory implements AttributeFactory<SetEditModifier> {
        @Override
        public SetEditModifier create(String key, ConfigurationSection config) {
            var args = new Args(config.getString(key, ""));
            return new SetEditModifier(key, new LazyMaskPatternPair(args.get(0, ""), args.get(1, "")));
        }
    }

}
