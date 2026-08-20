package me.wyne.wutils.common.serialization;

import com.google.gson.*;
import me.wyne.wutils.common.cooldown.CooldownMap;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Gson (de)serializer for {@link CooldownMap}, encoding it as a JSON object of
 * {@code key -> remaining-millis} entries. Only keys currently on cooldown are serialized.
 * Requires Gson on the classpath.
 *
 * @param <T> the cooldown key type, deserialized via {@code keyClass}
 */
public class CooldownMapSerializer<T> implements JsonSerializer<CooldownMap<T>>, JsonDeserializer<CooldownMap<T>> {

    private final Class<T> keyClass;

    /** @param keyClass the class Gson uses to deserialize each JSON object key back into {@code T} */
    public CooldownMapSerializer(@NotNull Class<T> keyClass) {
        this.keyClass = keyClass;
    }

    @Override
    public @NotNull CooldownMap<T> deserialize(@NotNull JsonElement jsonElement, @NotNull Type type, @NotNull JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        CooldownMap<T> cooldownMap = new CooldownMap<>();
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            T key = jsonDeserializationContext.deserialize(new JsonPrimitive(entry.getKey()), keyClass);
            long remaining = entry.getValue().getAsLong();
            cooldownMap.put(key, remaining);
        }

        return cooldownMap;
    }

    @Override
    public @NotNull JsonElement serialize(@NotNull CooldownMap<T> cooldownMap, @NotNull Type type, @NotNull JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        for (T key : cooldownMap.getMap().keySet()) {
            if (!cooldownMap.isCooldowned(key))
                continue;
            jsonObject.add(key.toString(), new JsonPrimitive(cooldownMap.getRemaining(key)));
        }

        return jsonObject;
    }

}
