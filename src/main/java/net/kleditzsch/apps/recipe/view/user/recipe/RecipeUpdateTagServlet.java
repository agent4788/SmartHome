package net.kleditzsch.apps.recipe.view.user.recipe;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.utility.form.FormValidation;
import net.kleditzsch.apps.recipe.model.editor.RecipeEditor;
import net.kleditzsch.apps.recipe.model.recipe.Recipe;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class RecipeUpdateTagServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {

            FormValidation form = FormValidation.create(req);
            ID recipeId = ID.of(req.getParameter("id"));

            Optional<Recipe> recipeOptional = RecipeEditor.getRecipeById(recipeId);
            if(recipeOptional.isPresent()) {

                List<ID> tagIds = form.getIDList("tags", "Tags");
                if(form.isSuccessful()) {

                    Recipe recipe = recipeOptional.get();
                    recipe.getTags().clear();
                    recipe.getTags().addAll(tagIds);

                    RecipeEditor.updateRecipe(recipe);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Tags erfolgreich gespeichert");
                    resp.sendRedirect("/recipe/recipeview?id=" + recipeId.get());
                } else {

                    //Fehlerhafte Eingaben
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
                    resp.sendRedirect("/recipe/recipeview?id=" + recipeId.get());
                }
            } else {

                //Rezept nicht gefunden
                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Das Rezept konnte nicht gefunden werden");
                resp.sendRedirect("/recipe/recipe");
            }
        } catch (Exception e) {

            //Fehlerhafte Eingaben+
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/recipe/recipe");
        }
    }
}
