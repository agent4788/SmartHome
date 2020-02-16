package net.kleditzsch.apps.automation.api.avm.Device.Components;

import net.kleditzsch.apps.automation.api.avm.FritzBoxHandler;

/**
 * Alarm
 */
public class Alert {

    /**
     * Gerätekennung
     */
    private String identifier = "";

    /**
     * Gerätehandler
     */
    private FritzBoxHandler fritzBoxHandler = null;

    /**
     * Alarm aktiv
     */
    private boolean alert = false;

    /**
     * @param identifier Gerätekennung
     * @param fritzBoxHandler Gerätehandler
     * @param alert Alarm aktiv
     */
    public Alert(String identifier, FritzBoxHandler fritzBoxHandler, boolean alert) {
        this.identifier = identifier;
        this.fritzBoxHandler = fritzBoxHandler;
        this.alert = alert;
    }

    /**
     * gibt an ob der Alarm aktiv ist
     *
     * @return Alarm aktiv
     */
    public boolean isAlert() {
        return alert;
    }
}
