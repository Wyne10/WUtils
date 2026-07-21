package me.wyne.wutils.structure.modifier.paste;

import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldedit.world.World;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.structure.mask.MaskUtils;
import me.wyne.wutils.structure.modifier.PasteModifier;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class MaskSourcePasteModifier extends ConfigurableAttribute<String> implements PasteModifier {

    public MaskSourcePasteModifier(@NotNull String key, @NotNull String value) {
        super(key, value);
    }

    public MaskSourcePasteModifier(@NotNull String value) {
        super(StructureModifier.PASTE_SOURCE_MASK.getKey(), value);
    }

    @Override
    public void apply(@NotNull PasteBuilder pasteBuilder, @NotNull World world) {
        pasteBuilder.maskSource(MaskUtils.parseMask(getValue(), world));
    }

    public static final class Factory implements AttributeFactory<MaskSourcePasteModifier> {
        @Override
        public MaskSourcePasteModifier create(String key, ConfigurationSection config) {
            return new MaskSourcePasteModifier(key, config.getString(key, ""));
        }
    }

}
