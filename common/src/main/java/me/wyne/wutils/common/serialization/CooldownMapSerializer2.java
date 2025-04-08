package me.wyne.wutils.common.serialization;

import com.google.gson.*;
import me.wyne.wutils.common.cooldown.CooldownMap;

import java.lang.reflect.Type;
import java.util.Map;

class CooldownMapSerializer2<T> implements JsonSerializer<CooldownMap<T>>, JsonDeserializer<CooldownMap<T>> {

    private final Class<T> keyClass;

    public CooldownMapSerializer2(Class<T> keyClass) {
        this.keyClass = keyClass;
    }

    private static class Entry<T> {
        T key;
        long remaining;

        Entry(T key, long remaining) {
            this.key = key;
            this.remaining = remaining;
        }
    }

    @Override
    public JsonElement serialize(CooldownMap<T> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        long now = System.currentTimeMillis();

        for (Map.Entry<T, Long> entry : src.getMap().entrySet()) {
            JsonObject obj = new JsonObject();
            obj.add("key", context.serialize(entry.getKey(), keyClass));
            long remaining = Math.max(0, entry.getValue() - now);
            obj.addProperty("remaining", remaining);
            array.add(obj);
        }

        return array;
    }

    @Override
    public CooldownMap<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        CooldownMap<T> cooldownMap = new CooldownMap<>();
        long now = System.currentTimeMillis();

        JsonArray array = json.getAsJsonArray();
        for (JsonElement element : array) {
            JsonObject obj = element.getAsJsonObject();
            T key = context.deserialize(obj.get("key"), keyClass);
            long remaining = obj.get("remaining").getAsLong();
            cooldownMap.put(key, now + remaining);
        }

        return cooldownMap;
    }

}
