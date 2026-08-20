package me.wyne.wutils.json;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Registers annotated fields for Gson (de)serialization to and from individual JSON files.
 *
 * <p>Fields are registered either by scanning an object for {@link JSON @JSON}-annotated
 * fields with {@link #registerObject(Object)}, or directly through the {@code register}/
 * {@code registerField} overloads. Each registration is keyed by a path, relative to
 * {@link #getDirectory()}, in a single map shared by every registration made on this
 * instance — including {@link #global}, a static instance meant for shared use across a
 * plugin. The backing map is a plain {@link HashMap}; concurrent registration, write, or
 * load calls on the same instance are not safe.
 *
 * <p>{@link #write()} serializes every registered field's current value to its file;
 * {@link #load()} and its overloads deserialize a file's contents back into its field. Both
 * require a directory to have been set, either through the {@link #JsonRegistry(Gson, File)}
 * constructor or {@link #setDirectory(File)} — otherwise they throw {@link NullPointerException}.
 */
public class JsonRegistry {

    public static @NotNull JsonRegistry global = new JsonRegistry();

    private Gson gson = new Gson();
    private File directory;
    // Keyed by JSON path; a later registration under the same path silently
    // replaces the earlier one, regardless of which object or field it belongs to.
    private final Map<@NotNull String, @NotNull JsonObject> objectMap = new HashMap<>();

    /**
     * Creates a registry with no backing directory. {@link #setDirectory(File)} must be
     * called before {@link #write()} or the directory-dependent {@link #load} overloads
     * can succeed.
     */
    public JsonRegistry() {}

    /**
     * Creates a registry backed by {@code directory}.
     */
    public JsonRegistry(@NotNull Gson gson, @NotNull File directory) {
        this.gson = gson;
        this.directory = directory;
    }

    /**
     * Scans {@code object}'s declared fields for {@link JSON @JSON} annotations and
     * registers each one found, keyed by its {@link JSON#path()}.
     *
     * <p>Only fields declared directly on {@code object}'s class are scanned; inherited
     * fields are not.
     */
    public void registerObject(@NotNull Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(JSON.class))
                continue;
            registerField(object, field);
        }
    }

    /**
     * Removes the registrations added by a matching {@link #registerObject(Object)} call,
     * by looking up each of {@code object}'s {@link JSON @JSON}-annotated fields and
     * removing whatever entry is currently stored at its {@link JSON#path()}.
     *
     * <p>Removal is by path only: it does not verify that the stored entry still belongs to
     * {@code object}. If another registration was later stored under the same path — see
     * {@link #register(Object, Field, String)} — that entry is removed instead. Entries
     * registered without a {@code @JSON}-annotated field, i.e. only through the
     * {@code register}/{@code registerField(Object, Field, Type)} overloads, cannot be
     * removed through this method.
     */
    public void unregisterObject(@NotNull Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(JSON.class))
                continue;
            objectMap.remove(field.getAnnotation(JSON.class).path());
        }
    }

    /**
     * Registers {@code field} under the path from its {@link JSON @JSON} annotation.
     *
     * @throws NullPointerException if {@code field} is not annotated with {@link JSON}
     */
    public void registerField(@NotNull Object holder, @NotNull Field field) {
        register(holder, field, field.getAnnotation(JSON.class).path());
    }

    /**
     * Registers {@code field} under the path from its {@link JSON @JSON} annotation, using
     * an explicit {@code type} instead of the field's own generic type for
     * (de)serialization.
     *
     * @throws NullPointerException if {@code field} is not annotated with {@link JSON}
     */
    public void registerField(@NotNull Object holder, @NotNull Field field, @NotNull Type type) {
        register(holder, field, field.getAnnotation(JSON.class).path(), type);
    }

    /**
     * Registers {@code field} under {@code path} directly, independent of any
     * {@link JSON @JSON} annotation. A later call using the same {@code path} silently
     * replaces this registration.
     */
    public void register(@NotNull Object holder, @NotNull Field field, @NotNull String path) {
        objectMap.put(path, new JsonObject(holder, field, field.getGenericType()));
    }

    /**
     * Registers {@code field} under {@code path} with an explicit {@code type}, independent
     * of any {@link JSON @JSON} annotation. A later call using the same {@code path}
     * silently replaces this registration.
     */
    public void register(@NotNull Object holder, @NotNull Field field, @NotNull String path, @NotNull Type type) {
        objectMap.put(path, new JsonObject(holder, field, type));
    }

    /**
     * Removes every registration.
     */
    public void clear() {
        objectMap.clear();
    }

    /**
     * Writes every registered field's current value to its JSON file under
     * {@link #getDirectory()}.
     *
     * <p>Creates each file's parent directories and the file itself if missing. Uses
     * reflection ({@link Field#setAccessible(boolean) setAccessible(true)}) to read
     * non-public fields.
     *
     * @throws NullPointerException if no directory has been set (see
     *                               {@link #setDirectory(File)})
     */
    public void write() throws NullPointerException, IOException, IllegalAccessException {
        if (directory == null)
            throw new NullPointerException("JsonRegistry directory is not specified");

        for (Map.Entry<String, JsonObject> entry : objectMap.entrySet()) {
            File file = new File(directory, entry.getKey());
            File parent = file.getParentFile();
            if (parent != null && !parent.exists())
                parent.mkdirs();
            if (!file.exists())
                file.createNewFile();
            try (Writer writer = new FileWriter(file)) {
                entry.getValue().field().setAccessible(true);
                gson.toJson(entry.getValue().field().get(entry.getValue().holder()), writer);
            }
        }
    }

    /**
     * Loads every registered field's value from its JSON file under
     * {@link #getDirectory()}.
     *
     * @throws NullPointerException if no directory has been set (see
     *                               {@link #setDirectory(File)})
     */
    public void load() throws NullPointerException, IOException, IllegalAccessException {
        for (Map.Entry<String, JsonObject> entry : objectMap.entrySet()) {
            load(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Loads the value of every field registered for {@code holder}.
     *
     * @throws NullPointerException if no directory has been set (see
     *                               {@link #setDirectory(File)})
     */
    public void load(@NotNull Object holder) throws NullPointerException, IOException, IllegalAccessException {
        for (Map.Entry<String, JsonObject> entry : objectMap.entrySet()) {
            if (!entry.getValue().holder().equals(holder))
                continue;
            load(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Loads {@code object}'s field from the file at {@code path} under
     * {@link #getDirectory()}. Does nothing if the file does not exist or is empty. Uses
     * reflection ({@link Field#setAccessible(boolean) setAccessible(true)}) to write
     * non-public fields.
     *
     * @throws NullPointerException if no directory has been set (see
     *                               {@link #setDirectory(File)})
     */
    public void load(@NotNull String path, @NotNull JsonObject object) throws NullPointerException, IOException, IllegalAccessException {
        if (directory == null)
            throw new NullPointerException("JsonRegistry directory is not specified");

        File file = new File(directory, path);
        if (!file.exists() || file.length() == 0)
            return;

        try (Reader reader = new FileReader(file)) {
            object.field().setAccessible(true);
            object.field().set(object.holder(), gson.fromJson(reader, object.type()));
        }
    }

    public @NotNull Gson getGson() {
        return gson;
    }

    public void setGson(@NotNull Gson gson) {
        this.gson = gson;
    }

    /**
     * @return the backing directory, or {@code null} if none has been set yet — see
     *         {@link #JsonRegistry()}
     */
    public @Nullable File getDirectory() {
        return directory;
    }

    /**
     * Sets the backing directory, which {@link #write()} and the directory-dependent
     * {@link #load} overloads require.
     */
    public void setDirectory(@NotNull File directory) {
        this.directory = directory;
    }

}
