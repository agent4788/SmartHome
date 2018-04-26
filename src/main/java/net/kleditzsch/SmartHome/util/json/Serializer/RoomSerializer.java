package net.kleditzsch.SmartHome.util.json.Serializer;

import com.google.gson.*;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.room.Interface.RoomElement;
import net.kleditzsch.SmartHome.model.automation.global.SwitchCommand;
import net.kleditzsch.SmartHome.model.automation.room.Room;
import net.kleditzsch.SmartHome.model.automation.room.element.ButtonElement;
import net.kleditzsch.SmartHome.model.automation.room.element.SensorElement;

import java.lang.reflect.Type;
import java.util.List;

public class RoomSerializer implements JsonSerializer<Room>, JsonDeserializer<Room> {

    @Override
    public Room deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jo = json.getAsJsonObject();
        Room room = new Room();

        //Raum Daten
        room.setId(ID.of(jo.get("id").getAsString()));
        room.setName(jo.get("name").getAsString());
        room.setDescription(jo.get("description").getAsString());
        room.setOrderId(jo.get("orderId").getAsInt());
        room.setDisabled(jo.get("disabled").getAsBoolean());
        room.setIconFile(jo.get("iconFile").getAsString());

        //Raum elemente
        JsonArray ja = jo.get("elements").getAsJsonArray();
        for(int i = 0; i < ja.size(); i++) {

            JsonObject joRe = ja.get(i).getAsJsonObject();
            RoomElement.Type type = context.deserialize(joRe.get("type"), RoomElement.Type.class);

            switch (type) {

                case BUTTON_ELEMENT:

                    ButtonElement be = new ButtonElement();

                    //Allgemeine Daten
                    be.setId(ID.of(joRe.get("id").getAsString()));
                    be.setName(joRe.get("name").getAsString());
                    be.setDescription(joRe.get("description").getAsString());
                    be.setOrderId(joRe.get("orderId").getAsInt());
                    be.setDisabled(joRe.get("disabled").getAsBoolean());
                    be.setIconFile(joRe.get("iconFile").getAsString());

                    //Schaltbefehle
                    JsonArray jaRe = joRe.get("commands").getAsJsonArray();
                    for(int j = 0; j < jaRe.size(); j++) {

                        be.getCommands().add(context.deserialize(jaRe.get(j), SwitchCommand.class));
                    }

                    room.getRoomElements().add(be);
                    break;
                case SENSOR_ELEMENT:

                    SensorElement se = new SensorElement();

                    //Allgemeine Daten
                    se.setId(ID.of(joRe.get("id").getAsString()));
                    se.setName(joRe.get("name").getAsString());
                    se.setDescription(joRe.get("description").getAsString());
                    se.setOrderId(joRe.get("orderId").getAsInt());
                    se.setDisabled(joRe.get("disabled").getAsBoolean());
                    se.setIconFile(joRe.get("iconFile").getAsString());

                    //Sensor IDs
                    se.setFirstSensorValueId(ID.of(joRe.get("firstSensorValueId").getAsString()));
                    se.setSecondSensorValueId(ID.of(joRe.get("secondSensorValueId").getAsString()));
                    se.setThirdSensorValueId(ID.of(joRe.get("thirdSensorValueId").getAsString()));

                    room.getRoomElements().add(se);
                    break;
            }
        }

        return room;
    }

    @Override
    public JsonElement serialize(Room src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jo = new JsonObject();

        //Raum Daten
        jo.add("id", new JsonPrimitive(src.getId().get()));
        jo.add("name", new JsonPrimitive(src.getName()));
        jo.add("description", new JsonPrimitive(src.getDescription().isPresent() ? src.getDescription().get() : ""));
        jo.add("orderId", new JsonPrimitive(src.getOrderId()));
        jo.add("disabled", new JsonPrimitive(src.isDisabled()));
        jo.add("iconFile", new JsonPrimitive(src.getIconFile()));

        //Raum Elemente
        JsonArray ja = new JsonArray();
        List<RoomElement> roomElements = src.getRoomElements();
        for(RoomElement re : roomElements) {

            JsonObject joRe = new JsonObject();
            joRe.add("id", new JsonPrimitive(re.getId().get()));
            joRe.add("name", new JsonPrimitive(re.getName()));
            joRe.add("description", new JsonPrimitive(re.getDescription().isPresent() ? re.getDescription().get() : ""));
            joRe.add("orderId", new JsonPrimitive(re.getOrderId()));
            joRe.add("disabled", new JsonPrimitive(re.isDisabled()));
            joRe.add("iconFile", new JsonPrimitive(re.getIconFile()));
            joRe.add("type", context.serialize(re.getType()));

            if(re instanceof ButtonElement) {

                //Button Element
                ButtonElement be = (ButtonElement) re;
                JsonArray commands = new JsonArray();
                for (SwitchCommand sc : be.getCommands()) {

                    JsonElement command = context.serialize(sc);
                    commands.add(command);
                }
                joRe.add("commands", commands);

            } else if(re instanceof SensorElement) {

                //Sensor Element
                SensorElement se = (SensorElement) re;
                joRe.add("firstSensorValueId", new JsonPrimitive(se.getFirstSensorValueId().isPresent() ? se.getFirstSensorValueId().get().toString() : ""));
                joRe.add("secondSensorValueId", new JsonPrimitive(se.getSecondSensorValueId().isPresent() ? se.getSecondSensorValueId().get().toString() : ""));
                joRe.add("thirdSensorValueId", new JsonPrimitive(se.getThirdSensorValueId().isPresent() ? se.getThirdSensorValueId().get().toString() : ""));
            }
            ja.add(joRe);
        }
        jo.add("elements", ja);

        return jo;
    }
}
