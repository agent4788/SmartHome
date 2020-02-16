package net.kleditzsch.apps.movie.model.movie;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.movie.model.movie.meta.Disc;

import java.util.Optional;

public class MovieFilter {

    /**
     * Minimallänge
     */
    private int minLength = 0;

    /**
     * Maximallänge
     */
    private int maxLength = Integer.MAX_VALUE;

    /**
     * Genre
     */
    private ID genre = null;

    /**
     * Medium
     */
    private ID disc = null;

    /**
     * Qualität
     */
    private Disc.Quality quality = null;

    /**
     * Bewertung
     */
    private int rating = -1;

    /**
     * gibt die Minimallänge zurück
     *
     * @return Minimallämge
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * setzt die Minimallänge
     *
     * @param minLength Minimallänge
     */
    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    /**
     * gibt die Maximallänge zurück
     *
     * @return Maximallänge
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * setzt die Maximallänge
     *
     * @param maxLength Maximallänge
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * gibt die Genre ID zurück
     *
     * @return Genre ID
     */
    public Optional<ID> getGenre() {
        return Optional.ofNullable(genre);
    }

    /**
     * setzt die Genre ID
     *
     * @param genre Genre ID
     */
    public void setGenre(ID genre) {
        this.genre = genre;
    }

    /**
     * gibt die Disc ID zurück
     *
     * @return Disc ID
     */
    public Optional<ID> getDisc() {
        return Optional.ofNullable(disc);
    }

    /**
     * setzt die Disc ID
     *
     * @param disc Disc ID
     */
    public void setDisc(ID disc) {
        this.disc = disc;
    }

    /**
     * gibt die allgemeine Qualität
     *
     * @return allgemeine Qualität
     */
    public Optional<Disc.Quality> getQuality() {
        return Optional.ofNullable(quality);
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
     * setzt die allgemeine Qualität
     *
     * @param quality allgemeine Qualität
     */
    public void setQuality(Disc.Quality quality) {
        this.quality = quality;
    }

    /**
     * gibt die Bewertung zurück
     *
     * @return Bewertung
     */
    public Optional<Integer> getRating() {

        if(rating >= 0) {

            return Optional.of(rating);
        }
        return Optional.empty();
    }

    /**
     * gibt an ob mindestens ein Filter aktiv ist
     *
     * @return Filter aktiv
     */
    public boolean isActive() {

        if(getMinLength() > 0
                || getMaxLength() < Integer.MAX_VALUE
                || getGenre().isPresent()
                || getRating().isPresent()
                || getQuality().isPresent()
                || getDisc().isPresent()) {

            return true;
        }
        return false;
    }
}
