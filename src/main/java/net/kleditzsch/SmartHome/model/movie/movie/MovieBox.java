package net.kleditzsch.SmartHome.model.movie.movie;

import net.kleditzsch.SmartHome.global.base.Element;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Disc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Filmbox
 */
public class MovieBox extends Element {

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
     * Erscheinungsjahr
     */
    private int year = 1900;

    /**
     * Disc
     */
    private Disc disc;

    /**
     * Preis
     */
    private double price = 0.0;

    /**
     * Kaufdatum
     */
    private LocalDate purchaseDate = LocalDate.of(2000, 01, 01);

    /**
     * Filme der Box
     */
    private List<Movie> movies = new ArrayList<>();

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
     * gibt das Erscheinungsjahr zurück
     *
     * @return Erscheinungsjahr
     */
    public int getYear() {
        return year;
    }

    /**
     * setzt das Erscheinungsjahr
     *
     * @param year Erscheinungsjahr
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * gibt die Disc zurück
     *
     * @return Disc
     */
    public Disc getDisc() {
        return disc;
    }

    /**
     * setzt die Disc
     *
     * @param disc Disc
     */
    public void setDisc(Disc disc) {
        this.disc = disc;
    }

    /**
     * gibt den Preis zurück
     *
     * @return Preis
     */
    public double getPrice() {
        return price;
    }

    /**
     * setzt den Preis
     *
     * @param price Preis
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * gibt das Kaufdatum zurück
     *
     * @return Kaufdatum
     */
    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    /**
     * setzt das Kaufdatum
     *
     * @param purchaseDate Kaufdatum
     */
    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    /**
     * gibt die Liste mit den Filmen der Box zurück
     *
     * @return Liste mit den Filmen der Box
     */
    public List<Movie> getMovies() {
        return movies;
    }
}
