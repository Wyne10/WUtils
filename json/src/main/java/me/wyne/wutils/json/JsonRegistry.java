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
        objectMap.put(field.getAnnotation(JSON.class).path(), new JsonObject(holder, field, field.getType()));
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
