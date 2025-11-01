package me.wyne.wutils.config.configurables.interaction.attribute;

import me.wyne.wutils.common.Args;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import me.wyne.wutils.config.configurables.attribute.CompositeAttributeFactory;
import me.wyne.wutils.config.configurables.attribute.ConfigurableAttribute;
import me.wyne.wutils.config.configurables.interaction.ContextInteractionAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAttribute;
import me.wyne.wutils.config.configurables.interaction.InteractionAttributeContext;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class SoundAttribute extends ConfigurableAttribute<Sound> implements ContextInteractionAttribute {

    public SoundAttribute(String key, Sound value) {
        super(key, value);
    }

    public SoundAttribute(Sound value) {
        super(InteractionAttribute.SOUND.getKey(), value);
    }

    @Override
    public void send(Audience audience, CommandSender sender, InteractionAttributeContext context) {
        audience.playSound(getValue());
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder().append(depth, getKey(), getValue().name().asString() + " " + getValue().volume() + " " + getValue().pitch() + " " + getValue().source().name()).buildNoSpace();
    }

    public static final class Factory implements CompositeAttributeFactory {
        @Override
        public SoundAttribute fromSection(String key, ConfigurationSection section) {
            return new SoundAttribute(
                    key,
                    Sound.sound(Key.key(section.getString("sound", "entity.item.pickup")),
                            Sound.Source.valueOf(section.getString("source", "MASTER")),
                            (float) section.getDouble("volume", 1.0),
                            (float) section.getDouble("pitch", 1.0)
                    )
            );
        }

        @Override
        public SoundAttribute fromString(String key, String string) {
            var args = new Args(string, " ");
            return new SoundAttribute(
                    key,
                    Sound.sound(Key.key(args.get(0, "entity.item.pickup")),
                            Sound.Source.valueOf(args.get(3, "MASTER")),
                            Float.parseFloat(args.get(1, "1.0")),
                            Float.parseFloat(args.get(2, "1.0"))
                    )
            );
        }
    }

}
