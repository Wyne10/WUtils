package me.wyne.wutils.config.configurables.attribute.common;

import me.wyne.wutils.common.Args;
import me.wyne.wutils.common.Ticks;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.common.duration.Durations;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.CompositeAttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import net.kyori.adventure.title.Title;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * An Adventure {@link Title}, configurable either as a {@code title}/{@code subtitle}/{@code fadeIn}/
 * {@code stay}/{@code fadeOut} section or as a
 * {@code "<title> <subtitle> <fadeIn> <stay> <fadeOut>"} string, and rendered back the same way.
 * Fade/stay times are ticks.
 */
public class TitleAttribute extends ConfigurableAttribute<TitleAttribute.TitleData> {

    public TitleAttribute(@NotNull String key, @NotNull TitleData value) {
        super(key, value);
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(),
                getValue().title() + " " +
                        getValue().subtitle() + " " +
                        Ticks.ofMillis(getValue().times.fadeIn().toMillis()) + " " +
                        Ticks.ofMillis(getValue().times.stay().toMillis()) + " " +
                        Ticks.ofMillis(getValue().times.fadeOut().toMillis())
        ).buildNoSpace();
    }

    public record TitleData(@NotNull String title, @NotNull String subtitle, @NotNull Title.Times times) {
        public static TitleData of(@NotNull String title) {
            return new TitleData(title, "", Title.Times.of(Ticks.duration(20), Ticks.duration(60), Ticks.duration(20)));
        }

        public static TitleData of(@NotNull String title, @NotNull String subtitle) {
            return new TitleData(title, subtitle, Title.Times.of(Ticks.duration(20), Ticks.duration(60), Ticks.duration(20)));
        }

        public static TitleData of(@NotNull String title, @NotNull String subtitle, long fadeIn, long stay, long fadeOut) {
            return new TitleData(title, subtitle, Title.Times.of(Ticks.duration(fadeIn), Ticks.duration(stay), Ticks.duration(fadeOut)));
        }
    }

    public static final class Factory implements CompositeAttributeFactory<TitleAttribute> {
        @Override
        public @NotNull TitleAttribute fromSection(@NotNull String key, @NotNull ConfigurationSection section) {
            return new TitleAttribute(
                    key,
                    new TitleData(
                            section.getString("title", ""),
                            section.getString("subtitle", ""),
                            Title.Times.of(
                                    Ticks.duration(ConfigUtils.getTicks(section, "fadeIn", 20)),
                                    Ticks.duration(ConfigUtils.getTicks(section, "stay", 60)),
                                    Ticks.duration(ConfigUtils.getTicks(section, "fadeOut", 20))
                            )
                    )
            );
        }

        /**
         * Parses {@code string} with {@link Args}'s default colon-or-whitespace delimiter, as
         * {@code "<title>:<subtitle>:<fadeIn>:<stay>:<fadeOut>"} (or space-separated). A colon inside
         * the title or subtitle text is therefore indistinguishable from a field separator.
         */
        @Override
        public @NotNull TitleAttribute fromString(@NotNull String key, @NotNull String string, @NotNull ConfigurationSection config) {
            var args = new Args(string);
            return new TitleAttribute(
                    key,
                    new TitleData(
                            args.get(0),
                            args.get(1),
                            Title.Times.of(
                                    Ticks.duration(Durations.getTicks(args.get(2, "20"))),
                                    Ticks.duration(Durations.getTicks(args.get(3, "60"))),
                                    Ticks.duration(Durations.getTicks(args.get(4, "20")))
                            )
                    )
            );
        }
    }

}
