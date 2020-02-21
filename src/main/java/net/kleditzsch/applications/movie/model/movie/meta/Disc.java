package net.kleditzsch.applications.movie.model.movie.meta;

import net.kleditzsch.smarthome.model.base.Element;

/**
 * Medientyp (DVD, Blu ray, ...)
 */
public class Disc extends Element {

    /**
     * Film Qualität
     */
    public enum Quality {
        SD, HD, UHD
    }

    private Quality quality = Quality.SD;

    /**
     * Sortierungs ID
     */
    private int orderId = 0;

    /**
     * gibt die allgemeine Qualität zurück
     *
     * @return allgemeine Qualität
     */
    public Quality getQuality() {
        return quality;
    }

    /**
     * setzt die allgemeine Qualität
     *
     * @param quality allgemeine Qualität
     */
    public void setQuality(Quality quality) {
        this.quality = quality;
    }

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
