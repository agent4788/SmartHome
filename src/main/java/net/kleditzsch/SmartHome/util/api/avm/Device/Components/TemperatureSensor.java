package net.kleditzsch.SmartHome.util.api.avm.Device.Components;

import net.kleditzsch.SmartHome.util.api.avm.FritzBoxHandler;

import java.io.IOException;

/**
 * Temperatursensor
 */
public class TemperatureSensor {

    /**
     * Gerätekennung
     */
    private String identifier = "";

    /**
     * Gerätehandler
     */
    private FritzBoxHandler fritzBoxHandler = null;

    /**
     * Temperatur [°C]
     */
    private double temperature = 0.0;

    /**
     * Offset [°C]
     */
    private double offset = 0.0;

    /**
     * @param identifier Gerätekennung
     * @param fritzBoxHandler Gerätehandler
     * @param temperature Temperatur [°C]
     * @param offset Offset [°C]
     */
    public TemperatureSensor(String identifier, FritzBoxHandler fritzBoxHandler, double temperature, double offset) {
        this.identifier = identifier;
        this.fritzBoxHandler = fritzBoxHandler;
        this.temperature = temperature;
        this.offset = offset;
    }

    /**
     * gibt die Temperatur zurück
     *
     * @return Temperatur [°C]
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * gibt das Offset zurück
     *
     * @return Offset [°C]
     */
    public double getOffset() {
        return offset;
    }

    /**
     * aktualisiert die aktuelle Leistung
     *
     * @return aktuelle Leistung [mW]
     */
    public double updateTemperature() throws IOException {

        try {

            String response = fritzBoxHandler.sendHttpRequest("webservices/homeautoswitch.lua?ain=" + identifier + "&switchcmd=gettemperature");
            temperature = Double.parseDouble(response.trim()) / 10;
        } catch (NumberFormatException | InterruptedException e) {

            temperature = 0.0;
        }
        return getTemperature();
    }
}
