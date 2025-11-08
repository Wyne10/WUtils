package me.wyne.wutils.config.configurables;

import me.wyne.wutils.animation.Animation;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AnimationConfigurable implements CompositeConfigurable {

    private final List<AnimationStepConfigurable> animationSteps;

    public AnimationConfigurable() {
        animationSteps = new LinkedList<>();
    }

    public AnimationConfigurable(ConfigurationSection section) {
        this();
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
        ConfigurationSection config = (ConfigurationSection) configObject;
        animationSteps.clear();
        config.getKeys(false).forEach(key ->
                animationSteps.add(new AnimationStepConfigurable(config.getConfigurationSection(key))));
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
