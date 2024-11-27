package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurable.Configurable;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.ConfigurationSection;

public class SoundConfigurable implements Configurable {

    private Sound sound;

    public SoundConfigurable(Sound sound) {
        this.sound = sound;
    }

    @Override
    public String toConfig(ConfigEntry configEntry) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.append(1, "sound", sound.name().asString());
        configBuilder.append(1, "source", sound.source());
        configBuilder.append(1, "volume", sound.volume());
        configBuilder.append(1, "pitch", sound.pitch());
        return configBuilder.build();
    }

    @Override
    public void fromConfig(Object configObject) {
        ConfigurationSection config = (ConfigurationSection) configObject;
        sound = Sound.sound(Key.key(config.getString("sound")), Sound.Source.valueOf(config.getString("source", "MASTER")), (float)config.getDouble("volume"), (float)config.getDouble("pitch"));
    }

    public Sound getSound() {
        return sound;
    }

}