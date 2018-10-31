package net.kleditzsch.SmartHome.model.movie.movie.meta;

import net.kleditzsch.SmartHome.global.base.Element;
import net.kleditzsch.SmartHome.global.base.ID;

/**
 * Film einer Filmreihe
 */
public class SeriesMovie extends Element {

    /**
     * Filmtyp in der Reihe
     */
    public enum SeriesMovieType {
        Prequel,    //Vorgeschichte
        Main,       //Hauptfilm
        Sequel,     //Fortsetzung
        Spin_Off,   //Nebengeschichte
        Remake      //neuverfilmung
    }

    /**
     * Nummer des Films in der Reihe
     */
    private int orderId = 0;

    /**
     * Nummer des Teils der Reihe (1, 2, 3, 3.1 ...)
     */
    private String partNumber = "1";

    /**
     * Beschreibung des Teils der Reihe
     */
    private String partDescription = "";

    /**
     * Zeitliche Sortierung nach der Handlung
     */
    private int timeOrder = 1;

    /**
     * Typ des Filme (Vorgeschichte, Fortsetzung ...)
     */
    private SeriesMovieType seriesType = SeriesMovieType.Main;

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
    }

    /**
     * gibt die Nummer des Teils in der Reihe zurück
     *
     * @return Nummer des Teils in der Reihe
     */
    public String getPartNumber() {
        return partNumber;
    }

    /**
     * setzt die Nummer des Teils in der Reihe
     *
     * @param partNumber Nummer des Teils in der Reihe
     */
    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    /**
     * gibt die Beschreibung des Teils in der Reihe zurück
     *
     * @return Beschreibung des Teils in der Reihe
     */
    public String getPartDescription() {
        return partDescription;
    }

    /**
     * setzt die Beschreibung des Teils in der Reihe
     *
     * @param partDescription Beschreibung des Teils in der Reihe
     */
    public void setPartDescription(String partDescription) {
        this.partDescription = partDescription;
    }

    /**
     * gibt die Zeitliche Reihenfolge tes Teils in der Reihe zurück
     *
     * @return Zeitliche Reihenfolge tes Teils in der Reihe
     */
    public int getTimeOrder() {
        return timeOrder;
    }

    /**
     * setzt die Zeitliche Reihenfolge tes Teils in der Reihe
     *
     * @param timeOrder Zeitliche Reihenfolge tes Teils in der Reihe
     */
    public void setTimeOrder(int timeOrder) {
        this.timeOrder = timeOrder;
    }

    /**
     * gibt den Filmtyp in der Reihe zurück
     *
     * @return Filmtyp in der Reihe
     */
    public SeriesMovieType getSeriesType() {
        return seriesType;
    }

    /**
     * Filmtyp in der Reihe
     *
     * @param seriesType Filmtyp in der Reihe
     */
    public void setSeriesType(SeriesMovieType seriesType) {
        this.seriesType = seriesType;
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
