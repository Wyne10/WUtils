package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.runnable.ForceField;
import me.wyne.wutils.common.Args;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.common.location.LocationUtils;
import me.wyne.wutils.common.vector.VectorUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.animation.ContextAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.CompositeAttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code forceField} effect — pushes or pulls nearby entities around a point offset from the
 * context location. Needs a location; returns {@link AnimationRunnable#EMPTY} when the context has
 * none. The string form is {@code radius velocity offset}, in that order — note this does not match
 * {@link ForceFieldData}'s own component order, {@code (offset, radius, velocity)}.
 */
public class ForceFieldAttribute extends ConfigurableAttribute<ForceFieldAttribute.ForceFieldData> implements ContextAnimationAttribute<AnimationContext> {

    public ForceFieldAttribute(@NotNull String key, @NotNull ForceFieldData value) {
        super(key, value);
    }

    public ForceFieldAttribute(@NotNull ForceFieldData value) {
        super(AnimationAttribute.FORCE_FIELD.getKey(), value);
    }

    @Override
    public @NotNull AnimationRunnable create(@NotNull AnimationContext context) {
        if (context.getLocation() == null) return AnimationRunnable.EMPTY;
        return new ForceField(LocationUtils.addRelative(context.getLocation(), getValue().offset()), getValue().radius(), getValue().velocity());
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().radius() + " " + getValue().velocity() + " " + getValue().offset()).buildNoSpace();
    }

    public record ForceFieldData(@NotNull Vector offset, double radius, double velocity) {}

    public static final class Factory implements CompositeAttributeFactory<ForceFieldAttribute> {
        @Override
        public @NotNull ForceFieldAttribute fromSection(@NotNull String key, @NotNull ConfigurationSection section) {
            return new ForceFieldAttribute(
                    key,
                    new ForceFieldData(
                            ConfigUtils.getVectorOrZero(section, "offset"),
                            section.getDouble("radius", 1.0),
                            section.getDouble("velocity", 1.0)
                    )
            );
        }

        @Override
        public @NotNull ForceFieldAttribute fromString(@NotNull String key, @NotNull String string, @NotNull ConfigurationSection config) {
            var args = new Args(string);
            return new ForceFieldAttribute(
                    key,
                    new ForceFieldData(
                            VectorUtils.getVectorOrZero(args.get(2)),
                            Double.parseDouble(args.get(0, "1.0")),
                            Double.parseDouble(args.get(1, "1.0"))
                    )
            );
        }
    }

}
