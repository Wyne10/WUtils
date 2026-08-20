package me.wyne.wutils.structure.persistence;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * Gson {@link JsonSerializer}/{@link JsonDeserializer} for {@link WorldStructureMemento}: writes the
 * clipboard and snapshot out as {@code .schem} files under {@code schematicDirectory} and everything
 * else as JSON fields, so a spawned structure survives a restart.
 *
 * <p>Requires WorldEdit on the runtime classpath ({@code compileOnlyApi} — the consumer must
 * supply it); Gson itself is not declared directly by this module but arrives transitively through
 * WorldEdit's own dependencies, so any consumer able to use this class already has it.</p>
 */
public class WorldStructureMementoSerializer implements JsonSerializer<WorldStructureMemento>, JsonDeserializer<WorldStructureMemento> {

    private final File schematicDirectory;
    private final Function<String, World> worldResolver;

    public WorldStructureMementoSerializer(@NotNull File schematicDirectory,
                                           @NotNull Function<String, World> worldResolver) {
        this.schematicDirectory = schematicDirectory;
        this.worldResolver = worldResolver;
    }

    /**
     * Creates a serializer that resolves a memento's saved world name via {@link Bukkit#getWorld}
     * (returning {@code null}, and so failing deserialization, if that world is not currently
     * loaded).
     */
    public static @NotNull WorldStructureMementoSerializer of(@NotNull File schematicDirectory) {
        return new WorldStructureMementoSerializer(schematicDirectory, name -> {
            org.bukkit.World bukkitWorld = Bukkit.getWorld(name);
            return bukkitWorld == null ? null : BukkitAdapter.adapt(bukkitWorld);
        });
    }

    /**
     * Writes {@code src}'s clipboard and snapshot to {@code <uniqueKey>-structure.schem} and
     * {@code <uniqueKey>-snapshot.schem} under {@code schematicDirectory} as a side effect, then
     * returns the remaining state as JSON.
     *
     * @throws JsonParseException if the clipboard region's world is unset
     */
    @Override
    public @NotNull JsonElement serialize(@NotNull WorldStructureMemento src, @NotNull Type typeOfSrc, @NotNull JsonSerializationContext context) {
        var region = src.region();
        var clipboardRegion = src.clipboardRegion();
        var world = clipboardRegion.getWorld();
        if (world == null)
            throw new JsonParseException("Cannot serialize memento " + src.uniqueKey() + ": clipboard region world is null");

        var structureFile = src.uniqueKey() + "-structure.schem";
        var snapshotFile = src.uniqueKey() + "-snapshot.schem";
        writeSchematic(src.clipboard(), new File(schematicDirectory, structureFile));
        writeSchematic(src.snapshot(), new File(schematicDirectory, snapshotFile));

        var json = new JsonObject();
        json.addProperty("uniqueKey", src.uniqueKey());
        json.addProperty("world", world.getName());
        json.add("location", vector(src.location().getX(), src.location().getY(), src.location().getZ()));
        json.add("transform", serializeTransform(src.transform()));

        var regionJson = new JsonObject();
        regionJson.addProperty("id", region.getId());
        regionJson.addProperty("transient", region.isTransient());
        regionJson.add("min", vector(region.getMinimumPoint()));
        regionJson.add("max", vector(region.getMaximumPoint()));
        json.add("region", regionJson);

        var clipboardRegionJson = new JsonObject();
        clipboardRegionJson.add("min", vector(clipboardRegion.getMinimumPoint()));
        clipboardRegionJson.add("max", vector(clipboardRegion.getMaximumPoint()));
        json.add("clipboardRegion", clipboardRegionJson);

        json.addProperty("clipboard", structureFile);
        json.addProperty("snapshot", snapshotFile);
        return json;
    }

    /**
     * Rebuilds a {@link WorldStructureMemento} from JSON, reading its clipboard and snapshot back
     * from the schematic files {@link #serialize} wrote.
     *
     * @throws JsonParseException if the saved world name does not resolve via this serializer's
     *                            world resolver (not currently loaded)
     */
    @Override
    public @NotNull WorldStructureMemento deserialize(@NotNull JsonElement element, @NotNull Type typeOfT, @NotNull JsonDeserializationContext context) throws JsonParseException {
        var json = element.getAsJsonObject();
        var uniqueKey = json.get("uniqueKey").getAsString();

        var worldName = json.get("world").getAsString();
        var world = worldResolver.apply(worldName);
        if (world == null)
            throw new JsonParseException("Cannot restore structure " + uniqueKey + ": world '" + worldName + "' is not loaded");

        var locationArray = json.get("location").getAsJsonArray();
        var location = new Location(world,
                locationArray.get(0).getAsDouble(),
                locationArray.get(1).getAsDouble(),
                locationArray.get(2).getAsDouble());

        var regionJson = json.getAsJsonObject("region");
        var region = new ProtectedCuboidRegion(
                regionJson.get("id").getAsString(),
                regionJson.get("transient").getAsBoolean(),
                vector(regionJson.get("min")),
                vector(regionJson.get("max")));

        var clipboardRegionJson = json.getAsJsonObject("clipboardRegion");
        var clipboardRegion = new CuboidRegion(world,
                vector(clipboardRegionJson.get("min")),
                vector(clipboardRegionJson.get("max")));

        var transform = deserializeTransform(json.get("transform"));

        var clipboard = readSchematic(new File(schematicDirectory, json.get("clipboard").getAsString()));
        var snapshot = readSchematic(new File(schematicDirectory, json.get("snapshot").getAsString()));

        return new WorldStructureMemento(uniqueKey, location, region, clipboardRegion, transform, clipboard, snapshot);
    }

    private static @NotNull JsonElement serializeTransform(@NotNull Transform transform) {
        if (transform.isIdentity())
            return JsonNull.INSTANCE;
        if (transform instanceof AffineTransform affine) {
            var array = new JsonArray();
            for (double coefficient : affine.coefficients())
                array.add(coefficient);
            return array;
        }
        throw new UnsupportedOperationException("Cannot serialize transform of type " + transform.getClass().getName());
    }

    private static @NotNull Transform deserializeTransform(@Nullable JsonElement element) {
        if (element == null || element.isJsonNull())
            return new AffineTransform();
        var array = element.getAsJsonArray();
        var coefficients = new double[array.size()];
        for (int i = 0; i < array.size(); i++)
            coefficients[i] = array.get(i).getAsDouble();
        return new AffineTransform(coefficients);
    }

    private static @NotNull JsonArray vector(double x, double y, double z) {
        var array = new JsonArray();
        array.add(x);
        array.add(y);
        array.add(z);
        return array;
    }

    private static @NotNull JsonArray vector(@NotNull BlockVector3 vector) {
        var array = new JsonArray();
        array.add(vector.getX());
        array.add(vector.getY());
        array.add(vector.getZ());
        return array;
    }

    private static @NotNull BlockVector3 vector(@NotNull JsonElement element) {
        var array = element.getAsJsonArray();
        return BlockVector3.at(array.get(0).getAsInt(), array.get(1).getAsInt(), array.get(2).getAsInt());
    }

    private static void writeSchematic(@NotNull Clipboard clipboard, @NotNull File file) {
        var parent = file.getParentFile();
        if (parent != null && !parent.exists())
            parent.mkdirs();
        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            writer.write(clipboard);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write schematic " + file.getName(), e);
        }
    }

    private static Clipboard readSchematic(@NotNull File file) {
        if (!file.exists())
            throw new IllegalStateException("Schematic file not found: " + file.getPath());
        try (ClipboardReader reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(new FileInputStream(file))) {
            return reader.read();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read schematic " + file.getName(), e);
        }
    }
}
