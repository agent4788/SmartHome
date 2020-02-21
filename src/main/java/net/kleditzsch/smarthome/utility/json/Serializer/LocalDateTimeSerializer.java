package net.kleditzsch.smarthome.utility.json.Serializer;

import com.google.gson.*;
import net.kleditzsch.smarthome.utility.datetime.DatabaseDateTimeUtil;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * Serialisiert Datumsobjekte
 */
public class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        String ldtStr = jsonElement.getAsString();
        return DatabaseDateTimeUtil.parseDateTimeFromDatabase(ldtStr);
    }

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {

        String ldtStr = DatabaseDateTimeUtil.getDatabaseDateTimeStr(localDateTime);
        return new JsonPrimitive(ldtStr);
    }
}
