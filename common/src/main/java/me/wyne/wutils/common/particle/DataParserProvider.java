package me.wyne.wutils.common.particle;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("deprecation")
public class DataParserProvider {

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

    public static StringDataParser<?> getDataParser(Class<?> clazz)
    {
        return parserMap.get(clazz);
    }

}
