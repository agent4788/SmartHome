package net.kleditzsch.SmartHome.model.movie.movie.meta;

import net.kleditzsch.SmartHome.global.base.Element;

/**
 * Altersfreigabe
 */
public class FSK extends Element {

    /**
     * Stufe der Altersfreigabe (je höher desto höher die Freigabe)
     */
    private int level = 0;

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
}
