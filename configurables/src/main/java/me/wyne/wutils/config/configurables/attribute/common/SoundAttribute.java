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

import java.util.Locale;

public class SoundAttribute extends ConfigurableAttribute<Sound> {

    public SoundAttribute(String key, Sound value) {
        super(key, value);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().name().asString() + " " + getValue().volume() + " " + getValue().pitch() + " " + getValue().source().name()).buildNoSpace();
    }

    public static final class Factory implements CompositeAttributeFactory<SoundAttribute> {
        @Override
        public SoundAttribute fromSection(String key, ConfigurationSection section) {
            var soundKey = Preconditions.checkNotNull(section.getString("sound"), "No sound provided for " + section.getCurrentPath());
            var sound = ConfigUtils.getByKeyOrName(soundKey, org.bukkit.Sound.class);
            Preconditions.checkNotNull(sound, "Invalid sound at " + section.getCurrentPath());
            return new SoundAttribute(
                    key,
                    Sound.sound(Key.key(sound.getKey().toString()),
                            Sound.Source.valueOf(section.getString("source", "MASTER").toUpperCase(Locale.ENGLISH)),
                            (float) section.getDouble("volume", 1.0),
                            (float) section.getDouble("pitch", 1.0)
                    )
            );
        }

        @Override
        public SoundAttribute fromString(String key, String string, ConfigurationSection config) {
            var args = new Args(string);
            var soundKey = Preconditions.checkNotNull(args.get(0), "No sound provided for " + ConfigUtils.getPath(config, key));
            var sound = ConfigUtils.getByKeyOrName(soundKey, org.bukkit.Sound.class);
            Preconditions.checkNotNull(sound, "Invalid sound at " + ConfigUtils.getPath(config, key));
            return new SoundAttribute(
                    key,
                    Sound.sound(Key.key(sound.getKey().toString()),
                            Sound.Source.valueOf(args.get(3, "MASTER").toUpperCase(Locale.ENGLISH)),
                            Float.parseFloat(args.get(1, "1.0")),
                            Float.parseFloat(args.get(2, "1.0"))
                    )
            );
        }
    }

}
