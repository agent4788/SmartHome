package net.kleditzsch.applications.automation.model.device.sensor.Interface;

import com.google.common.base.Preconditions;
import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.automation.model.device.AutomationElement;

import java.time.LocalDateTime;

/**
 * Standardimplementierung
 */
public abstract class SensorValue extends AutomationElement {

    /**
     * Identifizierer
     */
    private String identifier = "";

    /**
     * Systemwert
     */
    private boolean systemValue = false;

    /**
     * Zeitpunkt des letzten Werte Pushes
     */
    private LocalDateTime lastPushTime;

    /**
     * Timeout (0 = deaktiviert)
     */
    private int timeout = 0;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public SensorValue(ID id, String identifier, String name) {

        setId(id);
        setIdentifier(identifier);
        setName(name);
    }

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     * @param timeout Timeout
     */
    public SensorValue(ID id, String identifier, String name, int timeout) {

        setId(id);
        setIdentifier(identifier);
        setName(name);
        setTimeout(timeout);
    }

    /**
     * gibt den Identifizierer zurück
     *
     * @return Identifizierer
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * setzt den Identifizierer
     *
     * @param identifier Identifizierer
     */
    public void setIdentifier(String identifier) {

        Preconditions.checkNotNull(identifier);
        Preconditions.checkArgument(identifier.matches("^[a-zA-Z0-9\\-]{3,50}$"), "Ungültige Identifizierung %s", identifier);
        this.identifier = identifier;
        setChangedData();
    }

    /**
     * gibt an ob der Sensorwert ein Systemwert ist (kann nicht gelöscht werden)
     *
     * @return true wenn Systemwert
     */
    public boolean isSystemValue() {
        return this.systemValue;
    }

    /**
     * setzt den Sensorwert als Systemwert
     *
     * @param systemValue Systemwert
     */
    public void setSystemValue(boolean systemValue) {

        this.systemValue = systemValue;
        setChangedData();
    }

    /**
     * gibt die Zeit des letzten Pushs zurück
     *
     * @return Zeit
     */
    public LocalDateTime getLastPushTime() {
        return this.lastPushTime;
    }

    /**
     * setzt die Zeit des letzen Pushs
     *
     * @param lastPushTime Zeit
     */
    public void setLastPushTime(LocalDateTime lastPushTime) {

        this.lastPushTime = lastPushTime;
        setChangedData();
    }

    /**
     * aktualisiert die Zeit des letzten Pushes auf die aktuelle Zeit
     */
    public void updateLastPushTime() {

        this.lastPushTime = LocalDateTime.now();
        setChangedData();
    }

    /**
     * gibt den Timeout zurück
     *
     * @return Timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * setzt den Timeout
     *
     * @param timeout Timeout
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * gibt an ob der Timeout aktiviert ist
     *
     * @return Timeout aktiv
     */
    public boolean isTimeoutActivated() {

        return timeout > 0;
    }

    /**
     * gibt an ob die Timeout Zeit überschritten wurde
     *
     * @return Timeout Zeit überschritten (wenn deaktiviert immer false)
     */
    public boolean isTimeoutExceeded() {

        //prüfen ob Timeout aktiviert
        if(isTimeoutActivated()) {

            //prüfen ob Timeout abgelaufen
            LocalDateTime timeoutTime = LocalDateTime.now().minusSeconds(timeout);
            return lastPushTime.isBefore(timeoutTime);
        }
        return false;
    }
}
