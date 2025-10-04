package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.runnable.ForceField;
import me.wyne.wutils.common.Args;
import me.wyne.wutils.common.config.ConfigUtils;
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

public class ForceFieldAttribute extends ConfigurableAttribute<ForceFieldAttribute.ForceFieldData> implements ContextAnimationAttribute {

    public ForceFieldAttribute(String key, ForceFieldData value) {
        super(key, value);
    }

    public ForceFieldAttribute(ForceFieldData value) {
        super(AnimationAttribute.FORCE_FIELD.getKey(), value);
    }

    @Override
    public AnimationRunnable create(AnimationContext context) {
        if (context.getLocation() == null) return AnimationRunnable.Companion.getEMPTY();
        return new ForceField(context.getLocation().clone().add(getValue().offset()), getValue().radius(), getValue().velocity());
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().radius() + " " + getValue().velocity() + " " + VectorUtils.toString(getValue().offset())).buildNoSpace();
    }

    public record ForceFieldData(Vector offset, double radius, double velocity) {}

    public static final class Factory implements CompositeAttributeFactory {
        @Override
        public ForceFieldAttribute fromSection(String key, ConfigurationSection section) {
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
        public ForceFieldAttribute fromString(String key, String string) {
            var args = new Args(string, " ");
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
