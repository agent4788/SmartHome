package net.kleditzsch.SmartHome.view.movie.user.movie;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.MovieEditor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MovieUpdateRatingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {

            ID movieId = ID.of(req.getParameter("id"));
            int rating = Integer.parseInt(req.getParameter("rating"));

            if(rating >= 0 && rating <= 5) {

                if(MovieEditor.updateRating(movieId, rating)) {

                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("1");
                } else {

                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("0");
                }
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
