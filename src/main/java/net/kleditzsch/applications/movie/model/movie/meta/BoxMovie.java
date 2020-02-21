package net.kleditzsch.applications.movie.model.movie.meta;

import net.kleditzsch.smarthome.model.base.Element;
import net.kleditzsch.smarthome.model.base.ID;

/**
 * Film einer Filmbox
 */
public class BoxMovie extends Element {

    /**
     * Sortierung in der Box
     */
    private int orderId = 0;

    /**
     * Film
     */
    private ID movieId;

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

    /**
     * gibt die ID des Films zurück
     *
     * @return ID des Films
     */
    public ID getMovieId() {
        return movieId;
    }

    /**
     * setzt die ID des Films
     *
     * @param movieId ID des Films
     */
    public void setMovieId(ID movieId) {
        this.movieId = movieId;
    }
}
