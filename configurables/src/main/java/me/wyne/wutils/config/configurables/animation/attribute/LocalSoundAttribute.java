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
import org.jetbrains.annotations.NotNull;

/**
 * The {@code localSound} effect — plays a sound at the context location. Needs a location; returns
 * {@link AnimationRunnable#EMPTY} when the context has none.
 *
 * <p>A thin subclass of the shared {@link me.wyne.wutils.config.configurables.attribute.common.SoundAttribute},
 * so it accepts both the string and section config forms. Its own factory is a plain {@code AttributeFactory}
 * that merely delegates to {@link SoundAttribute.Factory}, a composite one — because the delegate
 * dispatches through {@code CompositeAttributeFactory}, this attribute can still be aliased with
 * {@code attributeType}.</p>
 */
public class LocalSoundAttribute extends SoundAttribute implements ContextAnimationAttribute<AnimationContext> {

    public LocalSoundAttribute(@NotNull String key, @NotNull Sound value) {
        super(key, value);
    }

    public LocalSoundAttribute(@NotNull Sound value) {
        super(AnimationAttribute.LOCAL_SOUND.getKey(), value);
    }

    @Override
    public @NotNull AnimationRunnable create(@NotNull AnimationContext context) {
        if (context.getLocation() == null) return AnimationRunnable.EMPTY;
        return new LocalSound(context.getLocation(), getValue());
    }

    public static final class Factory implements AttributeFactory<LocalSoundAttribute> {
        @Override
        public @NotNull LocalSoundAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new LocalSoundAttribute(key, new SoundAttribute.Factory().create(key, config).getValue());
        }
    }

}
