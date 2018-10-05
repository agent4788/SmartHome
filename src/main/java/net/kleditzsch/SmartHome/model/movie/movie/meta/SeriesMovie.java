package net.kleditzsch.SmartHome.model.movie.movie.meta;

import net.kleditzsch.SmartHome.global.base.Element;
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
        Spin_Off    //Nebengeschichte
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
    private Movie movie;

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
     * gibt den Film zurück
     *
     * @return Film
     */
    public Movie getMovie() {
        return movie;
    }

    /**
     * setzt den Film
     *
     * @param movie Film
     */
    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
