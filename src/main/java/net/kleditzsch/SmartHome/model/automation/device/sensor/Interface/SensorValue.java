package net.kleditzsch.SmartHome.model.automation.device.sensor.Interface;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.AutomationElement;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidatePattern;

import java.time.LocalDateTime;

/**
 * Standardimplementierung
 */
public abstract class SensorValue extends AutomationElement {

    /**
     * Identifizierer
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidatePattern(value = "^[a-zA-Z0-9\\-]{3,50}$", errorCode = 10008, message = "Ungültige Identifizierung")
    private String identifier = "";

    /**
     * Systemwert
     */
    private boolean systemValue = false;

    /**
     * Zeitpunkt des letzten Werte Pushes
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    private LocalDateTime lastPushTime;

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
    }

    /**
     * aktualisiert die Zeit des letzten Pushes auf die aktuelle Zeit
     */
    public void updateLastPushTime() {
        this.lastPushTime = LocalDateTime.now();
    }
}
