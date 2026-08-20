package me.wyne.wutils.structure.location.condition;

import me.wyne.wutils.common.world.BiomePreset;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public record BiomePresetCondition(@NotNull Set<@NotNull Biome> biomes, @NotNull List<@NotNull String> presets,
                                   boolean invert) implements LocationCondition {

    /**
     * Resolves {@code presets} (named biome groups, e.g. {@code "ocean"}) to their member
     * biomes via {@link BiomePreset#resolve} and builds the condition from the result.
     */
    public static @NotNull BiomePresetCondition of(@NotNull List<@NotNull String> presets, boolean invert) {
        return new BiomePresetCondition(BiomePreset.resolve(presets), presets, invert);
    }

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder()
                .appendCollection(depth, invert ? "is-not-in-biome-preset" : "is-in-biome-preset", presets)
                .buildNoTrail();
    }

    @Override
    public boolean isValid(@NotNull Location location) {
        return biomes.contains(location.getWorld().getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ())) != invert;
    }
}
