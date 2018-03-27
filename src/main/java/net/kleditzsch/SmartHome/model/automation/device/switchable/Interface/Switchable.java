package net.kleditzsch.SmartHome.model.automation.device.switchable.Interface;

import com.google.common.base.Preconditions;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.automation.device.AutomationElement;

import java.time.LocalDateTime;

/**
 * schaltbares ELement
 */
public abstract class Switchable extends AutomationElement {

    /**
     * Status
     */
    private State state = State.OFF;

    /**
     * Zeitpunkt des letzten Schaltvorganges
     */
    private LocalDateTime lastToggleTime;

    /**
     * gibt den aktuellen Status zurück
     *
     * @return Status
     */
    public State getState() {
        return state;
    }

    /**
     * setzt den aktuellen Status
     *
     * @param state Status
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * gibt die Zeit des letzten Schaltvorgans zurück
     *
     * @return Zeit
     */
    public LocalDateTime getLastToggleTime() {
        return this.lastToggleTime;
    }

    /**
     * setzt die Zeit des letzen Schaltvorganges
     *
     * @param lastToggleTime Zeit
     */
    public void setLastToggleTime(LocalDateTime lastToggleTime) {

        Preconditions.checkNotNull(lastToggleTime);
        this.lastToggleTime = lastToggleTime;
    }
}
