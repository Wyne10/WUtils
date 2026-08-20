package me.wyne.wutils.config.configurables;

import me.wyne.wutils.animation.*;
import me.wyne.wutils.config.configurables.animation.*;
import me.wyne.wutils.config.configurables.animation.attribute.*;
import me.wyne.wutils.config.configurables.attribute.*;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * A single step of an {@link AnimationConfigurable} — its timing ({@code delay}/{@code period}/
 * {@code duration}, applied via {@link TimingsAnimationAttribute}) and its effects (particles,
 * sounds, titles, ..., via {@link ContextAnimationAttribute}).
 *
 * <p>{@link #build(AnimationTypeAttribute.AnimationType, AnimationContext)} wraps the step's effect
 * attributes in a {@link CompositeRunnable} whenever there is not exactly one — including zero, which
 * yields a {@code CompositeRunnable} over an empty list rather than failing.</p>
 */
public class AnimationStepConfigurable extends AttributeConfigurable {

    public final static AttributeMap ANIMATION_STEP_ATTRIBUTE_MAP = new AttributeMap();

    static {
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.TYPE.getKey(), new AnimationTypeAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.DELAY.getKey(), new AnimationDelayAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.PERIOD.getKey(), new AnimationPeriodAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.DURATION.getKey(), new AnimationDurationAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.ANCHOR_CHARGE.getKey(), new AnchorChargeAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.FORCE_FIELD.getKey(), new ForceFieldAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.PLAYER_TITLE.getKey(), new PlayerTitleAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.LOCAL_SOUND.getKey(), new LocalSoundAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.PLAYER_SOUND.getKey(), new PlayerSoundAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.WORLD_PARTICLE.getKey(), new WorldParticleAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.FIREWORK.getKey(), new FireworkAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.PLAYER_MESSAGE.getKey(), new PlayerMessageAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.GLOBAL_MESSAGE.getKey(), new GlobalMessageAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.INTERACTION.getKey(), new InteractionAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.INTERACTIONS.getKey(), new InteractionListAttribute.Factory());
    }

    public AnimationStepConfigurable() {
        super(new ImmutableAttributeContainer(ANIMATION_STEP_ATTRIBUTE_MAP));
    }

    public AnimationStepConfigurable(@NotNull ConfigurationSection section) {
        super(new ImmutableAttributeContainer(ANIMATION_STEP_ATTRIBUTE_MAP), section);
    }

    public AnimationStepConfigurable(@NotNull AttributeContainer attributeContainer) {
        super(attributeContainer);
    }

    public AnimationStepConfigurable(@NotNull AttributeContainer attributeContainer, @NotNull ConfigurationSection section) {
        super(attributeContainer, section);
    }

    public @NotNull AnimationStep build(@NotNull AnimationTypeAttribute.AnimationType type, @NotNull AnimationContext context) {
        var timing = new AnimationTimings(0, 0, 0);
        getAttributeContainer().getSet(TimingsAnimationAttribute.class)
                .forEach(attribute -> attribute.apply(timing));
        var attributes = getAttributeContainer().getSet(ContextAnimationAttribute.class);
        AnimationRunnable runnable;
        if (attributes.size() == 1)
            runnable = attributes.iterator().next().create(context);
        else
            runnable = new CompositeRunnable(attributes.stream().map(attribute -> attribute.create(context)).toList());
        return type.create(runnable, timing.delay, timing.period, timing.duration);
    }

    public @NotNull AnimationStep build(@NotNull AnimationContext context) {
        var type = getAttributeContainer().getValue(AnimationAttribute.TYPE.getKey(), AnimationTypeAttribute.AnimationType.BLOCKING);
        return build(type, context);
    }

    public @NotNull AnimationStep buildBlocking(@NotNull AnimationContext context) {
        return build(AnimationTypeAttribute.AnimationType.BLOCKING, context);
    }

    public @NotNull AnimationStep buildParallel(@NotNull AnimationContext context) {
        return build(AnimationTypeAttribute.AnimationType.PARALLEL, context);
    }

    public static @NotNull AttributeContainerBuilder builder() {
        return new AnimationStepConfigurable().getAttributeContainer().toBuilder();
    }

}
