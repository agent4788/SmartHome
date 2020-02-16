package net.kleditzsch.apps.recipe.model.recipe;

import net.kleditzsch.SmartHome.model.base.Element;
import net.kleditzsch.SmartHome.model.base.ID;

/**
 * Verknüpftes Rezept
 */
public class LinkedRecipe extends Element implements Comparable<LinkedRecipe> {

    /**
     * Rezept ID
     */
    private ID recipeId = null;

    /**
     * Sortierungs ID
     */
    private int orderId = 0;

    /**
     * Option
     */
    private boolean optional = false;

    /**
     * gibt die Rezept ID zurück
     *
     * @return Rezept ID
     */
    public ID getRecipeId() {
        return recipeId;
    }

    /**
     * setzt die Rezept ID
     *
     * @param recipeId Rezept ID
     */
    public void setRecipeId(ID recipeId) {
        this.recipeId = recipeId;
    }

    /**
     * gibt die Sortierungs ID zurück
     *
     * @return Sortierungs ID
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * setzt die Sortierungs ID
     *
     * @param orderId Sortierungs ID
     */
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    /**
     * gibt an ob das Rezept Optional ist
     *
     * @return Rezept Optional
     */
    public boolean isOptional() {
        return optional;
    }

    /**
     * setzt das Rezept Optional
     *
     * @param optional Rezept Optional
     */
    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    @Override
    public int compareTo(LinkedRecipe o) {

        return Integer.compare(getOrderId(), o.getOrderId());
    }
}
