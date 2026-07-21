package me.wyne.wutils.structure.location.condition;

import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurable.ConfigBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public record BlockCondition(@NotNull Set<@NotNull Material> blocks, boolean invert) implements LocationCondition {
    @Override
    public String toConfig(int depth, ConfigEntry configEntry) {
        List<String> keys = blocks.stream()
                .map(block -> block.getKey().toString())
                .toList();
        return new ConfigBuilder()
                .appendCollection(depth, invert ? "is-not-on-block" : "is-on-block", keys)
                .buildNoTrail();
    }

    @Override
    public boolean isValid(@NotNull Location location) {
        return blocks.contains(location.getBlock().getType()) != invert;
    }
}
