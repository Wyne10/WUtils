package me.wyne.wutils.structure.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.worldedit.world.World;
import me.wyne.wutils.structure.WorldStructure;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.function.Function;

/**
 * Optional standalone facade for persisting a single {@link WorldStructure} to a JSON file (plus its two
 * side-car {@code .schem} files) and reloading it. Callers who instead nest a structure inside a larger,
 * separately-serialized object should register a {@link WorldStructureMementoSerializer} on their own
 * {@link GsonBuilder} and keep a {@link WorldStructureMemento} field (obtained via
 * {@link WorldStructure#capture()}) alongside their own state.
 */
public class WorldStructurePersistence {

    private final Gson gson;

    public WorldStructurePersistence(@NotNull File schematicDirectory) {
        this(WorldStructureMementoSerializer.of(schematicDirectory));
    }

    public WorldStructurePersistence(@NotNull File schematicDirectory,
                                     @NotNull Function<String, World> worldResolver) {
        this(new WorldStructureMementoSerializer(schematicDirectory, worldResolver));
    }

    public WorldStructurePersistence(@NotNull WorldStructureMementoSerializer serializer) {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(WorldStructureMemento.class, serializer)
                .setPrettyPrinting()
                .create();
    }

    /**
     * Captures the (already spawned) structure and writes its JSON metadata to {@code jsonFile}, writing
     * the two {@code .schem} side-cars into the serializer's schematic directory as a side effect.
     */
    public void save(@NotNull File jsonFile, @NotNull WorldStructure worldStructure) throws IOException {
        var parent = jsonFile.getParentFile();
        if (parent != null && !parent.exists())
            parent.mkdirs();
        try (Writer writer = new FileWriter(jsonFile)) {
            gson.toJson(worldStructure.capture(), WorldStructureMemento.class, writer);
        }
    }

    /**
     * Reads the JSON metadata and side-car {@code .schem} files and reconstructs an already-spawned
     * {@link WorldStructure}. The returned instance must not be {@code spawn()}ed again.
     */
    public @NotNull WorldStructure load(@NotNull File jsonFile) throws IOException {
        try (Reader reader = new FileReader(jsonFile)) {
            var memento = gson.fromJson(reader, WorldStructureMemento.class);
            return WorldStructure.restore(memento);
        }
    }

    public @NotNull Gson getGson() {
        return gson;
    }
}
