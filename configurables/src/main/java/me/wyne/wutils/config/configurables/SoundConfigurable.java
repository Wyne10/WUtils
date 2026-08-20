package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An Adventure {@link Sound}, read from a {@code sound}/{@code source}/{@code volume}/{@code pitch}
 * section.
 *
 * <p>Unlike {@link me.wyne.wutils.config.configurables.attribute.common.SoundAttribute}, which
 * resolves the sound name case-insensitively via {@code ConfigUtils.getByKeyOrName} and accepts
 * either a Bukkit enum name or a namespaced key, this class requires an exact namespaced key
 * ({@link Key#key}) and an exact-case {@link Sound.Source} name
 * ({@link Sound.Source#valueOf}). A plain enum name such as {@code BLOCK_ANVIL_USE} throws
 * {@code InvalidKeyException}, and a missing {@code sound} key throws {@link NullPointerException}.</p>
 */
public class SoundConfigurable implements CompositeConfigSerializable, ConfigDeserializable {

    @Nullable
    private Sound sound;

    public SoundConfigurable() {}

    public SoundConfigurable(@NotNull ConfigurationSection section) {
        fromConfig(section);
    }

    public SoundConfigurable(@Nullable Sound sound) {
        this.sound = sound;
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        if (sound == null)
            return "";
        ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.append(depth, "sound", sound.name().asString());
        configBuilder.append(depth, "source", sound.source());
        configBuilder.append(depth, "volume", sound.volume());
        configBuilder.append(depth, "pitch", sound.pitch());
        return configBuilder.build();
    }

    @SuppressWarnings({"PatternValidation", "DataFlowIssue"})
    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null) {
            sound = null;
            return;
        }
        ConfigurationSection config = (ConfigurationSection) configObject;
        sound = Sound.sound(Key.key(config.getString("sound")), Sound.Source.valueOf(config.getString("source", "MASTER")), (float) config.getDouble("volume", 1.0), (float) config.getDouble("pitch", 1.0));
    }

    @Nullable
    public Sound getSound() {
        return sound;
    }

}
