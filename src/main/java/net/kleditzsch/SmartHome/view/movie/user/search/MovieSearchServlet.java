package net.kleditzsch.SmartHome.view.movie.user.search;

import com.google.common.collect.Comparators;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.IntegerSetting;
import net.kleditzsch.SmartHome.model.movie.editor.*;
import net.kleditzsch.SmartHome.model.movie.movie.Movie;
import net.kleditzsch.SmartHome.model.movie.movie.MovieBox;
import net.kleditzsch.SmartHome.model.movie.movie.MovieSeries;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Actor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Director;
import net.kleditzsch.SmartHome.util.form.FormValidation;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class MovieSearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/user/search/search.html");
        JtwigModel model = JtwigModel.newModel();

        FormValidation form = FormValidation.create(req);
        if(form.fieldNotEmpty("query")) {

            String query = form.getString("query", "Suche");
            model.with("query", query);

            //Filme Suchen
            List<Movie> resultMovies = MovieEditor.search(query);
            if (resultMovies.size() > 0) {

                model.with("resultMovies", resultMovies.subList(0, (resultMovies.size() >= 8 ? 8 : resultMovies.size())));
                model.with("resultMoviesCount", resultMovies.size());
            }

            //Filmboxen suchen
            List<MovieBox> resultBoxes = MovieBoxEditor.search(query);
            if (resultBoxes.size() > 0) {

                model.with("resultBoxes", resultBoxes.subList(0, (resultBoxes.size() >= 8 ? 8 : resultBoxes.size())));
                model.with("resultBoxesCount", resultBoxes.size());
            }

            //Filmreihen suchen
            List<MovieSeries> resultSeries = MovieSeriesEditor.search(query);
            if (resultSeries.size() > 0) {

                model.with("resultSeries", resultSeries.subList(0, (resultSeries.size() >= 8 ? 8 : resultSeries.size())));
                model.with("resultSeriesCount", resultSeries.size());
            }

            //Regiseure suchen
            List<Director> resultDirectors = DirectorEditor.createAndLoad().getData().stream()
                    .filter(director -> director.getName().toLowerCase().contains(query.toLowerCase()))
                    .sorted(Comparator.comparing(Director::getName))
                    .collect(Collectors.toList());
            if (resultDirectors.size() > 0) {

                model.with("resultDirectors", resultDirectors.subList(0, (resultDirectors.size() >= 8 ? 8 : resultDirectors.size())));
                model.with("resultDirectorsCount", resultDirectors.size());
            }

            //Schauspieler suchen
            List<Actor> resultActors = ActorEditor.createAndLoad().getData().stream()
                    .filter(actor -> actor.getName().toLowerCase().contains(query.toLowerCase()))
                    .sorted(Comparator.comparing(Actor::getName))
                    .collect(Collectors.toList());
            if (resultActors.size() > 0) {

                model.with("resultActors", resultActors.subList(0, (resultActors.size() >= 8 ? 8 : resultActors.size())));
                model.with("resultActorsCount", resultActors.size());
            }
        } else {

            //kein Suchbegriff eingegeben
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
