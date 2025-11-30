package me.wyne.wutils.json;

import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class JsonRegistry {

    public static JsonRegistry global = new JsonRegistry();

    private Gson gson = new Gson();
    private File directory;
    private final Map<String, JsonObject> objectMap = new HashMap<>();

    public JsonRegistry() {}

    public JsonRegistry(Gson gson, File directory) {
        this.gson = gson;
        this.directory = directory;
    }

    public void registerObject(Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(JSON.class))
                continue;
            registerField(object, field);
        }
    }

    public void unregisterObject(Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(JSON.class))
                continue;
            objectMap.remove(field.getAnnotation(JSON.class).path());
        }
    }

    public void registerField(Object holder, Field field) {
        register(holder, field, field.getAnnotation(JSON.class).path());
    }

    public void registerField(Object holder, Field field, Type type) {
        register(holder, field, field.getAnnotation(JSON.class).path(), type);
    }

    public void register(Object holder, Field field, String path) {
        objectMap.put(path, new JsonObject(holder, field, field.getGenericType()));
    }

    public void register(Object holder, Field field, String path, Type type) {
        objectMap.put(path, new JsonObject(holder, field, type));
    }

    public void clear() {
        objectMap.clear();
    }

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

    public void load() throws NullPointerException, IOException, IllegalAccessException {
        for (Map.Entry<String, JsonObject> entry : objectMap.entrySet()) {
            load(entry.getKey(), entry.getValue());
        }
    }

    public void load(Object holder) throws NullPointerException, IOException, IllegalAccessException {
        for (Map.Entry<String, JsonObject> entry : objectMap.entrySet()) {
            if (!entry.getValue().holder().equals(holder))
                continue;
            load(entry.getKey(), entry.getValue());
        }
    }

    public void load(String path, JsonObject object) throws NullPointerException, IOException, IllegalAccessException {
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

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

}
