package net.kleditzsch.apps.recipe.model.recipe;

import net.kleditzsch.SmartHome.model.base.Element;

import java.util.Optional;

/**
 * Arbeitsschritt
 */
public class WorkStep extends Element implements Comparable<WorkStep> {

    /**
     * Sortierungs ID
     */
    private int orderId = 0;

    /**
     * Zeitaufwand in Minuten
     */
    private int workDuration = 0;

    /**
     * gibt die Sortierungs ID zurück
     *
     * @return Sortierungs ID
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * setzt die Sortierungs ID
     *
     * @param orderId Sortierungs ID
     */
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    /**
     * gibt die Arbeitszeit für den Schritt zurück
     *
     * @return Arbeitszeit für den Schritt
     */
    public Optional<Integer> getWorkDuration() {

        if(workDuration > 0) {

            return Optional.of(workDuration);
        }
        return Optional.empty();
    }

    /**
     * setzt die Arbeitszeit für den Schritt
     *
     * @param workDuration Arbeitszeit für den Schritt
     */
    public void setWorkDuration(int workDuration) {
        this.workDuration = workDuration;
    }

    @Override
    public int compareTo(WorkStep o) {

        return Integer.compare(getOrderId(), o.getOrderId());
    }
}
