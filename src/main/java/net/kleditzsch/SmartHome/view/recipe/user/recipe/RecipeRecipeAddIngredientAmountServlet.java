package net.kleditzsch.SmartHome.view.recipe.user.recipe;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.recipe.editor.IngredientEditor;
import net.kleditzsch.SmartHome.model.recipe.editor.RecipeEditor;
import net.kleditzsch.SmartHome.model.recipe.recipe.Ingredient;
import net.kleditzsch.SmartHome.model.recipe.recipe.IngredientAmount;
import net.kleditzsch.SmartHome.model.recipe.recipe.Recipe;
import net.kleditzsch.SmartHome.util.form.FormValidation;
import net.kleditzsch.SmartHome.util.recipe.RecipeUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Optional;

public class RecipeRecipeAddIngredientAmountServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;

        FormValidation form = FormValidation.create(req);
        ID recipeId = null;
        try {

            recipeId = ID.of(req.getParameter("id").trim());
            Optional<Recipe> recipeOptional = RecipeEditor.getRecipeById(recipeId);
            if(recipeOptional.isPresent()) {

                Ingredient ingredient = null;
                String name = form.getString("name", "Name", 1, 50);
                if(req.getParameter("ingredientId") != null && !req.getParameter("ingredientId").isBlank()) {

                    //bekannte Zutat
                    ID ingredientId = ID.of(req.getParameter("ingredientId").trim());
                    Optional<Ingredient> ingredientOptional = IngredientEditor.getIngredientById(ingredientId);
                    if(ingredientOptional.isPresent()) {

                        ingredient = ingredientOptional.get();
                    } else {

                        //Zutat nicht gefunden
                        success = false;
                    }
                } else {

                    //Neue Zutat
                    String unit = form.getString("unit", "Einheit", new ArrayList<>(RecipeUtil.baseUnits.keySet()));
                    if(form.isSuccessful()) {

                        ingredient = new Ingredient();
                        ingredient.setId(ID.create());
                        ingredient.setName(name);
                        ingredient.setUnit(unit);

                        IngredientEditor.addIngredient(ingredient);
                    } else {

                        //Fehlerhafte Eingaben
                        success = false;
                    }
                }

                //Zutatenmenge im Rezept speichern
                double amount = form.getDouble("amount", "Menge", 0.25, 1_000_000);
                if(ingredient != null && form.isSuccessful()) {

                    IngredientAmount ingredientAmount = new IngredientAmount();
                    ingredientAmount.setId(ID.create());
                    ingredientAmount.setIngredientId(ingredient.getId());
                    ingredientAmount.setAmount(amount);

                    Recipe recipe = recipeOptional.get();
                    recipe.getIngredientAmounts().add(ingredientAmount);
                    success = RecipeEditor.updateRecipe(recipe);
                } else {

                    //Fehlerhafte Zutat oder Menge
                    success = false;
                }
            } else {

                //Rezept nicht gefunden
                success = false;
            }
        } catch (Exception e) {

            //Fehlerhafte Eingaben
            success = false;
        }

        if (success) {

            //löschem i.O.
            req.getSession().setAttribute("success", true);
            req.getSession().setAttribute("message", "Die Zutat wurde erfolgreich hinzugefügt");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Die Zutat konnte nicht hinzugefügt werden");
        }

        //Weiterleitung
        if(recipeId != null) {

            resp.sendRedirect("/recipe/recipeview?id=" + recipeId.get() + "&edit=ingredient");
        } else {

            resp.sendRedirect("/recipe/recipe");
        }
    }
}
