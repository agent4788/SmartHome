package net.kleditzsch.SmartHome.model.automation.device.sensor;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMax;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateMin;
import net.kleditzsch.SmartHome.util.validation.Annotation.ValidateNotNull;

import java.time.LocalDateTime;

/**
 * Lebenszeichen
 */
public class LiveBitValue extends SensorValue {

    private Type type = Type.SENSORVALUE_LIVE_BIT;

    /**
     * Status
     */
    private boolean state = false;

    /**
     * Timeout in ms
     */
    @ValidateNotNull(errorCode = 10000, message = "Das Feld %s ist Null, sollte aber nicht")
    @ValidateMin(value = 100, errorCode = 10005, message = "Ungültiger Timeout")
    @ValidateMax(value = 86_400_000, errorCode = 10005, message = "Ungültiger Timeout")
    private long timeout = 10_000;

    /**
     * Statustexte
     */
    private String trueText = "";
    private String falseText = "";

    /**
     * @param id ID
     * @param identifier Identifizierung
     * @param name Name
     */
    public LiveBitValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * gibt an ob der Eingang aktiviert ist
     *
     * @return true wenn aktiviert
     */
    public boolean getState() {
        return state;
    }

    /**
     * setzt den Status
     *
     * @param state Status
     */
    public void setState(boolean state) {

        this.state = state;
        setChangedData();
    }

    /**
     * fügt einen neuen Status ein
     *
     * @param state Status
     */
    public void pushState(boolean state) {

        setState(state);
        setLastPushTime(LocalDateTime.now());
    }

    /**
     * gibt den Timeout zurück
     *
     * @return Timeout
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * setzt den Timeout
     *
     * @param timeout Timeout
     */
    public void setTimeout(long timeout) {

        this.timeout = timeout;
        setChangedData();
    }

    /**
     * gibt den Text für den "wahr" Fall zurück
     *
     * @return Text für den "wahr" Fall
     */
    public String getTrueText() {
        return trueText;
    }

    /**
     * setzt den Text für den "wahr" Fall
     *
     * @param trueText Text für den "wahr" Fall
     */
    public void setTrueText(String trueText) {

        this.trueText = trueText;
        setChangedData();
    }

    /**
     * gibt den Text für den "falsch" Fall zurück
     *
     * @return Text für den "falsch" Fall
     */
    public String getFalseText() {
        return falseText;
    }

    /**
     * setzt den Text für den "falsch" Fall
     *
     * @param falseText Text für den "falsch" Fall
     */
    public void setFalseText(String falseText) {

        this.falseText = falseText;
        setChangedData();
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
