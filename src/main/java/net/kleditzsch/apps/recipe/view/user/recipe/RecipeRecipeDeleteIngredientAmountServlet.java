package net.kleditzsch.apps.recipe.view.user.recipe;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.recipe.model.editor.RecipeEditor;
import net.kleditzsch.apps.recipe.model.recipe.IngredientAmount;
import net.kleditzsch.apps.recipe.model.recipe.Recipe;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class RecipeRecipeDeleteIngredientAmountServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;

        String idStr = req.getParameter("id").trim();
        String amountStr = req.getParameter("amount").trim();
        ID recipeId = null;
        if(idStr != null) {

            try {

                recipeId = ID.of(idStr);
                ID amount = ID.of(amountStr);

                Optional<Recipe> recipeOptional = RecipeEditor.getRecipeById(recipeId);
                if(recipeOptional.isPresent()) {

                    Recipe recipe = recipeOptional.get();
                    Optional<IngredientAmount> ingredientAmountOptional = recipe.getIngredientAmounts().stream().filter(ingredientAmount -> ingredientAmount.getId().equals(amount)).findFirst();
                    if(ingredientAmountOptional.isPresent()) {

                        recipe.getIngredientAmounts().remove(ingredientAmountOptional.get());
                        success = RecipeEditor.updateRecipe(recipe);
                    } else {

                        success = false;
                    }
                } else {

                    success = false;
                }

            } catch (Exception e) {

                success = false;
            }
        }

        if (success) {

            //löschem i.O.
            req.getSession().setAttribute("success", true);
            req.getSession().setAttribute("message", "Die Zutat wurde erfolgreich gelöscht");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Die Zutat konnte nicht gelöscht werden");
        }

        //Weiterleitung
        if(recipeId != null) {

            resp.sendRedirect("/recipe/recipeview?id=" + recipeId.get() + "&edit=ingredient");
        } else {

            resp.sendRedirect("/recipe/recipe");
        }
    }
}
