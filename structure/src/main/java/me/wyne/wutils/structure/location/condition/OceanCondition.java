package me.wyne.wutils.structure.location.condition;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Whether the candidate location's biome name contains {@code "OCEAN"}.
 *
 * <p>{@code invert} follows the record's own polarity, not the config key's: {@code false}
 * means "must be in ocean" ({@code is-in-ocean: true} in config).</p>
 */
public record OceanCondition(boolean invert) implements LocationCondition {
    public static final @NotNull Set<@NotNull Biome> OCEAN_BIOMES = Arrays.stream(Biome.values())
            .filter(biome -> biome.name().contains("OCEAN"))
            .collect(Collectors.toUnmodifiableSet());

    @Override
    public @NotNull String toConfig(int depth, @NotNull ConfigEntry configEntry) {
        return new ConfigBuilder()
                .append(depth, "is-in-ocean", !invert)
                .buildNoTrail();
    }

    @Override
    public boolean isValid(@NotNull Location location) {
        return OCEAN_BIOMES.contains(location.getWorld().getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ())) != invert;
    }
}
