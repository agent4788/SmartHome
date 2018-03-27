package net.kleditzsch.SmartHome.model.automation.device.sensor;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateIpAddress;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMax;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMin;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;

import java.time.LocalDateTime;

/**
 * Benutzer zu Hause
 */
public class UserAtHomeValue extends SensorValue {

    private Type type = Type.SENSORVALUE_USER_AT_HOME;

    /**
     * IP Adresse
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidateIpAddress(errorCode = 10003, message = "Ungültige IP Adresse")
    private String ipAddress = "";

    /**
     * Zu Hause
     */
    private boolean atHome = false;

    /**
     * Timeout in ms
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidateMin(value = 0, errorCode = 10005, message = "Ungültiger Timeout")
    @ValidateMax(value = 86_400_000, errorCode = 10005, message = "Ungültiger Timeout")
    private int timeout = 10_000;

    /**
     * Externe Datenquelle verwenden
     */
    private boolean useExternalDataSource = false;

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public UserAtHomeValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * gibt die IP Adresse des Gerätes zurück
     *
     * @return IP Adresse
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * setzt die IP Adresse des Gerätes
     *
     * @param ipAddress IP Adresse
     */
    public void setIpAddress(String ipAddress) {

        this.ipAddress = ipAddress;
    }

    /**
     * gibt an ob der Benutzer zu Hause ist
     *
     * @return zu Hause
     */
    public boolean isAtHome() {
        return atHome;
    }

    /**
     * setzt den Status ob der Benutzer zu Hause ist
     *
     * @param atHome Status ob der Benutzer zu Hause ist
     */
    public void setAtHome(boolean atHome) {
        this.atHome = atHome;
    }

    /**
     * fügt einen neuen zu Haus eStatus hinzu
     *
     * @param atHome Status ob der Benutzer zu Hause ist
     */
    public void pushAtHome(boolean atHome) {

        setAtHome(atHome);
        setLastPushTime(LocalDateTime.now());
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
     * gibt an ob eine externe Datenquelle verwendet werden soll
     *
     * @return externe Datenquelle verwenden
     */
    public boolean isUseExternalDataSource() {
        return useExternalDataSource;
    }

    /**
     * schaltet zwischen externer und interner Datenquelle um
     *
     * @param useExternalDataSource externe Datenquelle verwenden
     */
    public void setUseExternalDataSource(boolean useExternalDataSource) {
        this.useExternalDataSource = useExternalDataSource;
    }

    /**
     * gibt den Typ des Elementes zurück
     *
     * @return Typ ID
     */
    @Override
    public Type getType() {
        return type;
    }
}
