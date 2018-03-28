package net.kleditzsch.SmartHome.util.json.Serializer;

import com.google.gson.*;
import net.kleditzsch.SmartHome.global.base.ID;

import java.lang.reflect.Type;

public class IdSerializer implements JsonSerializer<ID>, JsonDeserializer<ID> {

    @Override
    public ID deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        String uuid = jsonElement.getAsString();
        return ID.of(uuid);
    }

    @Override
    public JsonElement serialize(ID src, Type typeOfSrc, JsonSerializationContext context) {

        String uuid = src.get();
        return new JsonPrimitive(uuid);
    }
}
