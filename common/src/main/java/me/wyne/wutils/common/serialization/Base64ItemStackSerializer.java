package me.wyne.wutils.common.serialization;

import com.google.gson.*;
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
 * Gson (de)serializer that round-trips a Bukkit {@link ItemStack} through Bukkit's native object
 * (de)serialization, base64-encoded into a single JSON string. Register it on a {@code GsonBuilder}
 * via {@code registerTypeAdapter(ItemStack.class, new Base64ItemStackSerializer())}. Requires Gson
 * on the classpath.
 */
public class Base64ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    /**
     * Decodes the base64 JSON string back into an {@link ItemStack}.
     *
     * @throws JsonParseException if the encoded data is corrupt or references a class that
     *                            cannot be resolved
     */
    @Override
    public @NotNull ItemStack deserialize(@NotNull JsonElement json, @NotNull Type typeOfT, @NotNull JsonDeserializationContext context) throws JsonParseException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(json.getAsString()));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            return (ItemStack) dataInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("An exception occurred trying to deserialize ItemStack from base64", e);
        }
    }

    @Override
    public @NotNull JsonElement serialize(@NotNull ItemStack src, @NotNull Type typeOfSrc, @NotNull JsonSerializationContext context) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeObject(src);
            return new JsonPrimitive(Base64Coder.encodeLines(outputStream.toByteArray()));
        } catch (IOException e) {
            throw new RuntimeException("An exception occurred trying to serialize ItemStack to base64", e);
        }
    }

}
