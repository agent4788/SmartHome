package net.kleditzsch.SmartHome.model.movie.movie;

import net.kleditzsch.SmartHome.global.base.Element;
import net.kleditzsch.SmartHome.model.movie.movie.meta.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private Disc disc;

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
    private FSK fsk;

    /**
     * Genre
     */
    private Genre genre;

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
    private List<Director> directors = new ArrayList<>();

    /**
     * Schauspieler
     */
    private List<Actor> actors = new ArrayList<>();

    /**
     * teil eine Filmbox
     */
    private boolean inMovieBox = false;

    /**
     * teil einer Fimreihe
     */
    private boolean inMovieSeries = false;

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
    public FSK getFsk() {
        return fsk;
    }

    /**
     * setzt die Altersfreigabe
     *
     * @param fsk Altersfreigabe
     */
    public void setFsk(FSK fsk) {
        this.fsk = fsk;
    }

    /**
     * gibt das Genre zurück
     *
     * @return Genre
     */
    public Genre getGenre() {
        return genre;
    }

    /**
     * setzt das Genre
     *
     * @param genre Genre
     */
    public void setGenre(Genre genre) {
        this.genre = genre;
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
    public List<Director> getDirectors() {
        return directors;
    }

    /**
     * gibt die Liste der Schauspieler zurück
     *
     * @return Liste der Schauspieler
     */
    public List<Actor> getActors() {
        return actors;
    }

    /**
     * gibt an ob der Film zu einer Box gehört
     *
     * @return gehört zu Filmbox
     */
    public boolean isInMovieBox() {
        return inMovieBox;
    }

    /**
     * markiert den Film als Teil einer Filmbox
     *
     * @param inMovieBox Teil einer Filmbox
     */
    public void setInMovieBox(boolean inMovieBox) {
        this.inMovieBox = inMovieBox;
    }

    /**
     * gibt an ob der Film zu einer Filmreihe hegört
     *
     * @return gehört zu Filmreihe
     */
    public boolean isInMovieSeries() {
        return inMovieSeries;
    }

    /**
     * markiert den Film als Teil einer Filmreihe
     *
     * @param inMovieSeries Teil einer Filmreihe
     */
    public void setInMovieSeries(boolean inMovieSeries) {
        this.inMovieSeries = inMovieSeries;
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
