package me.wyne.wutils.structure.location.condition;

import me.wyne.wutils.common.plugin.PluginUtils;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public record MountainsCondition(boolean invert) implements LocationCondition {
    public static final Pattern MOUNTAINS_REGEX = Pattern.compile(".*(?:MOUNTAINS|HILLS).*");
    public static final Set<Biome> MOUNTAIN_BIOMES = Collections.unmodifiableSet(getMountainBiomes());
    public static Set<Biome> getMountainBiomes() {
        Set<Biome> biomes = new HashSet<>();

        for (Biome biome : Biome.values()) {
            if (MOUNTAINS_REGEX.matcher(biome.name()).matches()) {
                biomes.add(biome);
            }
        }

        biomes.add(Biome.ERODED_BADLANDS);

        if (PluginUtils.getServerVersion() == 1165) {
            biomes.add(Biome.MOUNTAIN_EDGE);
            biomes.add(Biome.JUNGLE_EDGE);
            biomes.add(Biome.MODIFIED_JUNGLE_EDGE);
        }

        return biomes;
    }

    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        return new ConfigBuilder()
                .append(depth, "is-in-mountains", !invert)
                .buildNoTrail();
    }

    @Override
    public boolean isValid(@NotNull Location location) {
        return MOUNTAIN_BIOMES.contains(location.getWorld().getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ())) != invert;
    }
}
