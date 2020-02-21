package net.kleditzsch.applications.recipe.view.admin.ingredient;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.recipe.model.editor.IngredientEditor;
import net.kleditzsch.applications.recipe.model.recipe.Ingredient;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class RecipeIngredientDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        boolean success = true;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);

                Optional<Ingredient> ingredientOptional = IngredientEditor.getIngredientById(id);
                if(ingredientOptional.isPresent()) {

                    success = IngredientEditor.deleteIngredient(ingredientOptional.get().getId());
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
            resp.sendRedirect("/recipe/admin/ingredient");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Die Zutat konnte nicht gelöscht werden");
            resp.sendRedirect("/recipe/admin/ingredient");
        }
    }
}
