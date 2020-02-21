package net.kleditzsch.applications.automation.model.device.actor.Interface;

import com.google.common.base.Preconditions;

import java.time.LocalDateTime;

/**
 * schaltbares ELement
 */
public abstract class Switchable extends Actor {

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
        setChangedData();
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
        setChangedData();
    }
}
