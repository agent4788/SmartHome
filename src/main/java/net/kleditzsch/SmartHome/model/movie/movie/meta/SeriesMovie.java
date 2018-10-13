package net.kleditzsch.SmartHome.model.movie.movie.meta;

import net.kleditzsch.SmartHome.global.base.Element;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.movie.Movie;

/**
 * Film einer Filmreihe
 */
public class SeriesMovie extends Element {

    /**
     * Filmtyp in der Reihe
     */
    enum SeriesMovieType {
        Prequel,    //Vorgeschichte
        Main,       //Hauptfilm
        Sequel,     //Fortsetzung
        Spin_Off,   //Nebengeschichte
        Remake      //neuverfilmung
    }

    /**
     * Nummer des Films in der Reihe
     */
    private double seriesOrder = 0;

    /**
     * Typ des Filme (Vorgeschichte, Fortsetzung ...)
     */
    private SeriesMovieType seriesType = SeriesMovieType.Sequel;

    /**
     * Film
     */
    private ID movieId;

    /**
     * gibt die Nummer des Filmes in der Reihe zurück
     *
     * @return Nummer des Filmes in der Reihe
     */
    public double getSeriesOrder() {
        return seriesOrder;
    }

    /**
     * setzt die Nummer des Filmes in der Reihe
     *
     * @param seriesOrder Nummer des Filmes in der Reihe
     */
    public void setSeriesOrder(double seriesOrder) {
        this.seriesOrder = seriesOrder;
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
