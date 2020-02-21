package net.kleditzsch.applications.recipe.view.user.foodlist;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.recipe.model.editor.RecipeEditor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RecipeUpdateFoodListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean refToList = req.getParameter("refToList") != null;
        boolean refToRecipe = req.getParameter("refToRecipe") != null;
        try {

            ID recipeId = ID.of(req.getParameter("id"));
            boolean foodlist = req.getParameter("foodlist").equals("1");
            boolean removeBookmark = req.getParameter("removeBookmark") != null;

            if(RecipeEditor.updateFoodList(recipeId, foodlist, removeBookmark)) {

                resp.setStatus(HttpServletResponse.SC_OK);
                if(refToList) {

                    //entfernen i.O.
                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Das Rezept wurde erfolgreich von der Liste entfernt");
                    resp.sendRedirect("/recipe/foodlist");
                } else if(refToRecipe) {

                    //entfernen i.O.
                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Das Rezept wurde erfolgreich auf der Essensliste hinzugeügt");
                    resp.sendRedirect("/recipe/recipeview?id=" + recipeId.get());
                } else {

                    resp.getWriter().write("1");
                }
            } else {

                resp.setStatus(HttpServletResponse.SC_OK);
                if(refToList) {

                    //entfernen i.O.
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Das Rezept konnte nicht von der Liste entfernt werden");
                    resp.sendRedirect("/recipe/foodlist");
                } else if(refToRecipe) {

                    //entfernen i.O.
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Das Rezept konnte nicht auf der Liste gespeichert werden");
                    resp.sendRedirect("/recipe/recipeview?id=" + recipeId.get());
                } else {

                    resp.getWriter().write("0");
                }
            }
        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_OK);
            if(refToList) {

                //entfernen i.O.
                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Das Rezept konnte nicht von der Liste entfernt werden");
                resp.sendRedirect("/recipe/foodlist");
            } else if(refToRecipe) {

                //entfernen i.O.
                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Das Das Rezept konnte nicht auf der Liste gespeichert werden");
                resp.sendRedirect("/recipe/recipe");
            } else {

                resp.getWriter().write("0");
            }
        }
    }
}
