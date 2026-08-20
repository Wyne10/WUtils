package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.runnable.MessageEffect;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.animation.ContextAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The {@code globalMessage} effect — sends the configured lines to every online player.
 *
 * <p>The audience is always {@code audiences.players()}, but the effect still needs a context
 * player: {@link #create} returns {@link AnimationRunnable#EMPTY} when the context player is null,
 * because the player is also used to resolve the message's language and placeholders. The player
 * therefore gates whether anyone receives the broadcast, even though the broadcast itself goes to
 * everyone.</p>
 */
public class GlobalMessageAttribute extends ConfigurableAttribute<List<String>> implements ContextAnimationAttribute<AnimationContext> {

    public GlobalMessageAttribute(@NotNull String key, @NotNull List<@NotNull String> value) {
        super(key, value);
    }

    public GlobalMessageAttribute(@NotNull List<@NotNull String> value) {
        super(AnimationAttribute.GLOBAL_MESSAGE.getKey(), value);
    }

    @Override
    public @NotNull AnimationRunnable create(@NotNull AnimationContext context) {
        if (context.getPlayer() == null) return AnimationRunnable.EMPTY;
        return new MessageEffect(
                I18n.global.getAudiences().players(),
                getValue().stream()
                        .map(s -> I18n.global.accessor(context.getPlayer(), s).getPlaceholderComponent(context.getPlayer(), context.getTextReplacements()).replace(context.getComponentReplacements()).get())
                        .reduce(I18n::reduceRawComponent).orElse(Component.empty())
        );
    }

    public static final class Factory implements AttributeFactory<GlobalMessageAttribute> {
        @Override
        public @NotNull GlobalMessageAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new GlobalMessageAttribute(key, ConfigUtils.getStringList(config, key));
        }
    }

}
