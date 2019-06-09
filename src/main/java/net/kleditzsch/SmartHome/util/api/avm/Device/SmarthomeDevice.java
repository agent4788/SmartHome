package net.kleditzsch.SmartHome.util.api.avm.Device;

import net.kleditzsch.SmartHome.util.api.avm.Device.Components.*;
import net.kleditzsch.SmartHome.util.api.avm.FritzBoxHandler;

import java.io.IOException;
import java.util.Optional;

/**
 * Smarthome Gerät
 */
public class SmarthomeDevice {

    /**
     * Identifizierer
     */
    private String identifier = "";

    /**
     * Gerätehandler
     */
    private FritzBoxHandler fritzBoxHandler = null;

    /**
     * Firmware Version
     */
    private String firmwareVersion = "";

    /**
     * Hersteller
     */
    private String manufacturer = "";

    /**
     * Hersteller Gerätename
     */
    private String productName = "";

    /**
     * Funktionsmaske
     */
    private Integer functionBitmask = 0;

    /**
     * Gerät erreichbar
     */
    private boolean present = false;

    /**
     * Gerätename
     */
    private String name = "";

    /**
     * Meldung
     */
    private Alert alert = null;

    /**
     * Heizkörperregler
     */
    private HKR hkr = null;

    /**
     * Leistungsmesser
     */
    private PowerMeter powerMeter = null;

    /**
     * Schalter
     */
    private Switch aSwitch = null;

    /**
     * Temperatursensor
     */
    private TemperatureSensor temperatureSensor = null;

    public SmarthomeDevice(String identifier,
                           FritzBoxHandler fritzBoxHandler,
                           String firmwareVersion,
                           String manufacturer,
                           String productName,
                           Integer functionBitmask,
                           boolean present,
                           String name,
                           Alert alert,
                           HKR hkr,
                           PowerMeter powerMeter,
                           Switch aSwitch,
                           TemperatureSensor temperatureSensor) {
        this.identifier = identifier;
        this.fritzBoxHandler = fritzBoxHandler;
        this.firmwareVersion = firmwareVersion;
        this.manufacturer = manufacturer;
        this.productName = productName;
        this.functionBitmask = functionBitmask;
        this.present = present;
        this.name = name;
        this.alert = alert;
        this.hkr = hkr;
        this.powerMeter = powerMeter;
        this.aSwitch = aSwitch;
        this.temperatureSensor = temperatureSensor;
    }

    /**
     * prüft ob das Gerät einen Alarmsensor enthält
     *
     * @return Alarmsensor
     */
    public boolean isAlarmSensor() {
        return (functionBitmask & 16) == 16;
    }

    /**
     * prüft ob das Gerät einen Heizkörperregler enthält
     *
     * @return Heizkörperregler
     */
    public boolean isHkr() {
        return (functionBitmask & 64) == 64;
    }

    /**
     * prüft ob das Gerät einen Energiemesser enthält
     *
     * @return Energiemesser
     */
    public boolean isEnergyMeter() {
        return (functionBitmask & 128) == 128;
    }

    /**
     * prüft ob das Gerät einen Temperatursensor enthält
     *
     * @return Temperatursensor
     */
    public boolean isTemeratureSensor() {
        return (functionBitmask & 256) == 256;
    }

    /**
     * prüft ob das Gerät einen Schalter enthält
     *
     * @return Schalter
     */
    public boolean isSwitchSocket() {
        return (functionBitmask & 512) == 512;
    }

    /**
     * prüft ob das Gerät ein DECT Repeater ist
     *
     * @return DECT Repeater
     */
    public boolean isDectRepeater() {
        return (functionBitmask & 1024) == 1024;
    }

    /**
     * gibt die eindeutige Kennung zurück
     *
     * @return eindeutige Kennung
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * gibt die Firmwareversion des Gerätes zurück
     *
     * @return Firmwareversion
     */
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    /**
     * gibt den Hersteller des Gerätes zurück
     *
     * @return Hersteller
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * gibt den Gerätenamen des Herstellers zurück
     *
     * @return Gerätenamen des Herstellers
     */
    public String getProductName() {
        return productName;
    }

    /**
     * gibt die Funktionskennung zurück
     *
     * @return Funktionskennung
     */
    public Integer getFunctionBitmask() {
        return functionBitmask;
    }

    /**
     * gibt an ob das Gerät erreichbar ist
     *
     * @return Gerät erreichbar
     */
    public boolean isPresent() {
        return present;
    }

    /**
     * gibt den Namen des Gerätes zurück
     *
     * @return Name des Gerätes
     */
    public String getName() {
        return name;
    }

    /**
     * gibt das Objekt des Alarmsensors zurück
     *
     * @return Alarmsensor
     */
    public Optional<Alert> getAlert() {
        return Optional.ofNullable(alert);
    }

    /**
     * gibt das Objekt des Heizkörperreglers zurück
     *
     * @return Heizkörperregler
     */
    public Optional<HKR> getHkr() {
        return Optional.ofNullable(hkr);
    }

    /**
     * gibt das Objekt des Leistungsmessers zurück
     *
     * @return Leistungsmesser
     */
    public Optional<PowerMeter> getPowerMeter() {
        return Optional.ofNullable(powerMeter);
    }

    /**
     * gibt das Objekt des Schalters zurück
     *
     * @return Schalter
     */
    public Optional<Switch> getSwitch() {
        return Optional.ofNullable(aSwitch);
    }

    /**
     * gibt das Objekt des Temperatursensors zurück
     *
     * @return Temperatursensor
     */
    public Optional<TemperatureSensor> getTemperatureSensor() {
        return Optional.ofNullable(temperatureSensor);
    }

    /**
     * aktualisiert den Verbindungsstatus
     *
     * @return neuer Status
     */
    public boolean updatePresent() throws IOException {

        try {

            String response = fritzBoxHandler.sendHttpRequest("webservices/homeautoswitch.lua?ain=" + identifier + "&switchcmd=getswitchpresent");
            if(response.equals("1")) {

                present = true;
            } else {

                present = false;
            }
            return isPresent();
        } catch (InterruptedException e) {

            return false;
        }
    }
}
