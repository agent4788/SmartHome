package net.kleditzsch.apps.recipe.view.user.recipe;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.utility.form.FormValidation;
import net.kleditzsch.apps.recipe.model.editor.RecipeEditor;
import net.kleditzsch.apps.recipe.model.recipe.LinkedRecipe;
import net.kleditzsch.apps.recipe.model.recipe.Recipe;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class RecipeRecipeUpdateGarnishServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;

        FormValidation form = FormValidation.create(req);
        ID recipeId = null;
        try {

            recipeId = ID.of(req.getParameter("id").trim());
            Optional<Recipe> recipeOptional = RecipeEditor.getRecipeById(recipeId);
            if(recipeOptional.isPresent()) {

                Recipe recipe = recipeOptional.get();
                List<ID> linkedRecipesId = form.getIDList("garnishRecipes", "Beilagen");
                System.out.println(linkedRecipesId);
                List<Recipe> linkedRecipes = RecipeEditor.listRecipesByIdList(linkedRecipesId);
                recipe.getLinkedRecipes().clear();
                linkedRecipes.forEach(linkedRecipe-> {

                    LinkedRecipe linkedRecipe1 = new LinkedRecipe();
                    linkedRecipe1.setId(ID.create());
                    linkedRecipe1.setRecipeId(linkedRecipe.getId());
                    recipe.getLinkedRecipes().add(linkedRecipe1);
                });

                success = RecipeEditor.updateRecipe(recipe);
            }
        } catch (Exception e) {

            //Fehlerhafte Eingaben
            success = false;
        }

        if (success) {

            //löschem i.O.
            req.getSession().setAttribute("success", true);
            req.getSession().setAttribute("message", "Die Beilagen wurden erfolgreich gespeichert");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Die Beilagen konnten nicht gespeichert werden");
        }

        //Weiterleitung
        if(recipeId != null) {

            resp.sendRedirect("/recipe/recipeview?id=" + recipeId.get());
        } else {

            resp.sendRedirect("/recipe/recipe");
        }
    }
}
