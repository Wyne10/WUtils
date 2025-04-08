package me.wyne.wutils.common.serialization;

import com.google.gson.*;
import me.wyne.wutils.common.cooldown.Period;

import java.lang.reflect.Type;

public class PeriodSerializer implements JsonSerializer<Period>, JsonDeserializer<Period> {

    @Override
    public Period deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new Period(System.currentTimeMillis() + jsonElement.getAsLong());
    }

    @Override
    public JsonElement serialize(Period period, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(period.getRemaining());
    }

}
