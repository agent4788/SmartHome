package net.kleditzsch.applications.recipe.view.user.recipe;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.smarthome.utility.form.FormValidation;
import net.kleditzsch.applications.recipe.model.editor.RecipeEditor;
import net.kleditzsch.applications.recipe.model.recipe.Recipe;
import net.kleditzsch.applications.recipe.model.recipe.WorkStep;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class RecipeRecipeWorkStepFormServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;

        FormValidation form = FormValidation.create(req);
        ID recipeId = null;
        boolean add = false;
        try {

            recipeId = ID.of(req.getParameter("id").trim());
            Optional<Recipe> recipeOptional = RecipeEditor.getRecipeById(recipeId);
            if(recipeOptional.isPresent()) {

                Recipe recipe = recipeOptional.get();
                WorkStep workStep = null;
                String description = form.getString("description", "Beschreibung", 3, 1_000_000);
                int workTime = form.getInteger("workTime", "Arbeitsschritt", 0, 1_000_000);
                if(req.getParameter("workStepId") != null && !req.getParameter("workStepId").isBlank()) {

                    //bestehender Arbeitsschritt
                    ID workStepId = ID.of(req.getParameter("workStepId").trim());
                    Optional<WorkStep> workStepOptional = recipe.getWorkSteps().stream().filter(step -> step.getId().equals(workStepId)).findFirst();
                    if(workStepOptional.isPresent() && form.isSuccessful()) {

                        workStep = workStepOptional.get();
                        workStep.setDescription(description);
                        workStep.setWorkDuration(workTime);

                        success = RecipeEditor.updateRecipe(recipe);
                    } else {

                        //Zutat nicht gefunden
                        success = false;
                    }
                } else {

                    //neuer Arbeitsschritt
                    add = true;
                    if(form.isSuccessful()) {

                        workStep = new WorkStep();
                        workStep.setId(ID.create());
                        workStep.setDescription(description);
                        workStep.setWorkDuration(workTime);
                        int nextOrderId = recipe.getWorkSteps().stream().mapToInt(WorkStep::getOrderId).summaryStatistics().getMax() + 1;
                        nextOrderId = nextOrderId >= 0 ? nextOrderId : 0;
                        workStep.setOrderId(nextOrderId);

                        recipe.getWorkSteps().add(workStep);

                        success = RecipeEditor.updateRecipe(recipe);
                    } else {

                        //Fehlerhafte Eingaben
                        success = false;
                    }
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

            req.getSession().setAttribute("success", true);
            if(add) {

                req.getSession().setAttribute("message", "Der Arbeitsschritt wurde erfolgreich hinzugefügt");
            } else {

                req.getSession().setAttribute("message", "Der Arbeitsschritt wurde erfolgreich bearbeitet");
            }
        } else {

            req.getSession().setAttribute("success", false);
            if(add) {

                req.getSession().setAttribute("message", "Der Arbeitsschritt konnte nicht hinzugefügt werden");
            } else {

                req.getSession().setAttribute("message", "Der Arbeitsschritt konnte nicht bearbeitet werden");
            }
        }

        //Weiterleitung
        if(recipeId != null) {

            resp.sendRedirect("/recipe/recipeview?id=" + recipeId.get() + "&edit=workstep");
        } else {

            resp.sendRedirect("/recipe/recipe");
        }
    }
}
