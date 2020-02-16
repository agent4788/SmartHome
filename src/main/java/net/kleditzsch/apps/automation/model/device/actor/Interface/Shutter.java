package net.kleditzsch.apps.automation.model.device.actor.Interface;

import com.google.common.base.Preconditions;

import java.time.LocalDateTime;

public abstract class Shutter extends Actor {

    /**
     * niveau
     */
    private int level = 0;

    /**
     * Zeitstempel der letzten Änderung
     */
    private LocalDateTime lastUpdateTime;

    /**
     * gibt das niveau zurück
     *
     * @return niveau
     */
    public int getLevel() {
        return level;
    }

    /**
     * setzt das niveau
     *
     * @param level niveau
     */
    public void setLevel(int level) {

        this.level = level;
        setChangedData();
    }

    /**
     * gibt den Zeitstempel der letzten Änderung zurück
     *
     * @return Zeitstempel der letzten Änderung
     */
    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * setzt den Zeitstempel der letzten Änderung
     *
     * @param lastUpdateTime Zeitstempel der letzten Änderung
     */
    public void setLastUpdateTime(LocalDateTime lastUpdateTime) {

        Preconditions.checkNotNull(lastUpdateTime);
        this.lastUpdateTime = lastUpdateTime;
        setChangedData();
    }

    /**
     * gibt an ob das Element ein schaltbares Element ist
     *
     * @return schaltbares Element
     */
    public boolean isSwitchable() {

        return false;
    }

    /**
     * gibt an ob das Element ein einfaches schaltbares Element ist
     *
     * @return einfaches schaltbares Element
     */
    public boolean isSingleSwitchable() {

        return false;
    }

    /**
     * gibt an ob das Element ein doppeltes schaltbares Element ist
     *
     * @return doppeltes schaltbares Element
     */
    public boolean isDoubleSwitchable() {

        return false;
    }

    /**
     * gibt an ob das Element ein niveau Element ist
     *
     * @return niveau Element
     */
    public boolean isLevel() {

        return true;
    }
}
