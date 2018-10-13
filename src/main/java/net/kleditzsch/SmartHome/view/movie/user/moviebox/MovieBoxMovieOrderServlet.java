package net.kleditzsch.SmartHome.view.movie.user.moviebox;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.MovieBoxEditor;
import net.kleditzsch.SmartHome.model.movie.movie.MovieBox;
import net.kleditzsch.SmartHome.model.movie.movie.meta.BoxMovie;
import net.kleditzsch.SmartHome.util.collection.CollectionUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MovieBoxMovieOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if(req.getParameter("direction") != null && req.getParameter("boxid") != null && req.getParameter("movieid") != null) {

            try {

                boolean moveUp = req.getParameter("direction").equals("up");
                ID boxId = ID.of(req.getParameter("boxid"));
                ID movieId = ID.of(req.getParameter("movieid"));

                //Sortierungen anpassen
                Optional<MovieBox> movieBoxOptional = MovieBoxEditor.getMovieBox(boxId);
                if(movieBoxOptional.isPresent()) {

                    MovieBox movieBox = movieBoxOptional.get();
                    List<BoxMovie> boxMovies = movieBox.getBoxMovies().stream().sorted(Comparator.comparingInt(BoxMovie::getOrderId)).collect(Collectors.toList());
                    Optional<BoxMovie> boxMovieOptional = boxMovies.stream().filter(boxMovie -> boxMovie.getMovieId().equals(movieId)).findFirst();
                    if(boxMovieOptional.isPresent()) {

                        int currentIndex = boxMovies.indexOf(boxMovieOptional.get());
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
                            CollectionUtil.moveItem(currentIndex, newIndex, boxMovies);
                            int orderId = 0;
                            for (BoxMovie boxMovie : boxMovies) {

                                boxMovie.setOrderId(orderId);
                                orderId++;
                            }
                            movieBox.getBoxMovies().clear();
                            movieBox.getBoxMovies().addAll(boxMovies);

                            if(MovieBoxEditor.updateMovieBox(movieBox)) {

                                req.getSession().setAttribute("success", true);
                                req.getSession().setAttribute("message", "Die Sortierung wurde erfolgreich bearbeitet");
                                resp.sendRedirect("/movie/movieboxview?edit=1&id=" + boxId.get());
                            } else {

                                req.getSession().setAttribute("success", false);
                                req.getSession().setAttribute("message", "Fehler beim Speichern der sortierung");
                                resp.sendRedirect("/movie/movieboxview?edit=1&id=" + boxId.get());
                            }
                        } else {

                            req.getSession().setAttribute("success", false);
                            req.getSession().setAttribute("message", "Falscher Index");
                            resp.sendRedirect("/movie/movieboxview?edit=1&id=" + boxId.get());
                        }
                    }
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Unbekannter Film Index");
                    resp.sendRedirect("/movie/movieboxview?edit=1&id=" + boxId.get());
                }
            } catch (Exception e) {

                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Fehlerhafte Daten");
                resp.sendRedirect("/movie/moviebox");
            }
        } else {

            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Daten");
            resp.sendRedirect("/movie/moviebox");
        }
    }
}
