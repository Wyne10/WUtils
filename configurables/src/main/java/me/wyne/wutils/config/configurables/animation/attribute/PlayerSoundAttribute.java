package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.runnable.SoundEffect;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.animation.ContextAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.common.SoundAttribute;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.ConfigurationSection;

public class PlayerSoundAttribute extends SoundAttribute implements ContextAnimationAttribute<AnimationContext> {

    public PlayerSoundAttribute(String key, Sound value) {
        super(key, value);
    }

    public PlayerSoundAttribute(Sound value) {
        super(AnimationAttribute.LOCAL_SOUND.getKey(), value);
    }

    @Override
    public AnimationRunnable create(AnimationContext context) {
        if (context.getLocation() == null) return AnimationRunnable.EMPTY;
        return new SoundEffect(
                I18n.global.getAudiences().player(context.getPlayer()),
                getValue()
        );
    }

    public static final class Factory implements AttributeFactory<PlayerSoundAttribute> {
        @Override
        public PlayerSoundAttribute create(String key, ConfigurationSection config) {
            return new PlayerSoundAttribute(key, new SoundAttribute.Factory().create(key, config).getValue());
        }
    }

}
