package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.prefab.WorldParticleEffect;
import me.wyne.wutils.common.Args;
import me.wyne.wutils.common.ConfigUtils;
import me.wyne.wutils.common.VectorUtils;
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

public class WorldParticleEffectAttribute extends ConfigurableAttribute<WorldParticleEffectAttribute.ParticleData> implements ContextAnimationAttribute {

    public WorldParticleEffectAttribute(String key, ParticleData value) {
        super(key, value);
    }

    public WorldParticleEffectAttribute(ParticleData value) {
        super(AnimationAttribute.WORLD_PARTICLE_EFFECT.getKey(), value);
    }

    @Override
    public AnimationRunnable create(AnimationContext context) {
        if (context.getLocation() == null) return AnimationRunnable.Companion.getEMPTY();
        return new WorldParticleEffect(
                context.getLocation(),
                getValue().particle(),
                getValue().count(),
                getValue().speed(),
                getValue().offset()
        );
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(),
                getValue().particle().name() + " " +
                        getValue().count() + " " +
                        getValue().speed() + " " +
                        VectorUtils.toString(getValue().offset())
        ).buildNoSpace();
    }

    public record ParticleData(Particle particle, int count, double speed, Vector offset) {}

    public static final class Factory implements CompositeAttributeFactory {
        @Override
        public WorldParticleEffectAttribute fromSection(String key, ConfigurationSection section) {
            return new WorldParticleEffectAttribute(
                    key,
                    new ParticleData(
                            Particle.valueOf(section.getString("particle", "FLAME").toUpperCase()),
                            section.getInt("count", 1),
                            section.getDouble("speed", 1.0),
                            ConfigUtils.getVectorOrZero(section, "offset")
                    )
            );
        }

        @Override
        public WorldParticleEffectAttribute fromString(String key, String string) {
            var args = new Args(string, " ");
            return new WorldParticleEffectAttribute(
                    key,
                    new ParticleData(
                            Particle.valueOf(args.get(0, "FLAME").toUpperCase()),
                            Integer.parseInt(args.get(1, "1")),
                            Double.parseDouble(args.get(2, "1.0")),
                            VectorUtils.getVectorOrZero(args.get(3))
                    )
            );
        }
    }

}
