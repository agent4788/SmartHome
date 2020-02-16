package net.kleditzsch.apps.recipe.model.recipe;

import net.kleditzsch.SmartHome.model.base.Element;

/**
 * Zutat
 */
public class Ingredient extends Element implements Comparable<Ingredient> {

    /**
     * Einheit
     */
    private String unit = "";

    /**
     * gibt die Einheit zurück
     *
     * @return Einheit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * setzt die Einheit
     *
     * @param unit Einheit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public int compareTo(Ingredient o) {

        return getName().compareToIgnoreCase(o.getName());
    }
}
