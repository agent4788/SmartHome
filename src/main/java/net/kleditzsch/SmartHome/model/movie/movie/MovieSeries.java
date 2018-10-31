package net.kleditzsch.SmartHome.model.movie.movie;

import net.kleditzsch.SmartHome.global.base.Element;
import net.kleditzsch.SmartHome.model.movie.movie.meta.SeriesMovie;

import java.util.*;

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
     * Dateiname der Poster Datei
     */
    private String posterFile = "";

    /**
     * Dateiname der Banner Datei
     */
    private String bannerFile = "";

    /**
     * Filme der Filmreihe
     */
    private List<SeriesMovie> seriesMovies = new ArrayList<>();

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
     * gibt den Dateinamen der Poster Datei zurück
     *
     * @return Dateiname der Cover Datei
     */
    public String getPosterFile() {
        return posterFile;
    }

    /**
     * setzt den Dateinamen der Poster Datei
     *
     * @param posterFile Dateiname der Cover Datei
     */
    public void setPosterFile(String posterFile) {
        this.posterFile = posterFile;
    }

    /**
     * gibt den Dateinamen der Banner Datei zurück
     *
     * @return Dateiname der Banner Datei
     */
    public String getBannerFile() {
        return bannerFile;
    }

    /**
     * setzt den Dateinamen der Banner Datei
     *
     * @param bannerFile Dateiname der Banner Datei
     */
    public void setBannerFile(String bannerFile) {
        this.bannerFile = bannerFile;
    }

    /**
     * gibt die Liste der Filme der Reihe zurück
     *
     * @return Liste der Filme der Reihe
     */
    public List<SeriesMovie> getSeriesMovies() {
        return seriesMovies;
    }
}
