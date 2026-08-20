package me.wyne.wutils.common.particle;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Looks up the {@link StringDataParser} registered for a particle data class. Lookup is an exact
 * {@link Class} match against a fixed set of {@link Void}, {@link Material}, {@link BlockFace},
 * {@link Potion}, {@link Integer}, {@link Color}, {@link ItemStack}, {@link BlockData} and
 * {@link Particle.DustOptions} parsers — subclasses and unregistered classes have no parser.
 */
@SuppressWarnings("deprecation")
public final class DataParserProvider {

    private final static Map<Class<?>, StringDataParser<?>> parserMap = new HashMap<>()
    {
        { put(Void.class, new VoidDataParser()); }
        { put(Material.class, new MaterialParser()); }
        { put(BlockFace.class, new BlockFaceParser()); }
        { put(Potion.class, new PotionParser()); }
        { put(Integer.class, new IntegerParser()); }
        { put(Color.class, new ColorParser()); }
        { put(ItemStack.class, new ItemStackParser()); }
        { put(BlockData.class, new BlockDataParser()); }
        { put(Particle.DustOptions.class, new DustOptionsParser()); }
    };

    /** Returns the parser registered for {@code clazz}, or {@code null} if none is registered for that exact class. */
    public static @Nullable StringDataParser<?> getDataParser(@NotNull Class<?> clazz)
    {
        return parserMap.get(clazz);
    }

}
