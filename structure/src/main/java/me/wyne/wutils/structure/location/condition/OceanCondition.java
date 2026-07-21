package me.wyne.wutils.structure.location.condition;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public record OceanCondition(boolean invert) implements LocationCondition {
    public static final Set<Biome> OCEAN_BIOMES = Arrays.stream(Biome.values())
            .filter(biome -> biome.name().contains("OCEAN"))
            .collect(Collectors.toUnmodifiableSet());

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder()
                .append(depth, "is-in-ocean", !invert)
                .buildNoTrail();
    }

    @Override
    public boolean isValid(@NotNull Location location) {
        return OCEAN_BIOMES.contains(location.getWorld().getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ())) != invert;
    }
}
