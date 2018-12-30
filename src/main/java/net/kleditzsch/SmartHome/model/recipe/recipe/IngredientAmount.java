package net.kleditzsch.SmartHome.model.recipe.recipe;

import net.kleditzsch.SmartHome.global.base.Element;
import net.kleditzsch.SmartHome.global.base.ID;

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
    private int amount = 0;

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
    public int getAmount() {
        return amount;
    }

    /**
     * setzt die Menge
     *
     * @param amount Menge
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }
}
