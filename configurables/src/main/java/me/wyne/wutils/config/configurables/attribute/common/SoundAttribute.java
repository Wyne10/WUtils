package me.wyne.wutils.config.configurables.attribute.common;

import com.google.common.base.Preconditions;
import me.wyne.wutils.common.Args;
import me.wyne.wutils.common.config.ConfigUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.CompositeAttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * An Adventure {@link Sound}, configurable either as a {@code sound}/{@code volume}/{@code pitch}/
 * {@code source} section or as a {@code "<sound> <volume> <pitch> <source>"} string, and rendered
 * back into generated config the same way.
 *
 * <p>Both forms resolve the sound name through {@link ConfigUtils#getByKeyOrName}, so a Bukkit enum
 * name (any case) or a namespaced key both work.</p>
 */
public class SoundAttribute extends ConfigurableAttribute<Sound> {

    public SoundAttribute(@NotNull String key, @NotNull Sound value) {
        super(key, value);
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().name().asString() + " " + getValue().volume() + " " + getValue().pitch() + " " + getValue().source().name()).buildNoSpace();
    }

    public static final class Factory implements CompositeAttributeFactory<SoundAttribute> {
        @Override
        public @NotNull SoundAttribute fromSection(@NotNull String key, @NotNull ConfigurationSection section) {
            var soundKey = Preconditions.checkNotNull(section.getString("sound"), "No sound provided for " + section.getCurrentPath());
            var sound = ConfigUtils.getByKeyOrName(soundKey, org.bukkit.Sound.class);
            Preconditions.checkNotNull(sound, "Invalid sound at " + section.getCurrentPath());
            return new SoundAttribute(
                    key,
                    Sound.sound(Key.key(sound.getKey().toString()),
                            ConfigUtils.getByName(section.getString("source", "MASTER"), Sound.Source.class),
                            (float) section.getDouble("volume", 1.0),
                            (float) section.getDouble("pitch", 1.0)
                    )
            );
        }

        /**
         * Parses {@code string} with {@link Args#SPACE_DELIMITER}, as
         * {@code "<sound> <volume> <pitch> <source>"} — volume, pitch and source are all optional.
         */
        @Override
        public @NotNull SoundAttribute fromString(@NotNull String key, @NotNull String string, @NotNull ConfigurationSection config) {
            var args = new Args(string, Args.SPACE_DELIMITER);
            var soundKey = Preconditions.checkNotNull(args.get(0), "No sound provided for " + ConfigUtils.getPath(config, key));
            var sound = ConfigUtils.getByKeyOrName(soundKey, org.bukkit.Sound.class);
            Preconditions.checkNotNull(sound, "Invalid sound at " + ConfigUtils.getPath(config, key));
            return new SoundAttribute(
                    key,
                    Sound.sound(Key.key(sound.getKey().toString()),
                            ConfigUtils.getByName(args.get(3, "MASTER"), Sound.Source.class),
                            Float.parseFloat(args.get(1, "1.0")),
                            Float.parseFloat(args.get(2, "1.0"))
                    )
            );
        }
    }

}
