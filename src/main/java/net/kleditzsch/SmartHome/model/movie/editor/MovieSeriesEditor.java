package net.kleditzsch.SmartHome.model.movie.editor;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.movie.MovieSeries;
import net.kleditzsch.SmartHome.model.movie.movie.meta.SeriesMovie;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public abstract class MovieSeriesEditor {

    private static final String COLLECTION = "movie.movieSeries";

    /**
     * gibt eine sortierte Liste mit allen Film Reihen zurück
     *
     * @return Liste aller Filme
     */
    public static List<MovieSeries> listMovieSeries() {

        MongoCollection movieSeriesCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable iterator = movieSeriesCollection.find()
                .sort(Sorts.ascending("title"));

        List<MovieSeries> movies = new ArrayList<>(50);
        iterator.forEach((Block<Document>) document -> {

            movies.add(documentToMovieSeries(document));
        });
        return movies;
    }
    /**
     * gibt eine sortierte Liste mit allen Film Reihen zurück
     *
     * @param start start Index
     * @param length länge
     * @return Liste der Filme
     */
    public static List<MovieSeries> listMovieSeries(long start, long length) {

        MongoCollection movieSeriesCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable iterator = movieSeriesCollection.find()
                .sort(Sorts.ascending("title"))
                .skip(((Long) start).intValue())
                .limit(((Long) length).intValue());

        List<MovieSeries> movies = new ArrayList<>(50);
        iterator.forEach((Block<Document>) document -> {

            movies.add(documentToMovieSeries(document));
        });
        return movies;
    }

    /**
     * sucht die Filmreihe in der Datenbank und gibt das Objekt zurück
     *
     * @param id Filmreihen ID
     * @return Filmreihen Objekt
     */
    public static Optional<MovieSeries> getMovieSeries(ID id) {

        MongoCollection movieSeriesCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable iterator = movieSeriesCollection.find(new Document().append("_id", id.get()));
        if(iterator.first() != null) {

            return Optional.of(documentToMovieSeries((Document) iterator.first()));
        }
        return Optional.empty();
    }

    /**
     * gibt die Anzahl der Filmreihen zurück
     *
     * @return Anzahl der Filmreihen
     */
    public static long countMovieSeries() {

        MongoCollection movieSeriesCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        return movieSeriesCollection.count();
    }

    /**
     * speichert eine neue Filmreihe in der Datenbank
     *
     * @param movieSeries Filmreihe
     * @return ID der Filmreihe
     */
    public static ID addMovieSeries(MovieSeries movieSeries) {

        movieSeries.setId(ID.create());

        //Filme der Reihe vorbereiten
        List<Document> seriesMovies = new ArrayList<>(movieSeries.getSeriesMovies().size());
        movieSeries.getSeriesMovies().forEach(seriesMovie -> {

            Document seriesMovieDocument = new Document();
            seriesMovieDocument.append("orderId", seriesMovie.getOrderId());
            seriesMovieDocument.append("partNumber", seriesMovie.getPartNumber());
            seriesMovieDocument.append("partDescription", seriesMovie.getPartDescription());
            seriesMovieDocument.append("timeOrder", seriesMovie.getTimeOrder());
            seriesMovieDocument.append("seriesType", seriesMovie.getSeriesType().toString());
            seriesMovies.add(seriesMovieDocument);
        });

        Document document = new Document()
                .append("_id", movieSeries.getId().get())
                .append("description", movieSeries.getDescription().orElse(""))
                .append("title", movieSeries.getTitle())
                .append("subTitle", movieSeries.getSubTitle())
                .append("coverFile", movieSeries.getCoverFile())
                .append("seriesMovies", seriesMovies);
        MongoCollection movieSeriesCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        movieSeriesCollection.insertOne(document);

        return movieSeries.getId();
    }

    /**
     * aktualisiert die Daten einer Filmreihe in der Datenbank
     *
     * @param movieSeries Filmreihe
     * @return Erfolgsmeldung
     */
    public static boolean updateMovieSeries(MovieSeries movieSeries) {

        //Filme der Reihe vorbereiten
        List<Document> seriesMovies = new ArrayList<>(movieSeries.getSeriesMovies().size());
        movieSeries.getSeriesMovies().forEach(seriesMovie -> {

            Document seriesMovieDocument = new Document();
            seriesMovieDocument.append("orderId", seriesMovie.getOrderId());
            seriesMovieDocument.append("partNumber", seriesMovie.getPartNumber());
            seriesMovieDocument.append("partDescription", seriesMovie.getPartDescription());
            seriesMovieDocument.append("timeOrder", seriesMovie.getTimeOrder());
            seriesMovieDocument.append("seriesType", seriesMovie.getSeriesType().toString());
            seriesMovies.add(seriesMovieDocument);
        });

        MongoCollection movieSeriesCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = movieSeriesCollection.updateOne(
                eq("_id", movieSeries.getId().get()),
                combine(
                        set("description", movieSeries.getDescription().orElse("")),
                        set("title", movieSeries.getTitle()),
                        set("subTitle", movieSeries.getSubTitle()),
                        set("coverFile", movieSeries.getCoverFile()),
                        set("seriesMovies", seriesMovies)
                )
        );
        return result.wasAcknowledged();
    }

    /**
     * löscht eine Filmreihe aus der Datenbank
     *
     * @param movieSeriesId ID der Filmreihe
     * @return Erfolgsmeldung
     */
    public static boolean deleteMovieSeries(ID movieSeriesId) {

        MongoCollection movieSeriesCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        if(movieSeriesCollection.deleteOne(eq("_id", movieSeriesId.get())).getDeletedCount() == 1) {

            return true;
        }
        return false;
    }

    /**
     * liest die Daten eines Cokumentes in ein Film Objekt ein
     *
     * @param document Dokument
     * @return Film
     */
    private static MovieSeries documentToMovieSeries(Document document) {

        MovieSeries element = new MovieSeries();
        element.setId(ID.of(document.getString("_id")));
        element.setDescription(document.getString("description"));
        element.setTitle(document.getString("title"));
        element.setSubTitle(document.getString("subTitle"));
        element.setCoverFile(document.getString("coverFile"));

        List<Document> seriesMoviesDocuments = (List<Document>) document.get("seriesMovies");
        for (Document seriesMoviesDocument : seriesMoviesDocuments) {

            SeriesMovie seriesMovie = new SeriesMovie();
            seriesMovie.setOrderId(seriesMoviesDocument.getInteger("orderId"));
            seriesMovie.setPartNumber(seriesMoviesDocument.getDouble("partNumber"));
            seriesMovie.setPartDescription(seriesMoviesDocument.getString("partDescription"));
            seriesMovie.setTimeOrder(seriesMoviesDocument.getInteger("timeOrder"));
            seriesMovie.setSeriesType(SeriesMovie.SeriesMovieType.valueOf(seriesMoviesDocument.getString("seriesType")));
            element.getSeriesMovies().add(seriesMovie);
        }

        element.resetChangedData();
        return element;
    }
}
