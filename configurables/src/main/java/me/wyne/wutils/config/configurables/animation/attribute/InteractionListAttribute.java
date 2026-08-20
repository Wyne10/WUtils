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
import org.jetbrains.annotations.NotNull;

/**
 * The {@code interactions} effect — embeds a nested interaction list, sent in order to the context
 * player when the step runs. Needs a player; returns {@link AnimationRunnable#EMPTY} when the
 * context has none.
 *
 * <p>The placeholder target passed to each nested interaction is the context player itself, so their
 * audience and payload attributes resolve language and placeholders from that player.</p>
 */
public class InteractionListAttribute extends ConfigurableAttribute<InteractionListConfigurable> implements ContextAnimationAttribute<AnimationContext> {

    public InteractionListAttribute(@NotNull String key, @NotNull InteractionListConfigurable value) {
        super(key, value);
    }

    public InteractionListAttribute(@NotNull InteractionListConfigurable value) {
        super(AnimationAttribute.INTERACTIONS.getKey(), value);
    }

    @Override
    public @NotNull AnimationRunnable create(@NotNull AnimationContext context) {
        if (context.getPlayer() == null) return AnimationRunnable.EMPTY;
        var player = context.getPlayer();
        var interactionContext = new InteractionAttributeContext(player, context.getTextReplacements(), context.getComponentReplacements());
        return () -> getValue().send(player, interactionContext);
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder()
                .appendComposite(depth, getKey(), getValue(), configEntry)
                .buildNoSpace();
    }

    public static final class Factory implements AttributeFactory<InteractionListAttribute> {
        @Override
        public @NotNull InteractionListAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            var interactions = new InteractionListConfigurable();
            interactions.fromConfig(config.get(key));
            return new InteractionListAttribute(key, interactions);
        }
    }

}
