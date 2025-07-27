package me.wyne.wutils.config.configurables.animation.attribute;

import me.wyne.wutils.animation.AnimationRunnable;
import me.wyne.wutils.animation.runnable.TitleEffect;
import me.wyne.wutils.common.Args;
import me.wyne.wutils.common.Ticks;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.animation.AnimationAttribute;
import me.wyne.wutils.config.configurables.animation.AnimationContext;
import me.wyne.wutils.config.configurables.animation.ContextAnimationAttribute;
import me.wyne.wutils.config.configurables.attribute.CompositeAttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.title.Title;
import org.bukkit.configuration.ConfigurationSection;

public class PlayerTitleAttribute extends ConfigurableAttribute<PlayerTitleAttribute.TitleData> implements ContextAnimationAttribute {

    public PlayerTitleAttribute(String key, TitleData value) {
        super(key, value);
    }

    public PlayerTitleAttribute(TitleData value) {
        super(AnimationAttribute.PLAYER_TITLE.getKey(), value);
    }

    @Override
    public AnimationRunnable create(AnimationContext context) {
        if (context.getPlayer() == null) return AnimationRunnable.Companion.getEMPTY();
        return new TitleEffect(I18n.global.audiences.player(context.getPlayer()), Title.title(
                I18n.global.getPlaceholderComponent(I18n.toLocale(context.getPlayer()), context.getPlayer(), getValue().title, context.getTextReplacements()).replace(context.getComponentReplacements()).get(),
                I18n.global.getPlaceholderComponent(I18n.toLocale(context.getPlayer()), context.getPlayer(), getValue().subtitle, context.getTextReplacements()).replace(context.getComponentReplacements()).get(),
                getValue().times
        ));
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(),
                getValue().title() + " " +
                        getValue().subtitle() + " " +
                        Ticks.ofMillis(getValue().times.fadeIn().toMillis()) + " " +
                        Ticks.ofMillis(getValue().times.stay().toMillis()) + " " +
                        Ticks.ofMillis(getValue().times.fadeOut().toMillis())
        ).buildNoSpace();
    }

    public record TitleData(String title, String subtitle, Title.Times times) {}

    public static final class Factory implements CompositeAttributeFactory {
        @Override
        public PlayerTitleAttribute fromSection(String key, ConfigurationSection section) {
            return new PlayerTitleAttribute(
                    key,
                    new TitleData(
                            section.getString("title", ""),
                            section.getString("subtitle", ""),
                            Title.Times.of(
                                    Ticks.duration(section.getInt("fadeIn", 20)),
                                    Ticks.duration(section.getInt("stay", 60)),
                                    Ticks.duration(section.getInt("fadeOut", 20))
                            )
                    )
            );
        }

        @Override
        public PlayerTitleAttribute fromString(String key, String string) {
            var args = new Args(string, " ");
            return new PlayerTitleAttribute(
                    key,
                    new TitleData(
                            args.get(0),
                            args.get(1),
                            Title.Times.of(
                                    Ticks.duration(Integer.parseInt(args.get(2, "20"))),
                                    Ticks.duration(Integer.parseInt(args.get(3, "60"))),
                                    Ticks.duration(Integer.parseInt(args.get(4, "20")))
                            )
                    )
            );
        }
    }

}
