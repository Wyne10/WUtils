package me.wyne.wutils.structure.location.condition;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public record BiomeCondition(@NotNull Set<@NotNull Biome> biomes, boolean invert) implements LocationCondition {
    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        List<String> keys = biomes.stream()
                .map(biome -> biome.getKey().toString())
                .toList();
        return new ConfigBuilder()
                .appendCollection(depth, invert ? "is-not-in-biome" : "is-in-biome", keys)
                .buildNoTrail();
    }

    @Override
    public boolean isValid(@NotNull Location location) {
        return biomes.contains(location.getWorld().getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ())) != invert;
    }
}
