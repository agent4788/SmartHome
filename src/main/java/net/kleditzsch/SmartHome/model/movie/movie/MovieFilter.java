package net.kleditzsch.SmartHome.model.movie.movie;

import net.kleditzsch.SmartHome.global.base.ID;

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
     * setzt die Bewertung
     *
     * @param rating Bewertung
     */
    public void setRating(int rating) {
        this.rating = rating;
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
     * setzt die Disc ID
     *
     * @param disc Disc ID
     */
    public void setDisc(ID disc) {
        this.disc = disc;
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
                || getDisc().isPresent()) {

            return true;
        }
        return false;
    }
}
