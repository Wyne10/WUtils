package me.wyne.wutils.json;

import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class JsonRegistry {

    public static final JsonRegistry global = new JsonRegistry();
    public final LogWrapper log = new LogWrapper();

    private Gson gson = new Gson();
    private File directory;
    private final Map<String, JsonObject> objectMap = new HashMap<>();

    public void registerObject(Object object) {
        for(Field field : object.getClass().getDeclaredFields())
        {
            if (!field.isAnnotationPresent(JSON.class))
                continue;
            registerField(object, field);
        }
    }

    public void registerField(Object holder, Field field) {
        objectMap.put(field.getAnnotation(JSON.class).path(), new JsonObject(holder, field));
    }

    public void write() {
        if (directory == null)
            throw new NullPointerException("JsonRegistry directory is not specified");

        objectMap.forEach((path, object) -> {
            try (Writer writer = new FileWriter(new File(directory, path))) {
                object.field().setAccessible(true);
                gson.toJson(object.field().get(object.holder()), writer);
            } catch (IOException | IllegalAccessException e) {
                log.exception("An exception occurred trying to write json to file", e);
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
                object.field().set(object.holder(), gson.fromJson(reader, object.field().getType()));
            } catch (IOException | IllegalAccessException e) {
                log.exception("An exception occurred trying to read json file", e);
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
