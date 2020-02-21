package net.kleditzsch.apps.movie.view.user.search;

import net.kleditzsch.SmartHome.utility.form.FormValidation;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import net.kleditzsch.apps.movie.model.editor.MovieBoxEditor;
import net.kleditzsch.apps.movie.model.editor.MovieEditor;
import net.kleditzsch.apps.movie.model.editor.MovieSeriesEditor;
import net.kleditzsch.apps.movie.model.editor.PersonEditor;
import net.kleditzsch.apps.movie.model.movie.Movie;
import net.kleditzsch.apps.movie.model.movie.MovieBox;
import net.kleditzsch.apps.movie.model.movie.MovieSeries;
import net.kleditzsch.apps.movie.model.movie.meta.Person;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
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
            List<Person> resultDirectors = PersonEditor.createAndLoad().getData().stream()
                    .filter(director -> director.getName().toLowerCase().contains(query.toLowerCase()))
                    .sorted(Comparator.comparing(Person::getName))
                    .collect(Collectors.toList());
            if (resultDirectors.size() > 0) {

                model.with("resultPersons", resultDirectors.subList(0, (resultDirectors.size() >= 8 ? 8 : resultDirectors.size())));
                model.with("resultPersonsCount", resultDirectors.size());
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
