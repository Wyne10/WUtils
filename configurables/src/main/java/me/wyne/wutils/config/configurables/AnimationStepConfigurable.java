package me.wyne.wutils.config.configurables;

import me.wyne.wutils.animation.*;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.animation.ContextAnimationAttribute;
import me.wyne.wutils.config.configurables.animation.attribute.AnchorChargeAttribute;
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
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.DELAY.getKey(), new PrimitiveConfigurableAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.PERIOD.getKey(), new PrimitiveConfigurableAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.DURATION.getKey(), new PrimitiveConfigurableAttribute.Factory());
        ANIMATION_STEP_ATTRIBUTE_MAP.put(AnimationAttribute.ANCHOR_CHARGE.getKey(), new AnchorChargeAttribute.Factory());
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

    public AnimationStep buildBlocking(AnimationContext context) {
        int delay = attributeContainer.getValue(AnimationAttribute.DELAY.getKey(), 0);
        int period = attributeContainer.getValue(AnimationAttribute.PERIOD.getKey(), 0);
        int duration = attributeContainer.getValue(AnimationAttribute.DURATION.getKey(), 0);
        var attributes = attributeContainer.getSet(ContextAnimationAttribute.class);
        AnimationRunnable runnable;
        if (attributes.size() == 1)
            runnable = attributes.iterator().next().create(context);
        else
            runnable = new CompositeRunnable(attributes.stream().map(attribute -> attribute.create(context)).toList());
        return new BlockingAnimationStep(runnable, delay, period, duration);
    }

    public AnimationStep buildParallel(AnimationContext context) {
        int delay = attributeContainer.getValue(AnimationAttribute.DELAY.getKey(), 0);
        int period = attributeContainer.getValue(AnimationAttribute.PERIOD.getKey(), 0);
        int duration = attributeContainer.getValue(AnimationAttribute.DURATION.getKey(), 0);
        var attributes = attributeContainer.getSet(ContextAnimationAttribute.class);
        AnimationRunnable runnable;
        if (attributes.size() == 1)
            runnable = attributes.iterator().next().create(context);
        else
            runnable = new CompositeRunnable(attributes.stream().map(attribute -> attribute.create(context)).toList());
        return new ParallelAnimationStep(runnable, delay, period, duration);
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
    public <T> T get(String key) {
        return attributeContainer.get(key);
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
    public <V> Attribute<V> getAttribute(String key) {
        return attributeContainer.getAttribute(key);
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
    public <V> V getValue(String key) {
        return attributeContainer.getValue(key);
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
