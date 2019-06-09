package net.kleditzsch.SmartHome.util.api.avm.Device.Components;

import net.kleditzsch.SmartHome.util.api.avm.FritzBoxHandler;

import java.io.IOException;

/**
 * Schalter
 */
public class Switch {

    /**
     * Status
     */
    public enum STATE {
        ON, OFF
    }

    /**
     * Modus
     */
    public enum MODE {
        AUTO, MANUALLY
    }

    /**
     * Gerätekennung
     */
    private String identifier = "";

    /**
     * Gerätehandler
     */
    private FritzBoxHandler fritzBoxHandler = null;

    /**
     * Status
     */
    private STATE state = STATE.OFF;

    /**
     * Modus
     */
    private MODE mode = MODE.MANUALLY;

    /**
     * schalten des Gerätes gesperrt
     */
    private boolean lock = false;

    /**
     * schalten am Gerät gesperrt
     */
    private boolean deviceLock = false;

    /**
     * @param identifier Gerätekennung
     * @param fritzBoxHandler Verbindungshandler
     * @param state Status
     * @param mode Modus
     * @param lock schalten gesperrt
     * @param deviceLock schalten am Gerät gesperrt
     */
    public Switch(String identifier, FritzBoxHandler fritzBoxHandler, STATE state, MODE mode, boolean lock, boolean deviceLock) {
        this.identifier = identifier;
        this.fritzBoxHandler = fritzBoxHandler;
        this.state = state;
        this.mode = mode;
        this.lock = lock;
        this.deviceLock = deviceLock;
    }

    /**
     * gibt den aktuellen Schaltstatus zurück
     *
     * @return Schaltstatus
     */
    public STATE getState() {
        return state;
    }

    /**
     * gibt den Modus zurück
     *
     * @return Modus
     */
    public MODE getMode() {
        return mode;
    }

    /**
     * gibt an ob das Gerät zum schalten gesperrt ist
     *
     * @return schalten gesperrt
     */
    public boolean isLock() {
        return lock;
    }

    /**
     * gibt an ob das schalten am Gerät gesperrt ist
     *
     * @return schalten am Gerät gesperrt
     */
    public boolean isDeviceLock() {
        return deviceLock;
    }

    /**
     * aktualisiert den Schaltstatus des Gerätes
     *
     * @return Schaltstatus
     */
    public STATE updateState() throws IOException {

        try {

            String response = fritzBoxHandler.sendHttpRequest("webservices/homeautoswitch.lua?ain=" + identifier + "&switchcmd=getswitchstate");
            if(response.trim().equals("1")) {

                state = STATE.ON;
            } else {

                state = STATE.OFF;
            }
            return getState();
        } catch (InterruptedException e) {

            state = STATE.OFF;
            return getState();
        }
    }

    /**
     * sendet den Befehl zum einschalten des Schalters
     *
     * @return neuer Status
     */
    public STATE switchOn() throws IOException {

        try {

            String response = fritzBoxHandler.sendHttpRequest("webservices/homeautoswitch.lua?ain=" + identifier + "&switchcmd=setswitchon");
            if(response.trim().equals("1")) {

                state = STATE.ON;
            } else {

                state = STATE.OFF;
            }
            return getState();
        } catch (InterruptedException e) {

            state = STATE.OFF;
            return getState();
        }
    }

    /**
     * sendet den Befehl zum ausschalten des Schalters
     *
     * @return neuer Status
     */
    public STATE switchOff() throws IOException {

        try {

            String response = fritzBoxHandler.sendHttpRequest("webservices/homeautoswitch.lua?ain=" + identifier + "&switchcmd=setswitchoff");
            if(response.trim().equals("1")) {

                state = STATE.ON;
            } else {

                state = STATE.OFF;
            }
            return getState();
        } catch (InterruptedException e) {

            state = STATE.OFF;
            return getState();
        }
    }

    /**
     * sendet den Befehl zum umschalten des Schalters
     *
     * @return neuer Status
     */
    public STATE switchToggle() throws IOException {

        try {

            String response = fritzBoxHandler.sendHttpRequest("webservices/homeautoswitch.lua?ain=" + identifier + "&switchcmd=setswitchtoggle");;
            if(response.trim().equals("1")) {

                state = STATE.ON;
            } else {

                state = STATE.OFF;
            }
            return getState();
        } catch (InterruptedException e) {

            return STATE.OFF;
        }
    }
}
