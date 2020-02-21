package net.kleditzsch.applications.automation.api.tmdb.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Film Informationen
 */
public class MovieInfo {

    /**
     * ID
     */
    private int id = 0;

    /**
     * Titel
     */
    private String title = "";

    /**
     * Poster Pfad
     */
    private String posterPath = null;

    /**
     * Hintergrundpfad
     */
    private String backgroundPath = null;

    /**
     * Beschreibung
     */
    private String description = null;

    /**
     * Datum des Kinostarts
     */
    private LocalDate releaseDate = null;

    /**
     * Genres
     */
    private List<String> genres = new ArrayList<>();

    /**
     * Laufzeit
     */
    private int duration = 0;

    /**
     * gibt die ID zurück
     *
     * @return ID
     */
    public int getId() {
        return id;
    }

    /**
     * setzt die ID
     *
     * @param id ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * gibt dei Titel zurück
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
     * gibt den Poster Pfad zurück
     *
     * @return Poster Pfad
     */
    public Optional<String> getPosterPath() {
        return Optional.ofNullable(posterPath);
    }

    /**
     * setzt den Poster Pfad
     *
     * @param posterPath Poster Pfad
     */
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    /**
     * gibt den Hintergrund Pfad zurück
     *
     * @return Hintergrund Pfad
     */
    public Optional<String> getBackgroundPath() {
        return Optional.ofNullable(backgroundPath);
    }

    /**
     * setzt den Hintergrund Pfad
     *
     * @param backgroundPath Hintergrund Pfad
     */
    public void setBackgroundPath(String backgroundPath) {
        this.backgroundPath = backgroundPath;
    }

    /**
     * gibt die Beschreibung zurück
     *
     * @return Beschreibung
     */
    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    /**
     * setzt die Beschreibung
     *
     * @param description Beschreibung
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * gibt das Datum des Kinostarts zurück
     *
     * @return Datum des Kinostarts
     */
    public Optional<LocalDate> getReleaseDate() {
        return Optional.ofNullable(releaseDate);
    }

    /**
     * setzt das Datum des Kinostarts
     *
     * @param releaseDate Datum des Kinostarts
     */
    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * gibt eine Liste der Genres zurück
     *
     * @return Liste der Genres
     */
    public List<String> getGenres() {
        return genres;
    }

    /**
     * gibt die Laufzeit zurück
     *
     * @return Laufzeit
     */
    public Optional<Integer> getDuration() {

        if(duration > 0) {

            return Optional.of(duration);
        }
        return Optional.empty();
    }

    /**
     * setzt die Laufzeit
     *
     * @param duration Laufzeit
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }
}
