package me.wyne.wutils.structure.modifier.location;

import me.wyne.wutils.common.range.ClosedIntRange;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.structure.modifier.LocationModifier;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class OffsetLocationModifier extends ConfigurableAttribute<ClosedIntRange> implements LocationModifier {

    public OffsetLocationModifier(@NotNull String key, @NotNull ClosedIntRange value) {
        super(key, value);
    }

    public OffsetLocationModifier(@NotNull ClosedIntRange value) {
        super(StructureModifier.LOCATION_OFFSET.getKey(), value);
    }

    @Override
    public @NotNull Location apply(@NotNull Location location) {
        return location.clone().add(0, getValue().getRandom(), 0);
    }

    public static final class Factory implements AttributeFactory<OffsetLocationModifier> {
        @Override
        public @NotNull OffsetLocationModifier create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new OffsetLocationModifier(key, ClosedIntRange.getIntRange(config.getString(key, "0..0")));
        }
    }
}
