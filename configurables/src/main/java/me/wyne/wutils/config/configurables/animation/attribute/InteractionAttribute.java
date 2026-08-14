package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.InteractionConfigurable;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.animation.ContextAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAttributeContext;
import org.bukkit.configuration.ConfigurationSection;

public class InteractionAttribute extends ConfigurableAttribute<InteractionConfigurable> implements ContextAnimationAttribute<AnimationContext> {

    public InteractionAttribute(String key, InteractionConfigurable value) {
        super(key, value);
    }

    public InteractionAttribute(InteractionConfigurable value) {
        super(AnimationAttribute.INTERACTION.getKey(), value);
    }

    @Override
    public AnimationRunnable create(AnimationContext context) {
        if (context.getPlayer() == null) return AnimationRunnable.EMPTY;
        var player = context.getPlayer();
        var interactionContext = new InteractionAttributeContext(player, context.getTextReplacements(), context.getComponentReplacements());
        return () -> getValue().send(player, interactionContext);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder()
                .appendComposite(depth, getKey(), getValue(), configEntry)
                .buildNoSpace();
    }

    public static final class Factory implements AttributeFactory<InteractionAttribute> {
        @Override
        public InteractionAttribute create(String key, ConfigurationSection config) {
            return new InteractionAttribute(key, new InteractionConfigurable(config.getConfigurationSection(key)));
        }
    }

}
