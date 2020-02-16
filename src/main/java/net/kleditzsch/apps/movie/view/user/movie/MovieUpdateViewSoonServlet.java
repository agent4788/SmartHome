package net.kleditzsch.apps.movie.view.user.movie;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.movie.model.editor.MovieEditor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MovieUpdateViewSoonServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {

            ID movieId = ID.of(req.getParameter("id"));
            boolean viewSoon = req.getParameter("viewsoon").equals("1");

            if(MovieEditor.updateViewSoon(movieId, viewSoon)) {

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
