package net.kleditzsch.SmartHome.model.movie.movie;

import net.kleditzsch.SmartHome.global.base.Element;
import net.kleditzsch.SmartHome.model.movie.movie.meta.SeriesMovie;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Filmreihe
 */
public class MovieSeries extends Element {

    /**
     * Filmtitel
     */
    private String title = "";

    /**
     * Zusatztitel
     */
    private String subTitle = "";

    /**
     * Dateiname der Cover Datei
     */
    private String coverFile = "";

    /**
     * Filme der Filmreihe
     */
    private SortedSet<SeriesMovie> movies = new TreeSet<>(Comparator.comparingInt(SeriesMovie::getSeriesNumber));

    /**
     * gibt den Titel zurück
     *
     * @return Titel
     */
    public String getTitle() {
        return title;
    }

    /**
     * setzt den Titel
     *
     * @param title Titel
     */
    public void setTitle(String title) {

        this.title = title;
    }

    /**
     * gibt den Zusatztitel zurück
     *
     * @return Zusatztitel
     */
    public String getSubTitle() {
        return subTitle;
    }

    /**
     * setzt den Zusatztitel
     *
     * @param subTitle Zusatztitel
     */
    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    /**
     * gibt den Dateinamen der Cover Datei zurück
     *
     * @return Dateiname der Cover Datei
     */
    public String getCoverFile() {
        return coverFile;
    }

    /**
     * setzt den Dateinamen der Cover Datei
     *
     * @param coverFile Dateiname der Cover Datei
     */
    public void setCoverFile(String coverFile) {
        this.coverFile = coverFile;
    }

    /**
     * gibt die Liste der Filme der Reihe zurück
     *
     * @return Liste der Filme der Reihe
     */
    public SortedSet<SeriesMovie> getMovies() {
        return movies;
    }
}
