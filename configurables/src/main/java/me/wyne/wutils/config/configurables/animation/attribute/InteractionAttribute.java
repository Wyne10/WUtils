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
import org.jetbrains.annotations.NotNull;

/**
 * The {@code interaction} effect — embeds a nested interaction, sent to the context player when the
 * step runs. Needs a player; returns {@link AnimationRunnable#EMPTY} when the context has none.
 *
 * <p>The placeholder target passed to the nested interaction is the context player itself, so its
 * audience and payload attributes resolve language and placeholders from that player.</p>
 */
public class InteractionAttribute extends ConfigurableAttribute<InteractionConfigurable> implements ContextAnimationAttribute<AnimationContext> {

    public InteractionAttribute(@NotNull String key, @NotNull InteractionConfigurable value) {
        super(key, value);
    }

    public InteractionAttribute(@NotNull InteractionConfigurable value) {
        super(AnimationAttribute.INTERACTION.getKey(), value);
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

    public static final class Factory implements AttributeFactory<InteractionAttribute> {
        @Override
        public @NotNull InteractionAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new InteractionAttribute(key, new InteractionConfigurable(config.getConfigurationSection(key)));
        }
    }

}
