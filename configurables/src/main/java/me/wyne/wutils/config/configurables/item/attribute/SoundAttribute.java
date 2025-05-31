package me.wyne.wutils.config.configurables.item.attribute;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.AttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.item.*;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SoundAttribute extends ConfigurableAttribute<Sound> implements ClickEventAttribute {

    public SoundAttribute(String key, Sound value) {
        super(key, value);
    }

    public SoundAttribute(Sound value) {
        super(ItemAttribute.SOUND.getKey(), value);
    }

    @Override
    public void apply(InventoryClickEvent event) {
        event.getWhoClicked().playSound(getValue());
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.append(depth, "sound", getValue().name().asString());
        configBuilder.append(depth, "source", getValue().source());
        configBuilder.append(depth, "volume", getValue().volume());
        configBuilder.append(depth, "pitch", getValue().pitch());
        return configBuilder.toString();
    }

    public static final class Factory implements AttributeFactory {
        @Override
        public SoundAttribute create(String key, ConfigurationSection config) {
            return new SoundAttribute(
                    key,
                    Sound.sound(Key.key(config.getString("sound")),
                            Sound.Source.valueOf(config.getString("source", "MASTER")),
                            (float) config.getDouble("volume", 1.0),
                            (float) config.getDouble("pitch", 1.0)
                    )
            );
        }
    }

}
