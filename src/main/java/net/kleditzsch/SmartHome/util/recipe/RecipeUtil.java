package net.kleditzsch.SmartHome.util.recipe;

import net.kleditzsch.SmartHome.model.recipe.recipe.IngredientAmount;
import net.kleditzsch.SmartHome.model.recipe.recipe.Recipe;

import java.util.*;

/**
 * Rezepte Hilfsfunktionen
 */
public abstract class RecipeUtil {

    /**
     * Basis Mengeneinheiten
     */
    public static Map<String, String> baseUnits;
    static {

        Map<String, String> tmp = new HashMap<>();
        tmp.put("Stück", "Stück");
        tmp.put("Pack", "Pack");
        tmp.put("Brise", "Brise");
        tmp.put("Schwap", "Schwap");
        tmp.put("g", "g");
        tmp.put("kg", "kg");
        tmp.put("ml", "ml");
        tmp.put("l", "l");
        tmp.put("EL", "EL");
        tmp.put("cm", "cm");
        tmp.put("m", "m");
        baseUnits = Collections.unmodifiableMap(tmp);
    }

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
