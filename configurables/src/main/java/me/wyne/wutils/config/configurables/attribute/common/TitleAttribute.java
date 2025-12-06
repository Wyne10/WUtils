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

public class TitleAttribute extends ConfigurableAttribute<TitleAttribute.TitleData> {

    public TitleAttribute(String key, TitleData value) {
        super(key, value);
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
        public TitleAttribute fromSection(String key, ConfigurationSection section) {
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

        @Override
        public TitleAttribute fromString(String key, String string, ConfigurationSection config) {
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
