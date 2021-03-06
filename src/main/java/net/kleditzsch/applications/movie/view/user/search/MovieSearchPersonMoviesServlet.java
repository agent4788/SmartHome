package net.kleditzsch.applications.movie.view.user.search;

import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.smarthome.model.editor.SettingsEditor;
import net.kleditzsch.smarthome.model.settings.Interface.Settings;
import net.kleditzsch.smarthome.utility.form.FormValidation;
import net.kleditzsch.smarthome.utility.jtwig.JtwigFactory;
import net.kleditzsch.smarthome.utility.pagination.ListPagination;
import net.kleditzsch.applications.movie.model.editor.MovieEditor;
import net.kleditzsch.applications.movie.model.editor.PersonEditor;
import net.kleditzsch.applications.movie.model.movie.Movie;
import net.kleditzsch.applications.movie.model.movie.meta.Person;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

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
        SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        elementsAtPage = settingsEditor.getIntegerSetting(Settings.MOVIE_PAGINATION_ELEMENTS_AT_USER_PAGE).getValue();
        settingsLock.unlock();

        FormValidation form = FormValidation.create(req);
        if(form.fieldNotEmpty("id")) {

            ID id = form.getId("id", "Personen ID");
            if(form.isSuccessful()) {

                Optional<Person> personOptional = PersonEditor.createAndLoad().getById(id);
                if(personOptional.isPresent()) {

                    List<Movie> movies = new ArrayList<>(50);
                    movies.addAll(MovieEditor.searchMoviesByDirector(id));
                    movies.addAll(MovieEditor.searchMoviesByActor(id));

                    //Doppelte Einträge filtern
                    List<String> knownIDs = new ArrayList<>(movies.size());
                    movies = movies.stream()
                            .filter(m -> {

                                if(!knownIDs.contains(m.getId().get())) {

                                    knownIDs.add(m.getId().get());
                                    return true;
                                }
                                return false;
                            })
                            .sorted(Comparator.comparing(Movie::getTitle))
                            .collect(Collectors.toList());

                    //Blätterfunktion
                    ListPagination<Movie> pagination = new ListPagination<>(movies, elementsAtPage, index);
                    pagination.setBaseLink("/movie/serachpersonmovies?id=" + id.get() + "&index=");
                    model.with("person", personOptional.get());
                    model.with("pagination", pagination);
                } else {

                    //Regiseur nicht gefunden
                    model.with("errorMessage", "Person nicht gefunden");
                }
            } else  {

                //Ungültige ID
                model.with("errorMessage", "Ungültige ID");
            }
        } else {

            //kein Suchbegriff eingegeben
            model.with("errorMessage", "Ungültige Personen ID");
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
