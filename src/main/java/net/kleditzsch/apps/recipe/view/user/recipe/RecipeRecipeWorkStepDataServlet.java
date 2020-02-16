package net.kleditzsch.apps.recipe.view.user.recipe;

import com.google.gson.JsonObject;
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

public class RecipeRecipeWorkStepDataServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;

        String idStr = req.getParameter("id").trim();
        String stepIdStr = req.getParameter("step").trim();
        ID recipeId = null;
        JsonObject jo = new JsonObject();
        if(idStr != null && stepIdStr != null) {

            try {

                recipeId = ID.of(idStr);
                ID stepId = ID.of(stepIdStr);

                Optional<Recipe> recipeOptional = RecipeEditor.getRecipeById(recipeId);
                if(recipeOptional.isPresent()) {

                    Recipe recipe = recipeOptional.get();
                    Optional<WorkStep> workStepOptional = recipe.getWorkSteps().stream().filter(step -> step.getId().equals(stepId)).findFirst();
                    if(workStepOptional.isPresent()) {

                        WorkStep workStep = workStepOptional.get();
                        jo.addProperty("id", workStep.getId().get());
                        jo.addProperty("description", workStep.getDescription().orElse(""));
                        jo.addProperty("workTime", workStep.getWorkDuration().orElse(0));
                        success = true;
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

        jo.addProperty("success", success);
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().print(jo.toString());
    }
}
