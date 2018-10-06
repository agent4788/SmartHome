package net.kleditzsch.SmartHome.model.movie.movie.meta;

import net.kleditzsch.SmartHome.global.base.Element;

/**
 * Medientyp (DVD, Blu ray, ...)
 */
public class Disc extends Element {

    /**
     * Sortierungs ID
     */
    private int orderId = 0;

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
        setChangedData();
    }
}
