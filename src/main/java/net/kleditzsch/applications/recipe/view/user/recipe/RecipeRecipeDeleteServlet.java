package net.kleditzsch.applications.recipe.view.user.recipe;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.recipe.model.editor.RecipeEditor;
import net.kleditzsch.applications.recipe.model.recipe.Recipe;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class RecipeRecipeDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);

                Optional<Recipe> recipeOptional = RecipeEditor.getRecipeById(id);
                if(recipeOptional.isPresent()) {

                    success = RecipeEditor.deleteRecipe(recipeOptional.get().getId());

                    //Cover Datei löschen
                    if(recipeOptional.get().getImageFile() != null && recipeOptional.get().getImageFile().length() > 0 && !Recipe.defaultImages.containsKey(recipeOptional.get().getImageFile())) {

                        Path imageFile = Paths.get("upload/recipe").resolve(recipeOptional.get().getImageFile());
                        try {

                            Files.delete(imageFile);
                        } catch (Exception e) {}
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
            req.getSession().setAttribute("message", "Das Rezept wurde erfolgreich gelöscht");
            resp.sendRedirect("/recipe/recipe");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Das Rezept konnte nicht gelöscht werden");
            resp.sendRedirect("/recipe/recipe");
        }
    }
}
