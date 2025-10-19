package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.runnable.LocalSound;
import me.wyne.wutils.common.Args;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.animation.ContextAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.CompositeAttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;

public class LocalSoundAttribute extends ConfigurableAttribute<Sound> implements ContextAnimationAttribute {

    public LocalSoundAttribute(String key, Sound value) {
        super(key, value);
    }

    public LocalSoundAttribute(Sound value) {
        super(AnimationAttribute.LOCAL_SOUND.getKey(), value);
    }

    @Override
    public AnimationRunnable create(AnimationContext context) {
        if (context.getLocation() == null) return AnimationRunnable.EMPTY;
        return new LocalSound(
                context.getLocation(),
                Arrays.stream(org.bukkit.Sound.values())
                        .filter(sound -> getValue().name().value().equals(sound.getKey().value()))
                        .findAny().orElse(null),
                getValue().volume(),
                getValue().pitch()
        );
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().name().asString() + " " + getValue().volume() + " " + getValue().pitch()).buildNoSpace();
    }

    public static final class Factory implements CompositeAttributeFactory {
        @Override
        public LocalSoundAttribute fromSection(String key, ConfigurationSection section) {
            return new LocalSoundAttribute(
                    key,
                    Sound.sound(Key.key(section.getString("sound", "entity.item.pickup")),
                            Sound.Source.MASTER,
                            (float) section.getDouble("volume", 1.0),
                            (float) section.getDouble("pitch", 1.0)
                    )
            );
        }

        @Override
        public LocalSoundAttribute fromString(String key, String string) {
            var args = new Args(string, " ");
            return new LocalSoundAttribute(
                    key,
                    Sound.sound(Key.key(args.get(0, "entity.item.pickup")),
                            Sound.Source.MASTER,
                            Float.parseFloat(args.get(1, "1.0")),
                            Float.parseFloat(args.get(2, "1.0"))
                    )
            );
        }
    }

}
