package net.kleditzsch.SmartHome.view.movie.admin.statistic;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Projections;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.movie.editor.MovieBoxEditor;
import net.kleditzsch.SmartHome.model.movie.editor.MovieEditor;
import net.kleditzsch.SmartHome.model.movie.editor.MovieSeriesEditor;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.bson.Document;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;

public class MovieStatisticServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/statistic/statistic.html");
        JtwigModel model = JtwigModel.newModel();

        model.with("movieCount", MovieEditor.countMovies(true));
        model.with("movieBoxCount", MovieBoxEditor.countMovieBoxes());
        model.with("movieSeriesCount", MovieSeriesEditor.countMovieSeries());

        //Gesamtelänge, Durchschnittslänge, Gesamtpreis und Durchschnittspreis ermitteln
        MongoCollection movies = Application.getInstance().getDatabaseCollection(MovieEditor.COLLECTION);
        FindIterable iterator = movies.find().projection(Projections.include("price", "duration"));
        List<Integer> durationList = new ArrayList<>(100);
        List<Double> priceList = new ArrayList<>(100);
        iterator.forEach((Block<Document>) document -> {

            durationList.add(document.getInteger("duration"));
            priceList.add(document.get("price") instanceof Integer ? document.getInteger("price").doubleValue(): document.getDouble("price"));
        });

        MongoCollection movieBoxes = Application.getInstance().getDatabaseCollection(MovieEditor.COLLECTION);
        FindIterable iterator1 = movieBoxes.find().projection(Projections.include("price"));
        iterator1.forEach((Block<Document>) document -> {

            priceList.add(document.get("price") instanceof Integer ? document.getInteger("price").doubleValue(): document.getDouble("price"));
        });

        IntSummaryStatistics durationStatistics = durationList.stream().mapToInt(Integer::intValue).summaryStatistics();

        double sumPrice = priceList.stream().mapToDouble(Double::doubleValue).sum();
        double avgPrice = 0.0;
        if(durationList.size() > 0) {

            avgPrice = sumPrice / durationList.size();
        }
        long sumDuration = durationStatistics.getSum();
        double avgDuration = durationStatistics.getAverage();

        model.with("sumPrice", sumPrice);
        model.with("avgPrice", avgPrice);
        model.with("sumDuration", sumDuration);
        model.with("avgDuration", Math.round(avgDuration));

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
