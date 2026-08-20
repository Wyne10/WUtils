package me.wyne.wutils.common.serialization;

import com.google.gson.*;
import me.wyne.wutils.common.cooldown.Period;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

/**
 * Gson (de)serializer for {@link Period}, encoding it as the remaining duration in milliseconds
 * at the time of serialization. Requires Gson on the classpath.
 */
public class PeriodSerializer implements JsonSerializer<Period>, JsonDeserializer<Period> {

    /** Reconstructs a {@link Period} whose remaining duration counts down from the encoded millisecond value, starting now. */
    @Override
    public @NotNull Period deserialize(@NotNull JsonElement jsonElement, @NotNull Type type, @NotNull JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return new Period(System.currentTimeMillis() + jsonElement.getAsLong());
    }

    @Override
    public @NotNull JsonElement serialize(@NotNull Period period, @NotNull Type type, @NotNull JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(period.getRemaining());
    }

}
