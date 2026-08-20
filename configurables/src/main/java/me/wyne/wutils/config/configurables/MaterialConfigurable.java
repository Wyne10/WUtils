package me.wyne.wutils.config.configurables;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.CompositeConfigSerializable;
import me.wyne.wutils.config.configurable.ConfigDeserializable;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link Material}, read from a Bukkit material name via {@link Material#matchMaterial}.
 *
 * <p>{@link #fromConfig} stores whatever {@link Material#matchMaterial} returns without checking it,
 * and that method returns {@code null} for an unrecognised name — so an invalid config value silently
 * replaces the default {@code STONE} with {@code null}, and the next {@link #toConfig} call then
 * throws {@link NullPointerException}.</p>
 */
public class MaterialConfigurable implements CompositeConfigSerializable, ConfigDeserializable {

    private Material material = Material.STONE;

    public MaterialConfigurable() {}

    public MaterialConfigurable(@NotNull String materialName) {
        fromConfig(materialName);
    }

    public MaterialConfigurable(@NotNull Material material) {
        this.material = material;
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return material.toString();
    }

    @Override
    public void fromConfig(@Nullable Object configObject) {
        if (configObject == null)
            return;
        this.material = Material.matchMaterial((String) configObject);
    }

    /**
     * Returns the configured material, or {@code null} if the last {@link #fromConfig} call was given
     * a name {@link Material#matchMaterial} does not recognise.
     */
    public @Nullable Material getMaterial() {
        return material;
    }

}

