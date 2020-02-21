package net.kleditzsch.applications.movie.model.movie;

import net.kleditzsch.smarthome.model.base.Element;
import net.kleditzsch.smarthome.model.base.ID;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Film
 */
public class Movie extends Element {

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
     * Produktionsjahr
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
     * Laufzeit [min]
     */
    private int duration = 0;

    /**
     * Altersfreigabe
     */
    private ID fskId;

    /**
     * Genre
     */
    private ID genreId;

    /**
     * Bewertung
     */
    private int rating;

    /**
     * Kaufdatum
     */
    private LocalDate purchaseDate = LocalDate.of(2000, 01, 01);

    /**
     * Regiseure
     */
    private List<ID> directorIds = new ArrayList<>();

    /**
     * Schauspieler
     */
    private List<ID> actorIds = new ArrayList<>();

    /**
     * teil eine Filmbox
     */
    private ID boxId = null;

    /**
     * teil einer Fimreihe
     */
    private ID seriesId = null;

    /**
     * demnächst anschauen
     */
    private boolean viewSoon = false;

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
     * gibt das Produktionsjahr zurück
     *
     * @return Produktionsjahr
     */
    public int getYear() {
        return year;
    }

    /**
     * setzt das Produktionsjahr
     *
     * @param year Produktionsjahr
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * gibt die Disc zurück
     *
     * @return Disc
     */
    public ID getDiscId() {
        return discId;
    }

    /**
     * setzt die Disc
     *
     * @param discId Disc
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
     * gibt die Laufzeit zurück
     *
     * @return Laufzeit
     */
    public int getDuration() {
        return duration;
    }

    /**
     * setzt die Laufzeit
     *
     * @param duration Laufzeit
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * gibt die Altersfreigabe zurück
     *
     * @return Altersfreigabe
     */
    public ID getFskId() {
        return fskId;
    }

    /**
     * setzt die Altersfreigabe
     *
     * @param fskId Altersfreigabe
     */
    public void setFskId(ID fskId) {
        this.fskId = fskId;
    }

    /**
     * gibt das Genre zurück
     *
     * @return Genre
     */
    public ID getGenreId() {
        return genreId;
    }

    /**
     * setzt das Genre
     *
     * @param genreId Genre
     */
    public void setGenreId(ID genreId) {
        this.genreId = genreId;
    }

    /**
     * gibt die Bewertung zrrück
     *
     * @return Bewertung
     */
    public int getRating() {
        return rating;
    }

    /**
     * setzt die Bewertung
     *
     * @param rating Bewertung
     */
    public void setRating(int rating) {
        this.rating = rating;
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
     * gibt die Liste der Regiseure zurück
     *
     * @return Liste der Regiseure
     */
    public List<ID> getDirectorIds() {
        return directorIds;
    }

    /**
     * gibt die Liste der Schauspieler zurück
     *
     * @return Liste der Schauspieler
     */
    public List<ID> getActorIds() {
        return actorIds;
    }

    /**
     * gibt an ob der Film zu einer Box gehört
     *
     * @return gehört zu Filmbox
     */
    public Optional<ID> getBoxId() {
        return Optional.ofNullable(boxId);
    }

    /**
     * gibt an ob der Film zu einer Box gehört
     *
     * @return gehört zu Filmbox
     */
    public boolean isInMovieBox() {
        return getBoxId().isPresent();
    }

    /**
     * markiert den Film als Teil einer Filmbox
     *
     * @param boxId Teil einer Filmbox
     */
    public void setInMovieBox(ID boxId) {
        this.boxId = boxId;
    }

    /**
     * gibt an ob der Film zu einer Filmreihe hegört
     *
     * @return gehört zu Filmreihe
     */
    public Optional<ID> getSeriesId() {
        return Optional.ofNullable(seriesId);
    }

    /**
     * gibt an ob der Film zu einer Filmreihe hegört
     *
     * @return gehört zu Filmreihe
     */
    public boolean isInMovieSeries() {
        return getSeriesId().isPresent();
    }

    /**
     * markiert den Film als Teil einer Filmreihe
     *
     * @param seriesId Teil einer Filmreihe
     */
    public void setInMovieSeries(ID seriesId) {
        this.seriesId = seriesId;
    }

    /**
     * gibt an ob der Film als "Demnächst anschauen" markiert ist
     *
     * @return Film als "Demnächst anschauen" markiert
     */
    public boolean isViewSoon() {
        return viewSoon;
    }

    /**
     * setzt den Film als "Demnächst anschauen"
     *
     * @param viewSoon Film "Demnächst anschauen"
     */
    public void setViewSoon(boolean viewSoon) {
        this.viewSoon = viewSoon;
    }
}
