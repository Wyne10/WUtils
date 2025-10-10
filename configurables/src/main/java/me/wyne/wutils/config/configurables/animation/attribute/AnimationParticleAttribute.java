package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.data.AnimationParticle;
import me.wyne.wutils.common.Args;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.common.particle.DataParserProvider;
import me.wyne.wutils.common.vector.VectorUtils;
import me.wyne.wutils.config.configurables.attribute.AttributeBase;
import me.wyne.wutils.config.configurables.attribute.CompositeAttributeFactory;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;

public class AnimationParticleAttribute extends AttributeBase<AnimationParticle> {

    public AnimationParticleAttribute(String key, AnimationParticle value) {
        super(key, value);
    }

    public static final class Factory implements CompositeAttributeFactory {
        @Override
        public AnimationParticleAttribute fromSection(String key, ConfigurationSection section) {
            var particle = Particle.valueOf(section.getString("particle", "FLAME"));
            if (particle.getDataType() != Void.class && !section.contains("data"))
                throw new NullPointerException("Particle of type '" + particle.name() + "' requires extra data of type '" + particle.getDataType().getSimpleName() + "'");
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
        public AnimationParticleAttribute fromString(String key, String string) {
            var args = new Args(string, " ");
            var particle = Particle.valueOf(args.get(0, "FLAME"));
            if (particle.getDataType() != Void.class && args.size() < 5)
                throw new NullPointerException("Particle of type '" + particle.name() + "' requires extra data of type '" + particle.getDataType().getSimpleName() + "'");
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
