package net.kleditzsch.smarthome.utility.json.Serializer;

import com.google.gson.*;
import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.automation.model.global.SwitchCommand;
import net.kleditzsch.applications.automation.model.room.Interface.RoomElement;
import net.kleditzsch.applications.automation.model.room.Room;
import net.kleditzsch.applications.automation.model.room.element.ButtonElement;
import net.kleditzsch.applications.automation.model.room.element.DividerElement;
import net.kleditzsch.applications.automation.model.room.element.SensorElement;
import net.kleditzsch.applications.automation.model.room.element.VirtualSensorElement;

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
        room.setDisplayText(jo.get("displayText").getAsString());
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
                    be.setDisplayText(joRe.get("displayText").getAsString());
                    be.setOrderId(joRe.get("orderId").getAsInt());
                    be.setDisabled(joRe.get("disabled").getAsBoolean());
                    be.setIconFile(joRe.get("iconFile").getAsString());

                    //Sicherheitsabfrage
                    be.setSafetyRequestEnabled(joRe.get("safetyRequestEnabled").getAsBoolean());
                    be.setSafetyRequestIcon(joRe.get("safetyRequestIcon").getAsString());
                    be.setSafetyRequestHeaderText(joRe.get("safetyRequestHeaderText").getAsString());
                    be.setSafetyRequestText(joRe.get("safetyRequestText").getAsString());
                    be.setSafetyRequestExecuteButtonText(joRe.get("safetyRequestExecuteButtonText").getAsString());
                    be.setSafetyRequestCancelButtonText(joRe.get("safetyRequestCancelButtonText").getAsString());

                    //sonstiges
                    be.setOnButtonText(joRe.get("onButtonText").getAsString());
                    be.setOffButtonText(joRe.get("offButtonText").getAsString());
                    be.setDoubleButton(joRe.get("isDoubleButton").getAsBoolean());

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
                    se.setDisplayText(joRe.get("displayText").getAsString());
                    se.setOrderId(joRe.get("orderId").getAsInt());
                    se.setDisabled(joRe.get("disabled").getAsBoolean());
                    se.setIconFile(joRe.get("iconFile").getAsString());

                    //Sensor IDs
                    se.setFirstSensorValueId(ID.of(joRe.get("firstSensorValueId").getAsString()));
                    se.setSecondSensorValueId(ID.of(joRe.get("secondSensorValueId").getAsString()));
                    se.setThirdSensorValueId(ID.of(joRe.get("thirdSensorValueId").getAsString()));

                    room.getRoomElements().add(se);
                    break;

                case VIRTUAL_SENSOR_ELEMENT:

                    VirtualSensorElement ve = new VirtualSensorElement();

                    //Allgemeine Daten
                    ve.setId(ID.of(joRe.get("id").getAsString()));
                    ve.setName(joRe.get("name").getAsString());
                    ve.setDescription(joRe.get("description").getAsString());
                    ve.setDisplayText(joRe.get("displayText").getAsString());
                    ve.setOrderId(joRe.get("orderId").getAsInt());
                    ve.setDisabled(joRe.get("disabled").getAsBoolean());
                    ve.setIconFile(joRe.get("iconFile").getAsString());

                    //ID des Virtuellen Sensors
                    ve.setVirtualSensorId(ID.of(joRe.get("virtualSensorId").getAsString()));

                    room.getRoomElements().add(ve);
                    break;
                case DIVIDER_ELEMENT:

                    DividerElement de = new DividerElement();

                    //Allgemeine Daten
                    de.setId(ID.of(joRe.get("id").getAsString()));
                    de.setName(joRe.get("name").getAsString());
                    de.setDescription(joRe.get("description").getAsString());
                    de.setDisplayText(joRe.get("displayText").getAsString());
                    de.setOrderId(joRe.get("orderId").getAsInt());
                    de.setDisabled(joRe.get("disabled").getAsBoolean());
                    de.setIconFile(joRe.get("iconFile").getAsString());

                    //Darstellung
                    de.setIcon(joRe.get("icon").getAsString());

                    room.getRoomElements().add(de);
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
        jo.add("displayText", new JsonPrimitive(src.getDisplayText()));
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
            joRe.add("displayText", new JsonPrimitive(re.getDisplayText()));
            joRe.add("orderId", new JsonPrimitive(re.getOrderId()));
            joRe.add("disabled", new JsonPrimitive(re.isDisabled()));
            joRe.add("iconFile", new JsonPrimitive(re.getIconFile()));
            joRe.add("type", context.serialize(re.getType()));

            if(re instanceof ButtonElement) {

                //Button Element
                ButtonElement be = (ButtonElement) re;

                joRe.add("safetyRequestEnabled", new JsonPrimitive(be.isSafetyRequestEnabled()));
                joRe.add("safetyRequestIcon", new JsonPrimitive(be.getSafetyRequestIcon()));
                joRe.add("safetyRequestHeaderText", new JsonPrimitive(be.getSafetyRequestHeaderText()));
                joRe.add("safetyRequestText", new JsonPrimitive(be.getSafetyRequestText()));
                joRe.add("safetyRequestExecuteButtonText", new JsonPrimitive(be.getSafetyRequestExecuteButtonText()));
                joRe.add("safetyRequestCancelButtonText", new JsonPrimitive(be.getSafetyRequestCancelButtonText()));

                joRe.add("onButtonText", new JsonPrimitive(be.getOnButtonText()));
                joRe.add("offButtonText", new JsonPrimitive(be.getOffButtonText()));
                joRe.add("isDoubleButton", new JsonPrimitive(be.isDoubleButton()));

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
            } else if(re instanceof VirtualSensorElement) {

                //Virtual Sensor Element
                VirtualSensorElement ve = (VirtualSensorElement) re;
                joRe.add("virtualSensorId", new JsonPrimitive(ve.getVirtualSensorId().isPresent() ? ve.getVirtualSensorId().get().toString() : ""));
            } else if(re instanceof DividerElement) {

                //Divider Element
                DividerElement de = (DividerElement) re;
                joRe.add("icon", new JsonPrimitive(de.getIcon().isPresent() ? de.getIcon().get() : ""));
            }
            ja.add(joRe);
        }
        jo.add("elements", ja);

        return jo;
    }
}
