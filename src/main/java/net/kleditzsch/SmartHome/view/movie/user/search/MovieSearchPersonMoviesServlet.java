package net.kleditzsch.SmartHome.view.movie.user.search;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.IntegerSetting;
import net.kleditzsch.SmartHome.model.movie.editor.*;
import net.kleditzsch.SmartHome.model.movie.movie.Movie;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Actor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Director;
import net.kleditzsch.SmartHome.util.form.FormValidation;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.util.pagination.ListPagination;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MovieSearchPersonMoviesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/user/search/searchpersonmovies.html");
        JtwigModel model = JtwigModel.newModel();

        //Blätterfunktion
        int index = 0;
        if (req.getParameter("index") != null) {

            index = Integer.parseInt(req.getParameter("index"));
        }

        //Einstellungen laden
        int elementsAtPage = 25;
        SettingsEditor settingsEditor = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        Optional<IntegerSetting> elementsAtPageOptional = settingsEditor.getIntegerSetting(SettingsEditor.MOVIE_PAGINATION_ELEMENTS_AT_USER_PAGE);
        if (elementsAtPageOptional.isPresent()) {

            elementsAtPage = elementsAtPageOptional.get().getValue();
        }
        settingsLock.unlock();

        FormValidation form = FormValidation.create(req);
        if(form.fieldNotEmpty("directorid")) {

            ID id = form.getId("directorid", "Regiseur ID");
            if(form.isSuccessful()) {

                Optional<Director> directorOptional = DirectorEditor.createAndLoad().getById(id);
                if(directorOptional.isPresent()) {

                    List<Movie> movies = MovieEditor.searchMoviesByDirector(id);
                    ListPagination<Movie> pagination = new ListPagination<>(movies, elementsAtPage, index);
                    pagination.setBaseLink("/movie/serachpersonmovies?directorid=" + id.get() + "&index=");
                    model.with("director", directorOptional.get());
                    model.with("pagination", pagination);
                } else {

                    //Regiseur nicht gefunden
                    model.with("errorMessage", "Reigiseur nicht gefunden");
                }
            } else  {

                //Ungültige ID
                model.with("errorMessage", "Ungültige ID");
            }
        } else if (form.fieldNotEmpty("actorid")) {

            ID id = form.getId("actorid", "Schauspieler ID");
            if(form.isSuccessful()) {

                Optional<Actor> actorOptional = ActorEditor.createAndLoad().getById(id);
                if(actorOptional.isPresent()) {

                    List<Movie> movies = MovieEditor.searchMoviesByActor(id);
                    ListPagination<Movie> pagination = new ListPagination<>(movies, elementsAtPage, index);
                    pagination.setBaseLink("/movie/serachpersonmovies?actorid=" + id.get() + "&index=");
                    model.with("actor", actorOptional.get());
                    model.with("pagination", pagination);
                } else {

                    //Regiseur nicht gefunden
                    model.with("errorMessage", "Schauspieler nicht gefunden");
                }
            } else  {

                //Ungültige ID
                model.with("errorMessage", "Ungültige ID");
            }
        } else {

            //kein Suchbegriff eingegeben
        }

        //Viewport
        if(req.getSession().getAttribute("mobileView") != null && req.getSession().getAttribute("mobileView").equals("1")) {

            model.with("mobileView", true);
        } else {

            model.with("mobileView", false);
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
