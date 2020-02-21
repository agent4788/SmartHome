package net.kleditzsch.applications.movie.model.movie;

import net.kleditzsch.smarthome.model.base.Element;
import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.movie.model.movie.meta.BoxMovie;

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
    private ID discId;

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
    private List<BoxMovie> boxMovies = new ArrayList<>();

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
     * gibt die Disc ID zurück
     *
     * @return Disc ID
     */
    public ID getDiscId() {
        return discId;
    }

    /**
     * setzt die Disc ID
     *
     * @param discId Disc ID
     */
    public void setDiscId(ID discId) {
        this.discId = discId;
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
    public List<BoxMovie> getBoxMovies() {
        return boxMovies;
    }
}
