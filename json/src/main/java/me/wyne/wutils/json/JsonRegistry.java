package me.wyne.wutils.json;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class JsonRegistry {

    public static final JsonRegistry global = new JsonRegistry();
    public Logger log = LoggerFactory.getLogger(getClass());

    private Gson gson = new Gson();
    private File directory;
    private final Map<String, JsonObject> objectMap = new HashMap<>();

    public void registerObject(Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(JSON.class))
                continue;
            registerField(object, field);
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

    public void write() {
        if (directory == null)
            throw new NullPointerException("JsonRegistry directory is not specified");

        objectMap.forEach((path, object) -> {
            File file = new File(directory, path);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists())
                parent.mkdirs();

            try {
                if (!file.exists())
                    file.createNewFile();
                Writer writer = new FileWriter(file);
                object.field().setAccessible(true);
                gson.toJson(object.field().get(object.holder()), writer);
                writer.close();
            } catch (IOException | IllegalAccessException e) {
                log.error("An exception occurred trying to write json to file", e);
            }
        });
    }

    public void load() {
        if (directory == null)
            throw new NullPointerException("JsonRegistry directory is not specified");

        objectMap.forEach((path, object) -> {
            File file = new File(directory, path);
            if (!file.exists() || file.length() == 0)
                return;

            try (Reader reader = new FileReader(file)) {
                object.field().setAccessible(true);
                object.field().set(object.holder(), gson.fromJson(reader, object.type()));
            } catch (IOException | IllegalAccessException e) {
                log.error("An exception occurred trying to read json file", e);
            }
        });
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
