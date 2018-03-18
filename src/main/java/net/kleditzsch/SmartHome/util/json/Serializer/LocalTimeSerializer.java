package net.kleditzsch.SmartHome.util.json.Serializer;

import com.google.gson.*;
import net.kleditzsch.SmartHome.util.datetime.DatabaseDateTimeUtil;

import java.lang.reflect.Type;
import java.time.LocalTime;

/**
 * Serialisiert Datumsobjekte
 */
public class LocalTimeSerializer implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {

    @Override
    public LocalTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        String ldtStr = jsonElement.getAsString();
        return DatabaseDateTimeUtil.parseTimeFromDatabase(ldtStr);
    }

    @Override
    public JsonElement serialize(LocalTime localTime, Type type, JsonSerializationContext jsonSerializationContext) {

        String ldtStr = DatabaseDateTimeUtil.getDatabaseTimeStr(localTime);
        return new JsonPrimitive(ldtStr);
    }
}
