package me.wyne.wutils.config.configurables.animation.attribute;

import com.google.common.base.Preconditions;
import me.wyne.wutils.animation.data.AnimationParticle;
import me.wyne.wutils.common.Args;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.common.particle.DataParserProvider;
import me.wyne.wutils.common.vector.VectorUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.CompositeAttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;

public class AnimationParticleAttribute extends ConfigurableAttribute<AnimationParticle> {

    public AnimationParticleAttribute(String key, AnimationParticle value) {
        super(key, value);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().particle().name() + " " + getValue().count() + " " + getValue().extra() + " " + getValue().offset() + " " + DataParserProvider.getDataParser(getValue().particle().getDataType()).toString(getValue().data())).buildNoSpace();
    }

    public static final class Factory implements CompositeAttributeFactory<AnimationParticleAttribute> {
        @Override
        public AnimationParticleAttribute fromSection(String key, ConfigurationSection section) {
            var particleKey = Preconditions.checkNotNull(section.getString("particle"), "No particle provided for " + section.getCurrentPath());
            var particle = ConfigUtils.getByName(particleKey, Particle.class);
            Preconditions.checkNotNull(particle, "Invalid particle at " + section.getCurrentPath());
            Preconditions.checkArgument(particle.getDataType() != Void.class && !section.contains("data"), "Particle of type '" + particle.name() + "' requires extra data of type '" + particle.getDataType().getSimpleName() + "'");
            return new AnimationParticleAttribute(
                    key,
                    new AnimationParticle(
                            particle,
                            section.getInt("count", 1),
                            section.getDouble("extra", 1.0),
                            ConfigUtils.getVectorOrZero(section, "offset"),
                            DataParserProvider.getDataParser(particle.getDataType()).getData(section.getString("data"))
                    )
            );
        }

        @Override
        public AnimationParticleAttribute fromString(String key, String string, ConfigurationSection config) {
            var args = new Args(string);
            var particleKey = Preconditions.checkNotNull(args.getNullable(0), "No particle provided for " + ConfigUtils.getPath(config, key));
            var particle = ConfigUtils.getByName(particleKey, Particle.class);
            Preconditions.checkNotNull(particle, "Invalid particle at " + ConfigUtils.getPath(config, key));
            Preconditions.checkArgument(particle.getDataType() != Void.class && args.size() < 5, "Particle of type '" + particle.name() + "' requires extra data of type '" + particle.getDataType().getSimpleName() + "'");
            return new AnimationParticleAttribute(
                    key,
                    new AnimationParticle(
                            particle,
                            Integer.parseInt(args.get(1, "1")),
                            Double.parseDouble(args.get(2, "1.0")),
                            VectorUtils.getVectorOrZero(args.get(3)),
                            DataParserProvider.getDataParser(particle.getDataType()).getData(args.get(4))
                    )
            );
        }
    }

}
