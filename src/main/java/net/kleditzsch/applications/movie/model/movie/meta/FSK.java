package net.kleditzsch.applications.movie.model.movie.meta;

import net.kleditzsch.smarthome.model.base.Element;

/**
 * Altersfreigabe
 */
public class FSK extends Element {

    /**
     * Stufe der Altersfreigabe (je höher desto höher die Freigabe)
     */
    private int level = 0;

    /**
     * Name der Bilddatei des Logos
     */
    private String imageFile = "";

    /**
     * gibt das Level der Altersfreigabe zurück
     *
     * @return Level der Altersfreigabe
     */
    public int getLevel() {
        return level;
    }

    /**
     * setzt das Level der Altersfreigabe
     *
     * @param level Level der Altersfreigabe
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * bibt den Namen der Bilddatei des Logos zurück
     *
     * @return Name der Bilddatei des Logos
     */
    public String getImageFile() {
        return imageFile;
    }

    /**
     * setzt den Namen der Bilddatei des Logos
     *
     * @param imageFile Name der Bilddatei des Logos
     */
    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }
}
