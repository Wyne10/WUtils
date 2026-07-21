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

public class ReplaceEditModifier extends ConfigurableAttribute<LazyMaskPatternPair> implements EditSessionModifier {

    public ReplaceEditModifier(@NotNull String key, @NotNull LazyMaskPatternPair value) {
        super(key, value);
    }

    public ReplaceEditModifier(@NotNull LazyMaskPatternPair value) {
        super(StructureModifier.EDIT_REPLACE.getKey(), value);
    }

    @Override
    public void apply(@NotNull EditSession editSession, @NotNull Region region) {
        try {
            Preconditions.checkNotNull(region.getWorld(), "Replace modifier region world is null");
            editSession.replaceBlocks(region, getValue().getMask(region.getWorld()), getValue().getPattern(region.getWorld()));
        } catch (MaxChangedBlocksException e) {
            throw new RuntimeException("Replace modifier '" + getKey() + "' is changing too many blocks", e);
        }
    }

    public static final class Factory implements AttributeFactory<ReplaceEditModifier> {
        @Override
        public ReplaceEditModifier create(String key, ConfigurationSection config) {
            var args = new Args(config.getString(key, ""));
            return new ReplaceEditModifier(key, new LazyMaskPatternPair(args.get(0, ""), args.get(1, "")));
        }
    }

}
