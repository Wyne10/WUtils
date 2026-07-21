package me.wyne.wutils.structure.modifier.paste;

import com.sk89q.worldedit.session.PasteBuilder;
import com.sk89q.worldedit.world.World;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.structure.modifier.PasteModifier;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class EntitiesPasteModifier extends ConfigurableAttribute<Boolean> implements PasteModifier {

    public EntitiesPasteModifier(@NotNull String key, @NotNull Boolean value) {
        super(key, value);
    }

    public EntitiesPasteModifier(@NotNull Boolean value) {
        super(StructureModifier.SNAPSHOT_ENTITIES.getKey(), value);
    }

    @Override
    public void apply(@NotNull PasteBuilder pasteBuilder, @NotNull World world) {
        pasteBuilder.copyEntities(getValue());
    }

    public static final class Factory implements AttributeFactory<EntitiesPasteModifier> {
        @Override
        public EntitiesPasteModifier create(String key, ConfigurationSection config) {
            return new EntitiesPasteModifier(key, config.getBoolean(key, false));
        }
    }

}
