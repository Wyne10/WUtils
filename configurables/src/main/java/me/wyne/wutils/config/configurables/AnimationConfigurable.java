package me.wyne.wutils.config.configurables;

import me.wyne.wutils.animation.Animation;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.attribute.AttributeMap;
import me.wyne.wutils.config.configurables.attribute.ImmutableAttributeContainer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AnimationConfigurable implements CompositeConfigSerializable, ConfigDeserializable {

    private final List<AnimationStepConfigurable> animationSteps;
    private final AttributeMap attributeMap;

    public AnimationConfigurable() {
        this(AnimationStepConfigurable.ANIMATION_STEP_ATTRIBUTE_MAP);
    }

    public AnimationConfigurable(AnimationStepConfigurable... steps) {
        this();
        addSteps(steps);
    }

    public AnimationConfigurable(ConfigurationSection section) {
        this(AnimationStepConfigurable.ANIMATION_STEP_ATTRIBUTE_MAP, section);
    }

    public AnimationConfigurable(AttributeMap attributeMap) {
        this.animationSteps = new LinkedList<>();
        this.attributeMap = attributeMap;
    }

    public AnimationConfigurable(AttributeMap attributeMap, AnimationStepConfigurable... steps) {
        this(attributeMap);
        addSteps(steps);
    }

    public AnimationConfigurable(AttributeMap attributeMap, ConfigurationSection section) {
        this(attributeMap);
        fromConfig(section);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        ConfigBuilder builder = new ConfigBuilder();
        for (int i = 0; i < animationSteps.size(); i++) {
            builder.appendComposite(depth, "step-" + i, animationSteps.get(i), configEntry);
        }
        return builder.build();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        ConfigurationSection config = (ConfigurationSection) configObject;
        animationSteps.clear();
        config.getKeys(false).forEach(key -> {
            var repeat = config.getInt(key + ".repeat", 1);
            for (int i = 0; i < repeat; i++) {
                animationSteps.add(new AnimationStepConfigurable(
                        new ImmutableAttributeContainer(attributeMap, config.getConfigurationSection(key))));
            }
        });
    }

    public Animation build(Plugin plugin, AnimationContext context) {
        Animation animation = new Animation(plugin);
        for (AnimationStepConfigurable step : animationSteps) {
            animation.addStep(step.build(context));
        }
        return animation;
    }

    public AnimationConfigurable addStep(AnimationStepConfigurable step) {
        animationSteps.add(step);
        return this;
    }

    public AnimationConfigurable addSteps(AnimationStepConfigurable... steps) {
        animationSteps.addAll(Arrays.asList(steps));
        return this;
    }

    public List<AnimationStepConfigurable> getAnimationSteps() {
        return animationSteps;
    }

}
