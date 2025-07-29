package me.wyne.wutils.config.configurables;

import me.wyne.wutils.animation.*;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurables.animation.*;
import me.wyne.wutils.config.configurables.animation.attribute.*;
import me.wyne.wutils.config.configurables.attribute.*;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class AnimationStepConfigurable implements CompositeConfigurable {

    public final static AttributeMap ANIMATION_STEP_ATTRIBUTE_MAP = new AttributeMap(new LinkedHashMap<>());

    static {
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.TYPE.getKey(), new AnimationTypeAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.DELAY.getKey(), new AnimationDelayAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.PERIOD.getKey(), new AnimationPeriodAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.DURATION.getKey(), new AnimationDurationAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.ANCHOR_CHARGE.getKey(), new AnchorChargeAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.FORCE_FIELD.getKey(), new ForceFieldAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.PLAYER_TITLE.getKey(), new PlayerTitleAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.LOCAL_SOUND.getKey(), new LocalSoundAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.WORLD_PARTICLE.getKey(), new WorldParticleAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.FIREWORK.getKey(), new FireworkAttribute.Factory());
    }

    private final AttributeContainer attributeContainer;

    public AnimationStepConfigurable() {
        attributeContainer = new ImmutableAttributeContainer(ANIMATION_STEP_ATTRIBUTE_MAP, new LinkedHashMap<>());
    }

    public AnimationStepConfigurable(ConfigurationSection section) {
        this();
        fromConfig(section);
    }

    public AnimationStepConfigurable(AttributeContainer attributeContainer) {
        this.attributeContainer = attributeContainer;
    }

    public AnimationStepConfigurable(AttributeContainer attributeContainer, ConfigurationSection section) {
        this(attributeContainer);
        fromConfig(section);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return attributeContainer.toConfig(depth, configEntry);
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        attributeContainer.fromConfig(configObject);
    }

    public AnimationStep build(AnimationTypeAttribute.AnimationType type, AnimationContext context) {
        var timing = new AnimationTimings(0, 0, 0);
        attributeContainer.getSet(TimingsAnimationAttribute.class)
                .forEach(attribute -> attribute.apply(timing));
        var attributes = attributeContainer.getSet(ContextAnimationAttribute.class);
        AnimationRunnable runnable;
        if (attributes.size() == 1)
            runnable = attributes.iterator().next().create(context);
        else
            runnable = new CompositeRunnable(attributes.stream().map(attribute -> attribute.create(context)).toList());
        return type.create(runnable, timing.delay, timing.period, timing.duration);
    }

    public AnimationStep build(AnimationContext context) {
        var type = attributeContainer.getValue(AnimationAttribute.TYPE.getKey(), AnimationTypeAttribute.AnimationType.BLOCKING);
        return build(type, context);
    }

    public AnimationStep buildBlocking(AnimationContext context) {
        return build(AnimationTypeAttribute.AnimationType.BLOCKING, context);
    }

    public AnimationStep buildParallel(AnimationContext context) {
        return build(AnimationTypeAttribute.AnimationType.PARALLEL, context);
    }

    public AnimationStepConfigurable ignore(AnimationAttribute... ignore) {
        return new AnimationStepConfigurable(attributeContainer.ignore(Arrays.stream(ignore).map(AnimationAttribute::getKey).toArray(String[]::new)));
    }

    public AnimationStepConfigurable ignore(String... ignore) {
        return new AnimationStepConfigurable(attributeContainer.ignore(ignore));
    }

    public AnimationStepConfigurable with(String key, AttributeFactory factory) {
        return new AnimationStepConfigurable(attributeContainer.with(key, factory));
    }

    public AnimationStepConfigurable with(Map<String, AttributeFactory> keyMap) {
        return new AnimationStepConfigurable(attributeContainer.with(keyMap));
    }

    public AnimationStepConfigurable with(Attribute<?> attribute) {
        return new AnimationStepConfigurable(attributeContainer.with(attribute));
    }

    public AnimationStepConfigurable with(AttributeContainer container) {
        return new AnimationStepConfigurable(attributeContainer.with(container));
    }

    public AnimationStepConfigurable with(AnimationStepConfigurable animationStepConfigurable) {
        return new AnimationStepConfigurable(attributeContainer.with(animationStepConfigurable.getAttributeContainer()));
    }

    public AnimationStepConfigurable copy(AttributeContainer container) {
        return new AnimationStepConfigurable(attributeContainer.copy(container));
    }

    public AnimationStepConfigurable copy(AnimationStepConfigurable animationStepConfigurable) {
        return new AnimationStepConfigurable(attributeContainer.copy(animationStepConfigurable.getAttributeContainer()));
    }

    public AnimationStepConfigurable copy() {
        return new AnimationStepConfigurable(attributeContainer.copy());
    }

    @Nullable
    public <T> T get(Class<T> clazz) {
        return attributeContainer.get(clazz);
    }

    @Nullable
    public <T> T get(String key) {
        return attributeContainer.get(key);
    }

    @Nullable
    public <T> T get(Class<T> clazz, T def) {
        return attributeContainer.get(clazz, def);
    }

    public <T> T get(String key, T def) {
        return attributeContainer.get(key, def);
    }

    @Nullable
    public <T> T get(AnimationAttribute attribute) {
        return attributeContainer.get(attribute.getKey());
    }

    public <T> T get(AnimationAttribute attribute, T def) {
        return attributeContainer.get(attribute.getKey(), def);
    }

    public <T> Set<T> getSet(Class<T> clazz) {
        return attributeContainer.getSet(clazz);
    }

    @Nullable
    public <T, V> Attribute<V> getAttribute(Class<T> clazz) {
        return attributeContainer.getAttribute(clazz);
    }

    @Nullable
    public <V> Attribute<V> getAttribute(String key) {
        return attributeContainer.getAttribute(key);
    }

    @Nullable
    public <T, V> Attribute<V> getAttribute(Class<T> clazz, Attribute<V> def) {
        return attributeContainer.getAttribute(clazz, def);
    }

    public <V> Attribute<V> getAttribute(String key, Attribute<V> def) {
        return attributeContainer.getAttribute(key, def);
    }

    @Nullable
    public <V> Attribute<V> getAttribute(AnimationAttribute attribute) {
        return attributeContainer.getAttribute(attribute.getKey());
    }

    public <V> Attribute<V> getAttribute(AnimationAttribute attribute, Attribute<V> def) {
        return attributeContainer.getAttribute(attribute.getKey(), def);
    }

    public <V> Set<Attribute<V>> getAttributes(Class<Attribute<V>> clazz) {
        return attributeContainer.getAttributes(clazz);
    }

    @Nullable
    public <T, V> V getValue(Class<T> clazz) {
        return attributeContainer.getValue(clazz);
    }

    @Nullable
    public <V> V getValue(String key) {
        return attributeContainer.getValue(key);
    }

    @Nullable
    public <T, V> V getValue(Class<T> clazz, V def) {
        return attributeContainer.getValue(clazz, def);
    }

    public <V> V getValue(String key, V def) {
        return attributeContainer.getValue(key, def);
    }

    @Nullable
    public <V> V getValue(AnimationAttribute attribute) {
        return attributeContainer.getValue(attribute.getKey());
    }

    public <V> V getValue(AnimationAttribute attribute, V def) {
        return attributeContainer.getValue(attribute.getKey(), def);
    }

    public <V> Set<V> getValues(Class<V> clazz) {
        return attributeContainer.getValues(clazz);
    }

    public Map<String, Attribute<?>> getAttributes() {
        return attributeContainer.getAttributes();
    }

    public AttributeMap getAttributeMap() {
        return attributeContainer.getAttributeMap();
    }

    public AttributeContainer getAttributeContainer() {
        return attributeContainer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder {

        protected final AttributeContainerBuilder attributeContainerBuilder;

        public Builder() {
            this.attributeContainerBuilder = new ImmutableAttributeContainer(ANIMATION_STEP_ATTRIBUTE_MAP, new LinkedHashMap<>()).toBuilder();
        }

        public Builder(AttributeContainer attributeContainer) {
            this.attributeContainerBuilder = attributeContainer.toBuilder();
        }

        public Builder(AnimationStepConfigurable animationStepConfigurable) {
            this.attributeContainerBuilder = animationStepConfigurable.getAttributeContainer().toBuilder();
        }

        public Builder ignore(AnimationAttribute... ignore) {
            for (AnimationAttribute ignoreAttribute : ignore)
                attributeContainerBuilder.ignore(ignoreAttribute.getKey());
            return this;
        }

        public Builder ignore(String... ignore) {
            attributeContainerBuilder.ignore(ignore);
            return this;
        }

        public Builder with(String key, AttributeFactory factory) {
            attributeContainerBuilder.with(key, factory);
            return this;
        }

        public Builder with(Map<String, AttributeFactory> keyMap) {
            attributeContainerBuilder.with(keyMap);
            return this;
        }

        public Builder with(Attribute<?> attribute) {
            attributeContainerBuilder.with(attribute);
            return this;
        }

        public Builder with(AttributeContainer container) {
            attributeContainerBuilder.with(container);
            return this;
        }

        public Builder copy(AttributeContainer container) {
            attributeContainerBuilder.copy(container);
            return this;
        }

        public Builder copy(AnimationStepConfigurable animationStepConfigurable) {
            attributeContainerBuilder.copy(animationStepConfigurable.getAttributeContainer());
            return this;
        }

        public AnimationStepConfigurable build() {
            return new AnimationStepConfigurable(attributeContainerBuilder.buildImmutable());
        }

    }

}
