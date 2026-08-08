package me.wyne.wutils.common.world;

import org.bukkit.block.Biome;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.wyne.wutils.common.world.WorldUtils.HIGHLAND_REGEX;

public enum BiomePreset {
    ALL(Arrays.stream(Biome.values())
            .filter(biome -> !biome.name().equals("MOUNTAIN_EDGE")
                    && !biome.name().equals("THE_VOID")
                    && !biome.name().equals("CUSTOM"))
            .collect(Collectors.toUnmodifiableSet())),
    SNOWY("SNOWY_TAIGA", "SNOWY_TUNDRA", "SNOWY_BEACH", "ICE_SPIKES",
            "SNOWY_TAIGA_MOUNTAINS", "FROZEN_RIVER"),
    COLD("MOUNTAINS", "GRAVELLY_MOUNTAINS", "WOODED_MOUNTAINS",
            "MODIFIED_GRAVELLY_MOUNTAINS", "FROZEN_OCEAN", "DEEP_FROZEN_OCEAN",
            "TAIGA", "TAIGA_MOUNTAINS", "GIANT_TREE_TAIGA", "GIANT_SPRUCE_TAIGA",
            "STONE_SHORE", "WOODED_HILLS", "TAIGA_HILLS", "SNOWY_TAIGA_HILLS",
            "GIANT_TREE_TAIGA_HILLS", "GIANT_SPRUCE_TAIGA_HILLS", "SNOWY_MOUNTAINS"),
    SNOWY_OR_COLD(Stream.concat(BiomePreset.SNOWY.getBiomes().stream(), BiomePreset.COLD.getBiomes().stream())
            .collect(Collectors.toUnmodifiableSet())),
    LUSH("PLAINS", "SUNFLOWER_PLAINS", "FOREST", "FLOWER_FOREST", "BIRCH_FOREST",
            "TALL_BIRCH_FOREST", "DARK_FOREST", "DARK_FOREST_HILLS", "SWAMP",
            "SWAMP_HILLS", "JUNGLE", "MODIFIED_JUNGLE", "JUNGLE_EDGE", "MODIFIED_JUNGLE_EDGE",
            "BAMBOO_JUNGLE", "RIVER", "BEACH", "MUSHROOM_FIELDS", "MUSHROOM_FIELD_SHORE",
            "JUNGLE_HILLS", "BIRCH_FOREST_HILLS", "TALL_BIRCH_HILLS", "BAMBOO_JUNGLE_HILLS"),
    WARM("DESERT", "DESERT_LAKES", "DESERT_HILLS", "SAVANNA", "SHATTERED_SAVANNA", "BADLANDS",
            "ERODED_BADLANDS", "WOODED_BADLANDS_PLATEAU", "MODIFIED_WOODED_BADLANDS_PLATEAU",
            "SAVANNA_PLATEAU", "BADLANDS_PLATEAU", "MODIFIED_BADLANDS_PLATEAU", "SHATTERED_SAVANNA_PLATEAU"),
    LUSH_OR_WARM(Stream.concat(BiomePreset.LUSH.getBiomes().stream(), BiomePreset.WARM.getBiomes().stream())
            .collect(Collectors.toUnmodifiableSet())),
    OCEAN(Arrays.stream(Biome.values())
            .filter(biome -> biome.name().contains("OCEAN"))
            .collect(Collectors.toUnmodifiableSet())),
    HIGHLAND(Arrays.stream(Biome.values())
            .filter(biome -> HIGHLAND_REGEX.matcher(biome.name()).matches())
            .collect(Collectors.toUnmodifiableSet())),
    WOODLAND("FOREST", "FLOWER_FOREST", "BIRCH_FOREST", "TALL_BIRCH_FOREST",
            "DARK_FOREST", "DARK_FOREST_HILLS", "TAIGA", "TAIGA_MOUNTAINS",
            "SNOWY_TAIGA", "SNOWY_TAIGA_MOUNTAINS", "GIANT_TREE_TAIGA", "GIANT_SPRUCE_TAIGA",
            "JUNGLE", "MODIFIED_JUNGLE", "JUNGLE_EDGE", "MODIFIED_JUNGLE_EDGE", "BAMBOO_JUNGLE",
            "WOODED_MOUNTAINS", "WOODED_HILLS", "WOODED_BADLANDS_PLATEAU", "MODIFIED_WOODED_BADLANDS_PLATEAU",
            "TAIGA_HILLS", "SNOWY_TAIGA_HILLS", "JUNGLE_HILLS", "BIRCH_FOREST_HILLS", "TALL_BIRCH_HILLS",
            "GIANT_TREE_TAIGA_HILLS", "GIANT_SPRUCE_TAIGA_HILLS", "BAMBOO_JUNGLE_HILLS"),
    WETLAND("RIVER", "FROZEN_RIVER", "SWAMP", "SWAMP_HILLS", "BEACH", "SNOWY_BEACH", "STONE_SHORE"),
    NETHER("NETHER_WASTES", "SOUL_SAND_VALLEY", "CRIMSON_FOREST", "WARPED_FOREST", "BASALT_DELTAS"),
    END("THE_END", "SMALL_END_ISLANDS", "END_MIDLANDS", "END_HIGHLANDS", "END_BARRENS"),
    OVERWORLD(BiomePreset.ALL.getBiomes().stream()
            .filter(biome -> !BiomePreset.NETHER.getBiomes().contains(biome)
                    && !BiomePreset.END.getBiomes().contains(biome))
            .collect(Collectors.toUnmodifiableSet()));

    private final Set<Biome> biomes;

    BiomePreset(String... biomeNames) {
        this.biomes = resolve(biomeNames);
    }

    BiomePreset(Set<Biome> biomes) {
        this.biomes = biomes;
    }

    private static Set<Biome> resolve(String... biomeNames) {
        return Arrays.stream(biomeNames)
                .map(name -> {
                    try {
                        return Biome.valueOf(name);
                    } catch (IllegalArgumentException notPresentOnThisVersion) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Biome> getBiomes() {
        return biomes;
    }

    public static Set<Biome> resolve(List<String> presets) {
        Set<Biome> result = new HashSet<>();
        for (String entry : presets) {
            boolean negate = entry.startsWith("!");
            String name = negate ? entry.substring(1) : entry;
            BiomePreset preset;
            try {
                preset = BiomePreset.valueOf(name.toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException unknownPreset) {
                continue;
            }
            if (negate)
                result.removeAll(preset.biomes);
            else
                result.addAll(preset.biomes);
        }
        return result;
    }
}