package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.runnable.LocalSound;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.animation.ContextAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.common.SoundAttribute;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;

public class LocalSoundAttribute extends SoundAttribute implements ContextAnimationAttribute {

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

    public static final class Factory implements AttributeFactory<LocalSoundAttribute> {
        @Override
        public LocalSoundAttribute create(String key, ConfigurationSection config) {
            return new LocalSoundAttribute(key, new SoundAttribute.Factory().create(key, config).getValue());
        }
    }

}
