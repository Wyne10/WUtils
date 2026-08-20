package me.wyne.wutils.common.particle;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

/** Parses a single-item {@link ItemStack} by material name, case-insensitively (via {@link Material#matchMaterial}). */
public class ItemStackParser implements StringDataParser<ItemStack> {

    private final static Collection<String> suggestions = Arrays.stream(Material.values()).map(Enum::toString).toList();

    @Override
    public @NotNull Collection<@NotNull String> getSuggestions() {
        return suggestions;
    }

    /** @throws IllegalArgumentException if {@code string} does not match any {@link Material} */
    @SuppressWarnings("DataFlowIssue")
    @Override
    public @NotNull ItemStack getData(@NotNull String string) {
        return new ItemStack(Material.matchMaterial(string));
    }

    @Override
    public @NotNull String toString(@Nullable Object data) {
        return ((ItemStack)data).getType().name();
    }

}
