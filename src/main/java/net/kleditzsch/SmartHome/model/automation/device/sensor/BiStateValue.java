package net.kleditzsch.SmartHome.model.automation.device.sensor;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.sensor.Interface.SensorValue;

import java.time.LocalDateTime;

/**
 * BiState Sensorwert (z.B.: an/aus)
 */
public class BiStateValue extends SensorValue {

    private Type type = Type.SENSORVALUE_BI_STATE;

    /**
     * Status
     */
    private boolean state = false;

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
    public BiStateValue(ID id, String identifier, String name) {
        super(id, identifier, name);
    }

    /**
     * gibt den aktuellen Status zurück
     *
     * @return Status
     */
    public boolean getState() {
        return state;
    }

    /**
     * setzt den aktuellen Status
     *
     * @param state Status
     */
    public void setState(boolean state) {
        this.state = state;
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
    }

    /**
     * fügt den Status hinzu
     *
     * @param state Status
     */
    public void pushState(boolean state) {

        setState(state);
        setLastPushTime(LocalDateTime.now());
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
