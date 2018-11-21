package net.kleditzsch.SmartHome.view.automation.user;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.AutomationElement;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.VirtualSensorValue;
import net.kleditzsch.SmartHome.model.automation.device.sensor.virtual.*;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.DoubleSwitchable;
import net.kleditzsch.SmartHome.model.automation.device.switchable.Interface.Switchable;
import net.kleditzsch.SmartHome.model.automation.editor.RoomEditor;
import net.kleditzsch.SmartHome.model.automation.editor.SensorEditor;
import net.kleditzsch.SmartHome.model.automation.editor.SwitchableEditor;
import net.kleditzsch.SmartHome.model.automation.global.SwitchCommand;
import net.kleditzsch.SmartHome.model.automation.room.Interface.RoomElement;
import net.kleditzsch.SmartHome.model.automation.room.Room;
import net.kleditzsch.SmartHome.model.automation.room.element.ButtonElement;
import net.kleditzsch.SmartHome.model.automation.room.element.SensorElement;
import net.kleditzsch.SmartHome.model.automation.room.element.VirtualSensorElement;
import net.kleditzsch.SmartHome.model.global.options.SwitchCommands;
import net.kleditzsch.SmartHome.util.formatter.SensorValueFormatUtil;
import net.kleditzsch.SmartHome.util.sse.SseResponse;
import net.kleditzsch.SmartHome.util.sse.SseUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutomationSseSyncServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // SSE Antwortobjekt
        SseUtil sse = new SseUtil(resp);

        // 120 Datensätze senden und danach die Verbindung beenden (ca. alle 60 Sekunden)
        for(int i = 0; i < 120; i++) {

            boolean success = true, elementState = false;
            String message = "";
            Map<String, Boolean> buttonState = new HashMap<>();
            Map<String, Switchable.State> switchableState = new HashMap<>();
            Map<String, String> sensorState = new HashMap<>();
            Map<String, Map<String, String>> virtualSensorState = new HashMap<>();
            List<String> roomSensors = new ArrayList<>();

            try {

                //Liste mit ID und Status aller Doppelschaltelemente laden
                SwitchableEditor swe = Application.getInstance().getAutomation().getSwitchableEditor();
                ReentrantReadWriteLock.ReadLock sweLock = swe.readLock();
                sweLock.lock();

                swe.getData().forEach(switchable -> {

                    if(switchable instanceof DoubleSwitchable) {

                        switchableState.put(switchable.getId().get(), switchable.getState());
                    }
                });

                sweLock.unlock();

                //Liste mit ID und Wert aller Sensoren laden
                SensorEditor se = Application.getInstance().getAutomation().getSensorEditor();
                ReentrantReadWriteLock.ReadLock seLock = se.readLock();
                seLock.lock();

                se.getData().forEach(sensorValue -> {

                    if(sensorValue instanceof VirtualSensorValue) {

                        //Virtuelle Sensorwerte
                        if(sensorValue instanceof VirtualActualPowerValue) {

                            VirtualActualPowerValue vSensor = (VirtualActualPowerValue) sensorValue;
                            Map<String, String> values = new HashMap<>();
                            values.put("avg", SensorValueFormatUtil.format(vSensor.getAverageAsSensorValue()));
                            values.put("sum", SensorValueFormatUtil.format(vSensor.getSumAsSensorValue()));
                            virtualSensorState.put(vSensor.getId().get(), values);
                        } else if(sensorValue instanceof VirtualEnergyValue) {

                            VirtualEnergyValue vSensor = (VirtualEnergyValue) sensorValue;
                            Map<String, String> values = new HashMap<>();
                            values.put("avg", SensorValueFormatUtil.format(vSensor.getAverageAsSensorValue()));
                            values.put("sum", SensorValueFormatUtil.format(vSensor.getSumAsSensorValue()));
                            virtualSensorState.put(vSensor.getId().get(), values);
                        } else if(sensorValue instanceof VirtualGasAmountValue) {

                            VirtualGasAmountValue vSensor = (VirtualGasAmountValue) sensorValue;
                            Map<String, String> values = new HashMap<>();
                            values.put("sum", SensorValueFormatUtil.format(vSensor.getSumAsSensorValue()));
                            virtualSensorState.put(vSensor.getId().get(), values);
                        } else if(sensorValue instanceof VirtualLightIntensityValue) {

                            VirtualLightIntensityValue vSensor = (VirtualLightIntensityValue) sensorValue;
                            Map<String, String> values = new HashMap<>();
                            values.put("avg", SensorValueFormatUtil.format(vSensor.getAverageAsSensorValue()));
                            virtualSensorState.put(vSensor.getId().get(), values);
                        } else if(sensorValue instanceof VirtualTemperatureValue) {

                            VirtualTemperatureValue vSensor = (VirtualTemperatureValue) sensorValue;
                            Map<String, String> values = new HashMap<>();
                            values.put("avg", SensorValueFormatUtil.format(vSensor.getAverageAsSensorValue()));
                            values.put("min", SensorValueFormatUtil.format(vSensor.getMinAsSensorValue()));
                            values.put("max", SensorValueFormatUtil.format(vSensor.getMaxAsSensorValue()));
                            virtualSensorState.put(vSensor.getId().get(), values);
                        } else if(sensorValue instanceof VirtualWaterAmountValue) {

                            VirtualWaterAmountValue vSensor = (VirtualWaterAmountValue) sensorValue;
                            Map<String, String> values = new HashMap<>();
                            values.put("sum", SensorValueFormatUtil.format(vSensor.getSumAsSensorValue()));
                            virtualSensorState.put(vSensor.getId().get(), values);
                        }
                    } else {

                        //Sensorwerte
                        sensorState.put(sensorValue.getId().get(), SensorValueFormatUtil.format(sensorValue));
                    }
                });

                seLock.unlock();

                //Raum Elemente laden und den Status der elemente ermitteln
                ID roomId = ID.of(req.getParameter("roomid"));

                RoomEditor roomEditor = Application.getInstance().getAutomation().getRoomEditor();
                ReentrantReadWriteLock.ReadLock lock = roomEditor.readLock();
                lock.lock();

                //Raum laden
                Optional<Room> roomOptional = roomEditor.getById(roomId);
                if(roomOptional.isPresent()) {

                    //Raumelemente laden
                    List<RoomElement> roomElements = roomOptional.get().getRoomElements();
                    for (RoomElement roomElement : roomElements) {

                        //Status der Buttonelemente
                        if(roomElement instanceof ButtonElement && ((ButtonElement) roomElement).isDoubleButton()) {

                            elementState = true;
                            ButtonElement be = (ButtonElement) roomElement;
                            for(SwitchCommand switchCommand : be.getCommands()) {

                                if(switchableState.containsKey(switchCommand.getSwitchableId().get())) {

                                    if((switchCommand.getCommand() == SwitchCommands.on && switchableState.get(switchCommand.getSwitchableId().get()) == AutomationElement.State.OFF)
                                            || (switchCommand.getCommand() == SwitchCommands.off && switchableState.get(switchCommand.getSwitchableId().get()) == AutomationElement.State.ON)) {

                                        elementState = false;
                                    }
                                }
                            }

                            buttonState.put(be.getId().get(), elementState);
                        }

                        //Sensor ID's auflisten
                        if(roomElement instanceof VirtualSensorElement) {

                            VirtualSensorElement vSe = (VirtualSensorElement) roomElement;
                            vSe.getVirtualSensorId().ifPresent(id -> roomSensors.add(id.get()));
                        } else if(roomElement instanceof SensorElement) {

                            SensorElement sensorElement = (SensorElement) roomElement;
                            sensorElement.getFirstSensorValueId().ifPresent(id -> roomSensors.add(id.get()));
                            sensorElement.getSecondSensorValueId().ifPresent(id -> roomSensors.add(id.get()));
                            sensorElement.getThirdSensorValueId().ifPresent(id -> roomSensors.add(id.get()));
                        }
                    }

                } else {

                    success = false;
                    message = "Der Raum wurde nicht gefunden";
                }

                lock.unlock();

            } catch (Exception e) {

                success = false;
                message = "Feherhafte Eingaben";
            }

            //JSON erzeugen
            JsonObject jo = new JsonObject();
            jo.add("success", new JsonPrimitive(success));
            jo.add("message", new JsonPrimitive(message));

            //Button Elemente
            JsonArray buttons = new JsonArray();
            buttonState.forEach((id, state) -> {

                JsonObject entry = new JsonObject();
                entry.add("id", new JsonPrimitive(id));
                entry.add("state", new JsonPrimitive(state));
                buttons.add(entry);
            });
            jo.add("buttons", buttons);

            //Sensor Werte
            JsonArray sensors = new JsonArray();
            sensorState.forEach((id, value) -> {

                if(roomSensors.contains(id)) {

                    JsonObject entry = new JsonObject();
                    entry.add("id", new JsonPrimitive(id));
                    entry.add("value", new JsonPrimitive(value));
                    sensors.add(entry);
                }
            });
            jo.add("sensors", sensors);

            //Virtuelle Sensorwerte
            JsonArray vSensors = new JsonArray();
            virtualSensorState.forEach((id, values) -> {

                if(roomSensors.contains(id)) {

                    JsonObject entry = new JsonObject();
                    entry.add("id", new JsonPrimitive(id));
                    values.forEach((name, value) -> {

                        entry.add(name, new JsonPrimitive(value));
                    });
                    vSensors.add(entry);
                }
            });
            jo.add("vSensors", vSensors);

            String json = jo.toString();

            //Daten per SSE an den Browser senden
            SseResponse sseResponse = new SseResponse(json);
            sseResponse.setRetry(500);

            sse.sendResponse(sseResponse);

            try {

                Thread.sleep(500);
            } catch (InterruptedException e) {

                break;
            }
        }
    }
}
