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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A sequence of {@link AnimationStepConfigurable}s built into an {@link Animation}, in the order they
 * appear in config — unlike a single step's attributes, which apply in registration order, animation
 * steps run in file order.
 *
 * <p>{@link #fromConfig} reads a {@code repeat} integer per step section (default 1) and appends that
 * many identical steps for it; {@code repeat} is consumed here and never becomes an attribute of the
 * step itself.</p>
 */
public class AnimationConfigurable implements CompositeConfigSerializable, ConfigDeserializable {

    private final List<AnimationStepConfigurable> animationSteps;
    private final AttributeMap attributeMap;

    public AnimationConfigurable() {
        this(AnimationStepConfigurable.ANIMATION_STEP_ATTRIBUTE_MAP);
    }

    public AnimationConfigurable(@NotNull AnimationStepConfigurable... steps) {
        this();
        addSteps(steps);
    }

    public AnimationConfigurable(@NotNull ConfigurationSection section) {
        this(AnimationStepConfigurable.ANIMATION_STEP_ATTRIBUTE_MAP, section);
    }

    public AnimationConfigurable(@NotNull AttributeMap attributeMap) {
        this.animationSteps = new LinkedList<>();
        this.attributeMap = attributeMap;
    }

    public AnimationConfigurable(@NotNull AttributeMap attributeMap, @NotNull AnimationStepConfigurable... steps) {
        this(attributeMap);
        addSteps(steps);
    }

    public AnimationConfigurable(@NotNull AttributeMap attributeMap, @NotNull ConfigurationSection section) {
        this(attributeMap);
        fromConfig(section);
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
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

    public @NotNull Animation build(@NotNull Plugin plugin, @NotNull AnimationContext context) {
        Animation animation = new Animation(plugin);
        for (AnimationStepConfigurable step : animationSteps) {
            animation.addStep(step.build(context));
        }
        return animation;
    }

    public @NotNull AnimationConfigurable addStep(@NotNull AnimationStepConfigurable step) {
        animationSteps.add(step);
        return this;
    }

    public @NotNull AnimationConfigurable addSteps(@NotNull AnimationStepConfigurable... steps) {
        animationSteps.addAll(Arrays.asList(steps));
        return this;
    }

    public @NotNull List<@NotNull AnimationStepConfigurable> getAnimationSteps() {
        return animationSteps;
    }

}
