package me.wyne.wutils.structure.modifier.location;

import me.wyne.wutils.common.operation.IntOperation;
import me.wyne.wutils.common.operation.Operations;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.structure.modifier.LocationModifier;
import me.wyne.wutils.structure.modifier.StructureModifier;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class AltitudeLocationModifier extends ConfigurableAttribute<IntOperation> implements LocationModifier {

    public AltitudeLocationModifier(@NotNull String key, @NotNull IntOperation value) {
        super(key, value);
    }

    public AltitudeLocationModifier(@NotNull IntOperation value) {
        super(StructureModifier.LOCATION_ALTITUDE.getKey(), value);
    }

    @Override
    public @NotNull Location apply(@NotNull Location location) {
        var result = location.clone();
        result.setY(getValue().evaluate(location.getBlockY()));
        return result;
    }

    public static final class Factory implements AttributeFactory<AltitudeLocationModifier> {
        @Override
        public @NotNull AltitudeLocationModifier create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new AltitudeLocationModifier(key, Operations.getIntOperation(config.getString(key, "+0")));
        }
    }
}
