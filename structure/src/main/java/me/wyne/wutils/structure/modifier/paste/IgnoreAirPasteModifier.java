package me.wyne.wutils.structure.modifier.paste;

import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldedit.world.World;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.structure.modifier.PasteModifier;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class IgnoreAirPasteModifier extends ConfigurableAttribute<Boolean> implements PasteModifier {

    public IgnoreAirPasteModifier(@NotNull String key, @NotNull Boolean value) {
        super(key, value);
    }

    public IgnoreAirPasteModifier(@NotNull Boolean value) {
        super(StructureModifier.PASTE_IGNORE_AIR.getKey(), value);
    }

    @Override
    public void apply(@NotNull PasteBuilder pasteBuilder, @NotNull World world) {
        pasteBuilder.ignoreAirBlocks(getValue());
    }

    public static final class Factory implements AttributeFactory<IgnoreAirPasteModifier> {
        @Override
        public IgnoreAirPasteModifier create(String key, ConfigurationSection config) {
            return new IgnoreAirPasteModifier(key, config.getBoolean(key, false));
        }
    }

}
