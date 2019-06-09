package net.kleditzsch.SmartHome.util.api.avm.Device.Components;

import net.kleditzsch.SmartHome.util.api.avm.FritzBoxHandler;

import java.io.IOException;

/**
 * Energiemesser
 */
public class PowerMeter {

    /**
     * Gerätekennung
     */
    private String identifier = "";

    /**
     * Gerätehandler
     */
    private FritzBoxHandler fritzBoxHandler = null;

    /**
     * aktuelle Leistung [mW]
     */
    private double power = 0.0;

    /**
     * gesamter Energieverbrauch seit Inbetriebnahme [Wh]
     */
    private double energy = 0.0;

    /**
     * @param identifier Gerätekennung
     * @param fritzBoxHandler Verbindungshandler
     * @param power aktuelle Leistung [mW]
     * @param energy gesamter Energieverbrauch seit Inbetriebnahme [Wh]
     */
    public PowerMeter(String identifier, FritzBoxHandler fritzBoxHandler, double power, double energy) {
        this.identifier = identifier;
        this.fritzBoxHandler = fritzBoxHandler;
        this.power = power;
        this.energy = energy;
    }

    /**
     * gibt die aktuelle Leistung zurück
     *
     * @return aktuelle Leistung [mW]
     */
    public double getPower() {
        return power;
    }

    /**
     * gibt den gesamten Energieverbrauch seit Inbetriebnahme zurück
     *
     * @return gesamter Energieverbrauch seit Inbetriebnahme [Wh]
     */
    public double getEnergy() {
        return energy;
    }

    /**
     * aktualisiert die aktuelle Leistung
     *
     * @return aktuelle Leistung [mW]
     */
    public double updatePower() throws IOException {

        try {

            String response = fritzBoxHandler.sendHttpRequest("webservices/homeautoswitch.lua?ain=" + identifier + "&switchcmd=getswitchpower");
            power = Double.parseDouble(response.trim());
        } catch (NumberFormatException | InterruptedException e) {

            power = 0.0;
        }
        return getPower();
    }

    /**
     * aktualisiert die aktuelle Leistung
     *
     * @return aktuelle Leistung [mW]
     */
    public double updateEnergy() throws IOException {

        try {

            String response = fritzBoxHandler.sendHttpRequest("webservices/homeautoswitch.lua?ain=" + identifier + "&switchcmd=getswitchenergy");
            energy = Double.parseDouble(response.trim());
        } catch (NumberFormatException | InterruptedException e) {

            energy = 0.0;
        }
        return getEnergy();
    }
}
