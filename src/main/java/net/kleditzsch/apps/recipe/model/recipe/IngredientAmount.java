package net.kleditzsch.apps.recipe.model.recipe;

import net.kleditzsch.SmartHome.model.base.Element;
import net.kleditzsch.SmartHome.model.base.ID;

/**
 * Zutaten Menge
 */
public class IngredientAmount extends Element {

    /**
     * Zutat
     */
    private ID ingredientId = null;

    /**
     * Menge
     */
    private double amount = 0;

    /**
     * gibt die Zutaten ID zurück
     *
     * @return setzt die Zutaten ID
     */
    public ID getIngredientId() {
        return ingredientId;
    }

    /**
     * setzt die Zutaten ID
     *
     * @param ingredientId Zutaten ID
     */
    public void setIngredientId(ID ingredientId) {
        this.ingredientId = ingredientId;
    }

    /**
     * gibt die Menge zurück
     *
     * @return Menge
     */
    public double getAmount() {
        return amount;
    }

    /**
     * setzt die Menge
     *
     * @param amount Menge
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }
}
