package me.wyne.wutils.common.serialization;

import com.google.gson.*;
import me.wyne.wutils.common.cooldown.CooldownMap;

import java.lang.reflect.Type;
import java.util.Map;

public class CooldownMapSerializer<T> implements JsonSerializer<CooldownMap<T>>, JsonDeserializer<CooldownMap<T>> {

    private final Class<T> keyClass;

    public CooldownMapSerializer(Class<T> keyClass) {
        this.keyClass = keyClass;
    }

    @Override
    public CooldownMap<T> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
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
    public JsonElement serialize(CooldownMap<T> cooldownMap, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();

        for (T key : cooldownMap.getMap().keySet()) {
            if (!cooldownMap.isCooldowned(key))
                continue;
            jsonObject.add(key.toString(), new JsonPrimitive(cooldownMap.getRemaining(key)));
        }

        return jsonObject;
    }

}
