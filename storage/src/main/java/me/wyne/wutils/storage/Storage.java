package me.wyne.wutils.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public interface Storage {

    /**
     * Create folder for storage file.
     */
    void createStorageFolder(@NotNull final File folderPath);
    /**
     * Create storage file.
     */
    void createStorageFile();
    /**
     * Load data from storage file. Uses {@link #loadDataImpl()}, puts it in other thread and logs.
     */
    void loadData();

    /**
     * Implementation of loading data from storage file. {@link #loadData()} will take care of logging and putting implementation in other thread.
     * @return {@link Throwable} if data loading caused exception
     */
    @Nullable Throwable loadDataImpl();

    /**
     * Get element from data {@link Map}. May be used to add extra logic to data query.
     * Map is used because data is often stored as key:value.
     * @param data Data {@link Map} to get element from
     * @param key Data {@link Map} key to get element from
     * @return Element of {@link ValType} from data {@link Map} or null
     * @param <KeyType> Data {@link Map} key type
     * @param <ValType> Data {@link Map} value type
     */
    @Nullable
    <KeyType, ValType> ValType get(@NotNull final Map<KeyType, ValType> data, @NotNull final KeyType key);
    /**
     * Get {@link Collection} from data {@link Map}. May be used to add extra logic to data query.
     * Map is used because data is often stored as key:value.
     * @param data Data {@link Map} to get {@link Collection} from
     * @param key Data {@link Map} key to get {@link Collection} from
     * @return {@link Collection} from data {@link Map} or empty set
     * @param <KeyType> Data {@link Map} key type
     * @param <ValType> {@link Collection} value type
     */
    @NotNull
    <KeyType, ValType> Collection<ValType> getCollection(@NotNull final Map<KeyType, ? extends Collection<ValType>> data, @NotNull final KeyType key);

    /**
     * Save element to data {@link Map} and to storageFile to given path.
     * <br>Set path as null to save value directly to key in storageFile.
     * <br>Set data as null to save value only to storageFile.
     * @param data Data {@link Map} to save element to
     * @param key Data {@link Map} key to save element to
     * @param value Value to save
     * @param path Path in storageFile to save element to
     * @param <KeyType> Data {@link Map} key type
     * @param <ValType> Data {@link Map} value type
     */
    <KeyType, ValType> void save(@Nullable Map<KeyType, ValType> data, @NotNull final KeyType key, @NotNull final ValType value, @Nullable final String path);
    /**
     * Save element to {@link Collection} in data {@link Map} and to storageFile to given path.
     * @param data Data {@link Map} to save {@link Collection} to
     * @param key Data {@link Map} key to {@link Collection} to save element to
     * @param value Value to save
     * @param path Path to {@link com.google.gson.JsonArray} in storageFile to save element to
     * @return True/False if save succeed/failed
     * @param <KeyType> Data {@link Map} key type
     * @param <ValType> {@link Collection} value type
     * @param <ColType> Type of {@link Collection}
     */
    <KeyType, ValType, ColType extends Collection<ValType>> boolean saveCollection(@NotNull Map<KeyType, ColType> data, @NotNull final KeyType key, @NotNull final ValType value, @NotNull final String path);
    /**
     * Save element to inner {@link Map} in data {@link Map} and to storageFile to given path.
     * @param data Data {@link Map} to save inner {@link Map} element to
     * @param key Data {@link Map} key to inner {@link Map} to save element to
     * @param mapKey Inner {@link Map} key to save element to
     * @param value Value to save
     * @param path Path to inner {@link Map} element in storageFile to save element to
     * @param <KeyType> Data {@link Map} key type
     * @param <MapKeyType> Inner {@link Map} key type
     * @param <ValType> Inner {@link Map} value type
     * @param <MapType> Type of inner {@link Map}
     */
    <KeyType, MapKeyType, ValType, MapType extends Map<MapKeyType, ValType>> void saveMap(@NotNull Map<KeyType, MapType> data, @NotNull final KeyType key, @NotNull final MapKeyType mapKey, @NotNull final ValType value, @Nullable final String path);

    /**
     * Remove element from data {@link Map} and from storageFile from given path.
     * <br>Set path as null to remove whole key from storageFile.
     * <br>Set data as null to remove value only from storageFile.
     * @param data Data {@link Map} to remove element from
     * @param key Data {@link Map} key to remove element from
     * @param path Path in storageFile to remove element from
     * @return True/False if remove succeed/failed
     * @param <KeyType> Data {@link Map} key type
     * @param <ValType> Data {@link Map} value type
     */
    <KeyType, ValType> boolean remove(@Nullable Map<KeyType, ValType> data, @NotNull final KeyType key, @Nullable final String path);
    /**
     * Remove element from {@link Collection} in data {@link Map} and from storageFile from given path.
     * @param data Data {@link Map} to remove {@link Collection} element from
     * @param key Data {@link Map} key to {@link Collection} to remove element from
     * @param value Value to remove
     * @param path Path to {@link com.google.gson.JsonArray} in storageFile to remove element from
     * @return True/False if remove succeed/failed
     * @param <KeyType> Data {@link Map} key type
     * @param <ValType> {@link Collection} value type
     * @param <ColType> Type of {@link Collection}
     */
    <KeyType, ValType, ColType extends Collection<ValType>> boolean removeCollection(@NotNull Map<KeyType, ColType> data, @NotNull final KeyType key, @NotNull final ValType value, @NotNull final String path);
    /**
     * Remove element from inner {@link Map} in data {@link Map} and from storageFile from given path.
     * <br>Set path as null to remove element by mapKey
     * @param data Data {@link Map} to remove inner {@link Map} element from
     * @param key Data {@link Map} key to inner {@link Map} to remove element from
     * @param mapKey Inner {@link Map} key to remove
     * @param path Path to inner {@link Map} element in storageFile to remove
     * @return True/False if remove succeed/failed
     * @param <KeyType> Data {@link Map} key type
     * @param <MapKeyType> Inner {@link Map} key type
     * @param <ValType> Inner {@link Map} value type
     * @param <MapType> Type of inner {@link Map}
     */
    <KeyType, MapKeyType, ValType, MapType extends Map<MapKeyType, ValType>> boolean removeMap(@NotNull Map<KeyType, MapType> data, @NotNull final KeyType key, @NotNull final MapKeyType mapKey, @Nullable final String path);

    /**
     * Clear {@link Collection} in data {@link Map} and in storageFile in given path.
     * @param data Data {@link Map} to clear {@link Collection} from
     * @param key Data {@link Map} key to {@link Collection} to clear
     * @param path Path to {@link com.google.gson.JsonArray} in storageFile to clear
     * @return True/False if clear succeed/failed
     * @param <KeyType> Data {@link Map} key type
     * @param <ValType> {@link Collection} value type
     * @param <ColType> Type of {@link Collection}
     */
    <KeyType, ValType, ColType extends Collection<ValType>> boolean clearCollection(@NotNull Map<KeyType, ColType> data, @NotNull final KeyType key, @NotNull final String path);
    /**
     * Clear inner {@link Map} in data {@link Map} and in storageFile.
     * @param data Data {@link Map} to clear inner {@link Map} from
     * @param key Data {@link Map} key to inner {@link Map} to clear
     * @param mapKeyType Inner {@link Map} key type
     * @return True/False if clear succeed/failed
     * @param <KeyType> Data {@link Map} key type
     * @param <MapKeyType> Inner {@link Map} key type
     * @param <ValType> Inner {@link Map} value type
     * @param <MapType> Type of inner {@link Map}
     */
    <KeyType, MapKeyType, ValType, MapType extends Map<MapKeyType, ValType>> boolean clearMap(@NotNull Map<KeyType, MapType> data, @NotNull final KeyType key, @NotNull final Class<MapKeyType> mapKeyType);
}
