package net.kleditzsch.applications.recipe.view.user.recipe;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.recipe.model.editor.RecipeEditor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RecipeUpdateBookmarkServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {

            ID recipeId = ID.of(req.getParameter("id"));
            boolean bookmark = req.getParameter("bookmark").equals("1");

            if(RecipeEditor.updateBookmark(recipeId, bookmark)) {

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("1");
            } else {

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("0");
            }
        } catch (Exception e) {

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("0");
        }
    }
}
