package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigurable;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.ConfigurationSection;

public class SoundConfigurable implements CompositeConfigurable {

    private Sound sound;

    public SoundConfigurable(Sound sound) {
        this.sound = sound;
    }

    public SoundConfigurable(Object configObject) {
        fromConfig(configObject);
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.append(depth, "sound", sound.name().asString());
        configBuilder.append(depth, "source", sound.source());
        configBuilder.append(depth, "volume", sound.volume());
        configBuilder.append(depth, "pitch", sound.pitch());
        return configBuilder.build();
    }

    @SuppressWarnings({"PatternValidation", "DataFlowIssue"})
    @Override
    public void fromConfig(Object configObject) {
        ConfigurationSection config = (ConfigurationSection) configObject;
        sound = Sound.sound(Key.key(config.getString("sound")), Sound.Source.valueOf(config.getString("source", "MASTER")), (float)config.getDouble("volume", 1.0), (float)config.getDouble("pitch", 1.0));
    }

    public Sound getSound() {
        return sound;
    }

}
