package net.kleditzsch.applications.recipe.view.user.recipe;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.smarthome.utility.collection.CollectionUtil;
import net.kleditzsch.applications.recipe.model.editor.RecipeEditor;
import net.kleditzsch.applications.recipe.model.recipe.Recipe;
import net.kleditzsch.applications.recipe.model.recipe.WorkStep;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecipeRecipeWorkStepOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if(req.getParameter("direction") != null && req.getParameter("id") != null) {

            try {

                boolean moveUp = req.getParameter("direction").equals("up");
                ID id = ID.of(req.getParameter("id"));
                ID stepId = ID.of(req.getParameter("step"));

                //Sortierungen anpassen
                Optional<Recipe> recipeOptional = RecipeEditor.getRecipeById(id);
                if(recipeOptional.isPresent()) {

                    List<WorkStep> workSteps = recipeOptional.get().getWorkSteps().stream().sorted().collect(Collectors.toList());
                    Optional<WorkStep> workStepOptional = workSteps.stream().filter(step -> step.getId().equals(stepId)).findFirst();
                    if(workStepOptional.isPresent()) {

                        int currentIndex = workSteps.indexOf(workStepOptional.get());
                        int newIndex = 0;
                        if(moveUp) {

                            //nach oben verschieben
                            newIndex = currentIndex -1;
                        } else {

                            //nach unten verschieben
                            newIndex = currentIndex + 1;
                        }
                        if(newIndex >= 0 && newIndex <= 10_000) {

                            //neu sortieren und Speichern;
                            CollectionUtil.moveItem(currentIndex, newIndex, workSteps);
                            int orderId = 0;
                            for (WorkStep workStep : workSteps) {

                                Optional<WorkStep> workStepOptional1 = workSteps.stream().filter(step -> step.getId().equals(workStep.getId())).findFirst();
                                if (workStepOptional1.isPresent()) {

                                    workStepOptional1.get().setOrderId(orderId);
                                    orderId++;
                                }
                            }
                            RecipeEditor.updateRecipe(recipeOptional.get());

                            req.getSession().setAttribute("success", true);
                            req.getSession().setAttribute("message", "Die Sortierung wurde erfolgreich bearbeitet");
                            resp.sendRedirect("/recipe/recipeview?edit=workstep&id=" + id.get());
                        } else {

                            req.getSession().setAttribute("success", false);
                            req.getSession().setAttribute("message", "Falscher Index");
                            resp.sendRedirect("/recipe/recipeview?edit=workstep&id=" + id.get());
                        }
                    }
                } else {

                    //Arbeitsschritt nicht gefunden
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Der Arbeitsschritt wurde nicht gefunden");
                    resp.sendRedirect("/recipe/recipeview?edit=workstep&id=" + id.get());
                }

            } catch (Exception e) {

                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Fehlerhafte Daten");
                resp.sendRedirect("/recipe/recipe");
            }
        } else {

            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Daten");
            resp.sendRedirect("/recipe/recipe");
        }
    }
}
