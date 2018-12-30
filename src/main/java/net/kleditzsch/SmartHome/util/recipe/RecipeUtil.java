package net.kleditzsch.SmartHome.util.recipe;

import net.kleditzsch.SmartHome.model.recipe.recipe.IngredientAmount;
import net.kleditzsch.SmartHome.model.recipe.recipe.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Rezepte Hilfsfunktionen
 */
public abstract class RecipeUtil {

    /**
     * gibt eine kombinierte Liste mit allen Rezeptzutaten zurück
     *
     * @param recipes Rezepte
     * @return Zutatenmengen
     */
    public static List<IngredientAmount> combineRecipeIngredients(List<Recipe> recipes) {

        List<IngredientAmount> ingredientAmounts = new ArrayList<>();
        for (Recipe recipe : recipes) {

            for(IngredientAmount ingredientAmount : recipe.getIngredientAmounts()) {

                Optional<IngredientAmount> knownIngredientAmountOptional = ingredientAmounts
                        .stream()
                        .filter(amount -> amount.getIngredientId().equals(ingredientAmount.getIngredientId()))
                        .findFirst();
                if(knownIngredientAmountOptional.isPresent()) {

                    //Zutat schon vorhanden -> Menge addieren
                    knownIngredientAmountOptional.get().setAmount(ingredientAmount.getAmount() + knownIngredientAmountOptional.get().getAmount());
                } else {

                    //Zutat noch nicht vorhanden
                    IngredientAmount amount = new IngredientAmount();
                    amount.setIngredientId(ingredientAmount.getIngredientId());
                    amount.setAmount(ingredientAmount.getAmount());
                    ingredientAmounts.add(amount);
                }
            }
        }
        return ingredientAmounts;
    }
}
