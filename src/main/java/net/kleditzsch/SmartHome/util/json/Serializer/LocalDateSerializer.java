package net.kleditzsch.SmartHome.util.json.Serializer;

import com.google.gson.*;
import net.kleditzsch.SmartHome.util.datetime.DatabaseDateTimeUtil;

import java.lang.reflect.Type;
import java.time.LocalDate;

/**
 * Serialisiert Datumsobjekte
 */
public class LocalDateSerializer implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        String ldtStr = jsonElement.getAsString();
        return DatabaseDateTimeUtil.parseDateFromDatabase(ldtStr);
    }

    @Override
    public JsonElement serialize(LocalDate localDate, Type type, JsonSerializationContext jsonSerializationContext) {

        String ldtStr = DatabaseDateTimeUtil.getDatabaseDateStr(localDate);
        return new JsonPrimitive(ldtStr);
    }
}
