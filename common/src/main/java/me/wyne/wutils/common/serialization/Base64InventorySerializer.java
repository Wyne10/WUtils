package me.wyne.wutils.common.serialization;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Gson (de)serializer that round-trips a Bukkit {@link Inventory} through Bukkit's native object
 * (de)serialization, base64-encoded into a single JSON string. Register it on a {@code GsonBuilder}
 * via {@code registerTypeAdapter(Inventory.class, new Base64InventorySerializer())}. Requires Gson
 * on the classpath.
 */
public class Base64InventorySerializer implements JsonSerializer<Inventory>, JsonDeserializer<Inventory> {

    /**
     * Decodes the base64 JSON string back into an {@link Inventory} of the size it was
     * serialized with.
     *
     * @throws JsonParseException if the encoded data is corrupt or references a class that
     *                            cannot be resolved
     */
    @Override
    public @NotNull Inventory deserialize(@NotNull JsonElement json, @NotNull Type typeOfT, @NotNull JsonDeserializationContext context) throws JsonParseException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(json.getAsString()));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            Inventory inventory = Bukkit.createInventory(null, dataInput.readInt());
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }
            return inventory;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("An exception occurred trying to deserialize inventory from base64", e);
        }
    }

    @Override
    public @NotNull JsonElement serialize(@NotNull Inventory src, @NotNull Type typeOfSrc, @NotNull JsonSerializationContext context) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeInt(src.getSize());
            for (int i = 0; i < src.getSize(); i++) {
                dataOutput.writeObject(src.getItem(i));
            }
            return new JsonPrimitive(Base64Coder.encodeLines(outputStream.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException("An exception occurred trying to serialize inventory to base64", e);
        }
    }

}
