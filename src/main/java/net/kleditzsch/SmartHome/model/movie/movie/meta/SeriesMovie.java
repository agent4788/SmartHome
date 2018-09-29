package net.kleditzsch.SmartHome.model.movie.movie.meta;

import net.kleditzsch.SmartHome.global.base.Element;
import net.kleditzsch.SmartHome.model.movie.movie.Movie;

/**
 * Film einer Filmreihe
 */
public class SeriesMovie extends Element {

    /**
     * Nummer des Films in der Reihe
     */
    private int seriesNumber = 0;

    /**
     * Film
     */
    private Movie movie;

    /**
     * gibt die Nummer des Filmes in der Reihe zurück
     *
     * @return Nummer des Filmes in der Reihe
     */
    public int getSeriesNumber() {
        return seriesNumber;
    }

    /**
     * setzt die Nummer des Filmes in der Reihe
     *
     * @param seriesNumber Nummer des Filmes in der Reihe
     */
    public void setSeriesNumber(int seriesNumber) {
        this.seriesNumber = seriesNumber;
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
