package net.kleditzsch.apps.movie.view.admin.statistic;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.utility.collection.CollectionUtil;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import net.kleditzsch.apps.movie.model.editor.*;
import net.kleditzsch.apps.movie.model.movie.Movie;
import net.kleditzsch.apps.movie.model.movie.MovieBox;
import net.kleditzsch.apps.movie.model.movie.meta.Disc;
import net.kleditzsch.apps.movie.model.movie.meta.FSK;
import net.kleditzsch.apps.movie.model.movie.meta.Genre;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MovieStatisticServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/statistic/statistic.html");
        JtwigModel model = JtwigModel.newModel();

        //Statistische Daten ermitteln
        List<Movie> movies = MovieEditor.listMovies();
        List<MovieBox> movieBoxes = MovieBoxEditor.listMovieBoxes();

        //Preis
        DoubleSummaryStatistics moviePrice = movies.stream().mapToDouble(Movie::getPrice).summaryStatistics();
        DoubleSummaryStatistics movieBoxPrice = movieBoxes.stream().mapToDouble(MovieBox::getPrice).summaryStatistics();
        double sumPrice = moviePrice.getSum() + movieBoxPrice.getSum();

        IntSummaryStatistics movieDuration = movies.stream().mapToInt(Movie::getDuration).summaryStatistics();

        model.with("sumPrice", sumPrice);
        model.with("avgPrice", sumPrice / movies.size());
        model.with("sumDuration", movieDuration.getSum());
        model.with("avgDuration", Math.round(movieDuration.getAverage()));

        model.with("movieCount", movies.size());
        model.with("movieBoxCount", movieBoxes.size());
        model.with("movieSeriesCount", MovieSeriesEditor.countMovieSeries());

        //Käufe der letzten 10 Jahre
        Map<Integer, List<Movie>> yearlyPurcheases = new LinkedHashMap<>();
        for (int i = LocalDate.now().minusYears(9).getYear(); i <= LocalDate.now().getYear(); i++) {

            final int year = i;
            List<Movie> yearPurchaseMovies = movies.stream().filter(m -> m.getPurchaseDate().getYear() == year).collect(Collectors.toList());
            yearlyPurcheases.put(year, yearPurchaseMovies);
        }

        //Anzahl der gekauften Filme pro Jahr
        Map<Integer, Integer> yearPurchasedMovies = new LinkedHashMap<>();
        yearlyPurcheases.forEach((year, purchasedMovies) -> {

            yearPurchasedMovies.put(year, purchasedMovies.size());
        });
        model.with("yearMovies", yearPurchasedMovies);

        //Gesamtpreis der gekauften Filme pro Jahr
        Map<Integer, Double> yearPriceMovies = new LinkedHashMap<>();
        yearlyPurcheases.forEach((year, purchasedMovies) -> {

            yearPriceMovies.put(year, purchasedMovies.stream().mapToDouble(Movie::getPrice).sum());
        });
        model.with("yearPriceMovies", yearPriceMovies);

        //Durchschnittspreis der gekauften Filme pro Jahr
        Map<Integer, Double> yearAvgPriceMovies = new LinkedHashMap<>();
        yearlyPurcheases.forEach((year, purchasedMovies) -> {

            yearAvgPriceMovies.put(year, purchasedMovies.stream().mapToDouble(Movie::getPrice).summaryStatistics().getAverage());
        });
        model.with("yearAvgPriceMovies", yearAvgPriceMovies);

        //Genres gruppieren und zählen
        final Map<String, Integer> genreCount = new LinkedHashMap<>(25);
        movies.forEach(m -> {

            String genreId = m.getGenreId().get();
            if(genreCount.containsKey(genreId)) {

                genreCount.put(genreId, genreCount.get(genreId) + 1);
            } else {

                genreCount.put(genreId, 1);
            }
        });
        GenreEditor genreEditor = GenreEditor.createAndLoad();
        Map<String, Integer> genres = new LinkedHashMap<>();
        Map<String, Integer> genreCountSorted = CollectionUtil.sortByValueReversed(genreCount);
        genreCountSorted.forEach((id, count) -> {

            Optional<Genre> genreOptional = genreEditor.getById(ID.of(id));
            if(genreOptional.isPresent()) {

                genres.put(genreOptional.get().getName(), count);
            }
        });
        model.with("genres", genres);

        //Medien gruppieren und zählen
        final Map<String, Integer> discCount = new LinkedHashMap<>(25);
        movies.forEach(m -> {

            String discId = m.getDiscId().get();
            if(discCount.containsKey(discId)) {

                discCount.put(discId, discCount.get(discId) + 1);
            } else {

                discCount.put(discId, 1);
            }
        });
        DiscEditor discEditor = DiscEditor.createAndLoad();
        Map<String, Integer> discs = new LinkedHashMap<>();
        Map<String, Integer> discCountSorted = CollectionUtil.sortByValueReversed(discCount);
        discCountSorted.forEach((id, count) -> {

            Optional<Disc> discOptional = discEditor.getById(ID.of(id));
            if(discOptional.isPresent()) {

                discs.put(discOptional.get().getName(), count);
            }
        });
        model.with("discs", discs);

        final Map<Integer, Integer> ratingCount = new LinkedHashMap<>(25);
        movies.forEach(m -> {

            int rating = m.getRating();
            if(ratingCount.containsKey(rating)) {

                ratingCount.put(rating, ratingCount.get(rating) + 1);
            } else {

                ratingCount.put(rating, 1);
            }
        });
        model.with("ratingCount", CollectionUtil.sortByValueReversed(ratingCount));

        //Altersfreigaben gruppieren und zählen
        final Map<String, Integer> fskCount = new LinkedHashMap<>(25);
        movies.forEach(m -> {

            String fskId = m.getFskId().get();
            if(fskCount.containsKey(fskId)) {

                fskCount.put(fskId, fskCount.get(fskId) + 1);
            } else {

                fskCount.put(fskId, 1);
            }
        });
        FskEditor fskEditor = FskEditor.createAndLoad();
        Map<String, Integer> fsks = new LinkedHashMap<>();
        Map<String, Integer> fskCountSorted = CollectionUtil.sortByValueReversed(fskCount);
        fskCountSorted.forEach((id, count) -> {

            Optional<FSK> fskOptional = fskEditor.getById(ID.of(id));
            if(fskOptional.isPresent()) {

                fsks.put(fskOptional.get().getName(), count);
            }
        });
        model.with("fsks", fsks);

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
