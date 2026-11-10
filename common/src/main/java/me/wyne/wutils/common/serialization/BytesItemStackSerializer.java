package me.wyne.wutils.common.serialization;

import com.google.gson.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Base64;

/**
 * Gson (de)serializer that round-trips a Bukkit {@link ItemStack} through Paper's
 * {@link ItemStack#serializeAsBytes()}, base64-encoded into a single JSON string. Unlike
 * {@link Base64ItemStackSerializer} the data is compact and carries the data version, so the server
 * can upgrade it after a Minecraft update. Requires Paper at runtime and Gson on the classpath.
 *
 * <p>Register it on a {@code GsonBuilder} via
 * {@code registerTypeHierarchyAdapter(ItemStack.class, new BytesItemStackSerializer())}.</p>
 */
public class BytesItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    /**
     * Decodes the base64 JSON string back into an {@link ItemStack}.
     *
     * @throws JsonParseException if the encoded data is corrupt
     */
    @Override
    public @NotNull ItemStack deserialize(@NotNull JsonElement json, @NotNull Type typeOfT, @NotNull JsonDeserializationContext context) throws JsonParseException {
        try {
            return ItemStack.deserializeBytes(Base64.getDecoder().decode(json.getAsString()));
        } catch (RuntimeException e) {
            throw new JsonParseException("An exception occurred trying to deserialize ItemStack from bytes", e);
        }
    }

    @Override
    public @NotNull JsonElement serialize(@NotNull ItemStack src, @NotNull Type typeOfSrc, @NotNull JsonSerializationContext context) {
        return new JsonPrimitive(Base64.getEncoder().encodeToString(src.serializeAsBytes()));
    }

}
