package me.wyne.wutils.storage;

import com.google.gson.*;
import me.wyne.wutils.log.Log;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Requires {@link com.google.gson} dependency.
 */
public abstract class JsonStorage implements Storage {

    protected final Plugin plugin;

    protected final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    protected final File storageFile;
    protected final ExecutorService jsonExecutorService;

    public JsonStorage(@NotNull final Plugin plugin, @NotNull final File storageFile)
    {
        try {
            Class.forName("com.google.gson.Gson", false, getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            if (!Log.error("Trying to use JsonStorage but com.google.gson is not included"))
                Bukkit.getLogger().severe("Trying to use JsonStorage but com.google.gson is not included");
            if (!Log.error(e.getMessage()))
                Bukkit.getLogger().severe(e.getMessage());
        }
        this.plugin = plugin;
        this.storageFile = storageFile;
        jsonExecutorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void createStorageFolder()
    {
        if (!plugin.getDataFolder().exists()) {
            Log.info("Создание папки плагина...");
            plugin.getDataFolder().mkdirs();
            Log.info("Папка плагина создана");
        }
    }

    @Override
    public void createStorageFile()
    {
        if (!storageFile.exists()) {
            Log.info("Создание файла '" + storageFile.getName() + "'...");
            try {
                if (storageFile.createNewFile()) {
                    PrintWriter writer = new PrintWriter(storageFile);
                    writer.write("{ }");
                    writer.flush();
                    writer.close();
                }
                Log.info("Файл '" + storageFile.getName() + "' создан");
            } catch (IOException e) {
                Log.error("Произошла ошибка при создании файла '" + storageFile.getName() + "'");
                Log.error(e.getMessage());
            }
        }
    }

    /**
     * Get element from {@link HashMap}. If HashMap doesn't have given key it will return null.
     * HashMap is used because data is often stored as key:value.
     * @param data {@link HashMap} to get element from
     * @param key {@link HashMap} key to get element from
     * @return Element of {@link ValType} from data or null
     * @param <KeyType> Data {@link HashMap} key type
     * @param <ValType> Data {@link HashMap} value type
     */
    @Override
    @Nullable
    public <KeyType, ValType> ValType get(@NotNull final HashMap<KeyType, ValType> data, @NotNull final KeyType key)
    {
        if (!data.containsKey(key))
            return null;
        return data.get(key);
    }
    /**
     * Get {@link Collection} from {@link HashMap}. If HashMap doesn't have given key or retrieved {@link Collection} is empty it will return null.
     * HashMap is used because data is often stored as key:value.
     * @param data {@link HashMap} to get {@link Collection} from
     * @param key {@link HashMap} key to get {@link Collection} from
     * @return {@link Collection} from data or null
     * @param <KeyType> Data {@link HashMap} key type
     * @param <ValType> {@link Collection} value type
     */
    @Override
    @NotNull
    public <KeyType, ValType> Collection<ValType> getCollection(@NotNull final HashMap<KeyType, ? extends Collection<ValType>> data, @NotNull final KeyType key)
    {
        if (!data.containsKey(key) || data.get(key).isEmpty())
            return Collections.emptySet();
        return data.get(key);
    }

    @Override
    public <KeyType, ValType> void save(@Nullable HashMap<KeyType, ValType> data, @NotNull final KeyType key, @NotNull final ValType value, @Nullable final String path)
    {
        if (data != null)
            data.put(key, value);

        jsonExecutorService.execute(() -> {
            try
            {
                JsonObject datas = (JsonObject) JsonParser.parseReader(new FileReader(storageFile));
                if (path != null)
                {
                    JsonObject dataObject = datas.has(key.toString()) ?
                            datas.getAsJsonObject(key.toString()) : new JsonObject();
                    dataObject.add(path, gson.toJsonTree(value));
                    datas.add(key.toString(), dataObject);
                }
                else
                {
                    datas.add(key.toString(), gson.toJsonTree(value));
                }
                PrintWriter writer = new PrintWriter(storageFile);
                writer.write(gson.toJson(datas));
                writer.flush();
                writer.close();
                if (path != null)
                    Log.info("Сохранено значение '" + value + "' ключа '" + key + "' по пути '" + path + "'");
                else
                    Log.info("Сохранено значение '" + value + "' ключа '" + key + "'");
            }
            catch (FileNotFoundException e)
            {
                Log.error("Произошла ошибка при записи значения в файл '" + storageFile.getName() + "'");
                Log.error("Ключ: " + key);
                Log.error("Значение: " + value);
                Log.error("Путь: " + path);
                Log.error(e.getMessage());
            }
        });
    }
    @Override
    public <KeyType, ValType, ColType extends Collection<ValType>> boolean saveCollection(@NotNull HashMap<KeyType, ColType> data, @NotNull final KeyType key, @NotNull final ValType value, @NotNull final String path)
    {
        Collection<ValType> newCollection = new HashSet<ValType>();

        if (data.containsKey(key))
            newCollection = data.get(key);

        if (newCollection.contains(value))
        {
            Log.warn("Значение '" + value + "' коллекции ключа '" + key + "' уже было сохранено");
            Log.warn("Путь: " + path);
            return false;
        }

        newCollection.add(value);
        data.put(key, (ColType) newCollection);

        jsonExecutorService.execute(() -> {
            try
            {
                JsonObject datas = (JsonObject) JsonParser.parseReader(new FileReader(storageFile));
                JsonObject dataObject = datas.has(key.toString()) ?
                        datas.getAsJsonObject(key.toString()) : new JsonObject();
                JsonArray collectionObject = dataObject.has(path) ?
                        dataObject.getAsJsonArray(path) : new JsonArray();
                collectionObject.add(gson.toJsonTree(value));
                dataObject.add(path, collectionObject);
                datas.add(key.toString(), dataObject);
                PrintWriter writer = new PrintWriter(storageFile);
                writer.write(gson.toJson(datas));
                writer.flush();
                writer.close();
                Log.info("Сохранено значение '" + value + "' коллекции ключа '" + key + "'по пути '" + path + "'");
            }
            catch (FileNotFoundException e)
            {
                Log.error("Произошла ошибка при записи значения в файл '" + storageFile.getName() + "'");
                Log.error("Ключ: " + key);
                Log.error("Значение: " + value);
                Log.error("Путь: " + path);
                Log.error(e.getMessage());
            }
        });
        return true;
    }

    @Override
    public <KeyType, ValType> boolean remove(@Nullable HashMap<KeyType, ValType> data, @NotNull final KeyType key, @Nullable final String path)
    {
        if (data != null)
        {
            if (!data.containsKey(key))
            {
                Log.warn("Значение ключа '" + key + "' не найдено");
                Log.warn("Путь: " + path);
                return false;
            }

            data.remove(key);
        }

        jsonExecutorService.execute(() -> {
            try {
                JsonObject datas = (JsonObject) JsonParser.parseReader(new FileReader(storageFile));
                if (!datas.has(key.toString()))
                {
                    if (data != null)
                    {
                        Log.warn("Значение ключа '" + key + "' не найдено");
                        Log.warn("Путь: " + path);
                    }
                    return;
                }
                if (path != null)
                {
                    JsonObject dataObject = datas.getAsJsonObject(key.toString());
                    dataObject.remove(path);
                    datas.add(key.toString(), dataObject);
                }
                else
                {
                    datas.remove(key.toString());
                }
                PrintWriter writer = new PrintWriter(storageFile);
                writer.write(gson.toJson(datas));
                writer.flush();
                writer.close();
                if (path != null)
                    Log.info("Удалено значение ключа '" + key + "' по пути '" + path + "'");
                else
                    Log.info("Удалено значение ключа '" + key + "'");
            } catch (FileNotFoundException e) {
                Log.error("Произошла ошибка при удалении значения из файла '" + storageFile.getName() + "'");
                Log.error("Ключ: " + key);
                Log.error("Путь: " + path);
                Log.error(e.getMessage());
            }
        });
        return true;
    }
    @Override
    public <KeyType, ValType, ColType extends Collection<ValType>> boolean removeCollection(@NotNull HashMap<KeyType, ColType> data, @NotNull final KeyType key, @NotNull final ValType value, @NotNull final String path)
    {
        Collection<ValType> newCollection;

        if (data.containsKey(key))
        {
            newCollection = data.get(key);
        }
        else
        {
            Log.warn("Значение ключа '" + key + "' не найдено");
            Log.warn("Путь: " + path);
            return false;
        }

        if (!newCollection.contains(value))
        {
            Log.warn("Значение '" + value + "' коллекции ключа '" + key + "' не найдено");
            Log.warn("Путь: " + path);
            return false;
        }

        newCollection.remove(value);
        data.put(key, (ColType) newCollection);

        jsonExecutorService.execute(() -> {
            try {
                JsonObject datas = (JsonObject) JsonParser.parseReader(new FileReader(storageFile));
                JsonObject dataObject = datas.getAsJsonObject(key.toString());
                JsonArray collectionObject = dataObject.getAsJsonArray(path);
                for (JsonElement collectionElement : collectionObject.asList())
                {
                    if (collectionElement.getAsString().equals(value.toString()))
                    {
                        collectionObject.remove(collectionElement);
                        break;
                    }
                }
                dataObject.add(path, collectionObject);
                datas.add(key.toString(), dataObject);
                PrintWriter writer = new PrintWriter(storageFile);
                writer.write(gson.toJson(datas));
                writer.flush();
                writer.close();
                Log.info("Удалено значение '" + value + "' коллекции ключа '" + key + "' по пути '" + path + "'");
            } catch (FileNotFoundException e) {
                Log.error("Произошла ошибка при удалении значения из файла '" + storageFile.getName() + "'");
                Log.error("Ключ: " + key);
                Log.error("Значение: " + value);
                Log.error("Путь: " + path);
                Log.error(e.getMessage());
            }
        });
        return true;
    }

    @Override
    public <KeyType, ValType, ColType extends Collection<ValType>> boolean clearCollection(@NotNull HashMap<KeyType, ColType> data, @NotNull final KeyType key, @NotNull final String path)
    {
        if (!data.containsKey(key))
        {
            Log.warn("Значение ключа '" + key + "' не найдено");
            Log.warn("Путь: " + path);
            return false;
        }

        if (data.get(key).isEmpty())
        {
            Log.warn("Коллекция ключа '" + key + "' не имеет элементов");
            Log.warn("Путь: " + path);
            return false;
        }

        data.put(key, (ColType) Collections.emptySet());

        jsonExecutorService.execute(() -> {
            try {
                JsonObject datas = (JsonObject) JsonParser.parseReader(new FileReader(storageFile));
                JsonObject dataObject = datas.getAsJsonObject(key.toString());
                dataObject.add(path, new JsonArray());
                datas.add(key.toString(), dataObject);
                PrintWriter writer = new PrintWriter(storageFile);
                writer.write(gson.toJson(datas));
                writer.flush();
                writer.close();
                Log.info("Очищена коллекция ключа '" + key + "' по пути '" + path + "'");
            } catch (FileNotFoundException e) {
                Log.error("Произошла ошибка при удалении значения из файла '" + storageFile.getName() + "'");
                Log.error("Ключ: " + key);
                Log.error("Путь: " + path);
                Log.error(e.getMessage());
            }
        });
        return true;
    }
}
