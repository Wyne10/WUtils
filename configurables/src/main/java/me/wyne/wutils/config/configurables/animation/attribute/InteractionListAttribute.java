package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.InteractionListConfigurable;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.animation.ContextAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAttributeContext;
import org.bukkit.configuration.ConfigurationSection;

public class InteractionListAttribute extends ConfigurableAttribute<InteractionListConfigurable> implements ContextAnimationAttribute<AnimationContext> {

    public InteractionListAttribute(String key, InteractionListConfigurable value) {
        super(key, value);
    }

    public InteractionListAttribute(InteractionListConfigurable value) {
        super(AnimationAttribute.INTERACTIONS.getKey(), value);
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

    public static final class Factory implements AttributeFactory<InteractionListAttribute> {
        @Override
        public InteractionListAttribute create(String key, ConfigurationSection config) {
            var interactions = new InteractionListConfigurable();
            interactions.fromConfig(config.get(key));
            return new InteractionListAttribute(key, interactions);
        }
    }

}
