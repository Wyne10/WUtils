package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.runnable.TitleEffect;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.animation.ContextAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.common.TitleAttribute;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.title.Title;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code playerTitle} effect — shows a title/subtitle to the context player. Needs a player;
 * returns {@link AnimationRunnable#EMPTY} when the context has none.
 *
 * <p>A thin subclass of the shared {@link me.wyne.wutils.config.configurables.attribute.common.TitleAttribute},
 * so it accepts both the string and section config forms and, because its factory dispatches through
 * a {@code CompositeAttributeFactory}, can be aliased with {@code attributeType}.</p>
 */
public class PlayerTitleAttribute extends TitleAttribute implements ContextAnimationAttribute<AnimationContext> {

    public PlayerTitleAttribute(@NotNull String key, @NotNull TitleData value) {
        super(key, value);
    }

    public PlayerTitleAttribute(@NotNull TitleData value) {
        super(AnimationAttribute.PLAYER_TITLE.getKey(), value);
    }

    @Override
    public @NotNull AnimationRunnable create(@NotNull AnimationContext context) {
        if (context.getPlayer() == null) return AnimationRunnable.EMPTY;
        return new TitleEffect(I18n.global.getAudiences().player(context.getPlayer()), Title.title(
                I18n.global.accessor(context.getPlayer(), getValue().title()).getPlaceholderComponent(context.getPlayer(), context.getTextReplacements()).replace(context.getComponentReplacements()).get(),
                I18n.global.accessor(context.getPlayer(), getValue().subtitle()).getPlaceholderComponent(context.getPlayer(), context.getTextReplacements()).replace(context.getComponentReplacements()).get(),
                getValue().times()
        ));
    }

    public static final class Factory implements AttributeFactory<PlayerTitleAttribute> {
        @Override
        public @NotNull PlayerTitleAttribute create(@NotNull String key, @NotNull ConfigurationSection config) {
            return new PlayerTitleAttribute(key, new TitleAttribute.Factory().create(key, config).getValue());
        }
    }

}
