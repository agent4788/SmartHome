package net.kleditzsch.apps.recipe.view.user.recipe;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.recipe.model.editor.RecipeEditor;
import net.kleditzsch.apps.recipe.model.recipe.Recipe;
import net.kleditzsch.apps.recipe.model.recipe.WorkStep;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class RecipeRecipeDeleteWorkStepServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;

        String idStr = req.getParameter("id").trim();
        String stepIdStr = req.getParameter("step").trim();
        ID recipeId = null;
        if(idStr != null && stepIdStr != null) {

            try {

                recipeId = ID.of(idStr);
                ID stepId = ID.of(stepIdStr);

                Optional<Recipe> recipeOptional = RecipeEditor.getRecipeById(recipeId);
                if(recipeOptional.isPresent()) {

                    Recipe recipe = recipeOptional.get();
                    Optional<WorkStep> workStepOptional = recipe.getWorkSteps().stream().filter(step -> step.getId().equals(stepId)).findFirst();
                    if(workStepOptional.isPresent()) {

                        recipe.getWorkSteps().remove(workStepOptional.get());
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
            req.getSession().setAttribute("message", "Der Arbeitsschritt wurde erfolgreich gelöscht");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Der Arbeitsschritt konnte nicht gelöscht werden");
        }

        //Weiterleitung
        if(recipeId != null) {

            resp.sendRedirect("/recipe/recipeview?id=" + recipeId.get() + "&edit=workstep");
        } else {

            resp.sendRedirect("/recipe/recipe");
        }
    }
}
