package net.kleditzsch.applications.recipe.model.recipe;

import net.kleditzsch.smarthome.model.base.Element;

/**
 * Rezept Tag
 */
public class Tag extends Element implements Comparable<Tag> {

    /**
     * Tag Farben
     */
    public enum Color {

        none,
        red,
        orange,
        yellow,
        olive,
        green,
        teal,
        blue,
        violet,
        purple,
        pink,
        brown,
        grey,
        black
    }

    /**
     * Tag Farbe
     */
    private Color color = Color.none;

    /**
     * gibt die Tag Farbe zurück
     *
     * @return Tag Farbe
     */
    public Color getColor() {
        return color;
    }

    /**
     * setzt die Tag Farbe
     *
     * @param color Tag Farbe
     */
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int compareTo(Tag o) {

        return getName().compareToIgnoreCase(o.getName());
    }
}
