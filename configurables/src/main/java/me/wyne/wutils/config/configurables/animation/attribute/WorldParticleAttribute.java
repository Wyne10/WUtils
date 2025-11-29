package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.data.AnimationParticle;
import me.wyne.wutils.animation.runnable.WorldParticle;
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
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

public class WorldParticleAttribute extends ConfigurableAttribute<WorldParticleAttribute.ParticleData> implements ContextAnimationAttribute {

    public WorldParticleAttribute(String key, ParticleData value) {
        super(key, value);
    }

    public WorldParticleAttribute(ParticleData value) {
        super(AnimationAttribute.WORLD_PARTICLE.getKey(), value);
    }

    @Override
    public AnimationRunnable create(AnimationContext context) {
        if (context.getLocation() == null) return AnimationRunnable.EMPTY;
        return new WorldParticle(
                context.getLocation(),
                new AnimationParticle(
                        getValue().particle(),
                        getValue().count(),
                        getValue().speed(),
                        getValue().offset(),
                        null
                )
        );
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(),
                getValue().particle().name() + " " +
                        getValue().count() + " " +
                        getValue().speed() + " " +
                        getValue().offset()
        ).buildNoSpace();
    }

    public record ParticleData(Particle particle, int count, double speed, Vector offset) {}

    public static final class Factory implements CompositeAttributeFactory<WorldParticleAttribute> {
        @Override
        public WorldParticleAttribute fromSection(String key, ConfigurationSection section) {
            return new WorldParticleAttribute(
                    key,
                    new ParticleData(
                            Particle.valueOf(section.getString("particle", "FLAME")),
                            section.getInt("count", 1),
                            section.getDouble("speed", 1.0),
                            ConfigUtils.getVectorOrZero(section, "offset")
                    )
            );
        }

        @Override
        public WorldParticleAttribute fromString(String key, String string, ConfigurationSection config) {
            var args = new Args(string, " ");
            return new WorldParticleAttribute(
                    key,
                    new ParticleData(
                            Particle.valueOf(args.get(0, "FLAME")),
                            Integer.parseInt(args.get(1, "1")),
                            Double.parseDouble(args.get(2, "1.0")),
                            VectorUtils.getVectorOrZero(args.get(3))
                    )
            );
        }
    }

}
