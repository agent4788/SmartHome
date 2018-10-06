package net.kleditzsch.SmartHome.model.movie.editor;


import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.movie.Movie;
import net.kleditzsch.SmartHome.util.datetime.DatabaseDateTimeUtil;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Filme Verwaltung
 */
public abstract class MovieEditor {

    private static final String COLLECTION = "movie.movie";

    /**
     * gibt eine sortierte Liste mit allen Filmen zurück
     *
     * @return Liste aller Filme
     */
    public static List<Movie> listMovies() {

        MongoCollection movieCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable iterator = movieCollection.find()
                .sort(Sorts.ascending("title", "subTitle"));

        List<Movie> movies = new ArrayList<>(50);
        iterator.forEach((Block<Document>) document -> {

            movies.add(documentToMovie(document));
        });
        return movies;
    }

    /**
     * gibt eine Liste mit den Filmen des Bereiches zurück
     *
     * @param start start Index
     * @param length länge
     * @return Liste der Filme
     */
    public static List<Movie> listMovies(long start, long length) {

        MongoCollection movieCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable iterator = movieCollection.find()
                .sort(Sorts.ascending("title", "subTitle"))
                .skip(((Long) start).intValue())
                .limit(((Long) length).intValue());

        List<Movie> movies = new ArrayList<>(50);
        iterator.forEach((Block<Document>) document -> {

            movies.add(documentToMovie(document));
        });
        return movies;
    }

    /**
     * gibt eine Liste mit allen Filmen die zu einer Filmbox gehörenzurück
     *
     * @return Liste aller Filme in einer Filmbox
     */
    public static List<Movie> listBoxMovies() {

        MongoCollection movieCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable iterator = movieCollection.find(eq("boxId", null));

        List<Movie> movies = new ArrayList<>(50);
        iterator.forEach((Block<Document>) document -> {

            movies.add(documentToMovie(document));
        });
        return movies;
    }

    /**
     * gibt eine Liste mit allen Filmen die zu einer Filmreihe gehören zurück
     *
     * @return Liste aller Filme in einer Filmreihe
     */
    public static List<Movie> listSeriesMovies() {

        MongoCollection movieCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable iterator = movieCollection.find(eq("seriesId", null));

        List<Movie> movies = new ArrayList<>(50);
        iterator.forEach((Block<Document>) document -> {

            movies.add(documentToMovie(document));
        });
        return movies;
    }

    /**
     * sucht eine Film in der Datenbank und gibt dessen Objekt zurück
     *
     * @param id Film ID
     * @return Film Objekt
     */
    public static Optional<Movie> getMovie(ID id) {

        MongoCollection movieCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable iterator = movieCollection.find(new Document().append("_id", id.get()));
        if(iterator.first() != null) {

            return Optional.of(documentToMovie((Document) iterator.first()));
        }
        return Optional.empty();
    }

    /**
     * gibt die Aznahl der Filme zurück
     *
     * @param includeBoxMovies Filme in einer Box mit zählen
     * @return Anzahl der FIlme
     */
    public static long countMovies(boolean includeBoxMovies) {

        MongoCollection movieCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        if(includeBoxMovies) {

            return movieCollection.count();
        }
        return movieCollection.count(eq("boxId", null));
    }

    /**
     * speichert einen neuen Film in der Datenbank
     *
     * @param movie Film
     * @return ID des Films
     */
    public static ID addMovie(Movie movie) {

        //Neue ID vergeben
        movie.setId(ID.create());
        Document document = new Document()
                .append("_id", movie.getId().get())
                .append("description", movie.getDescription().orElse(""))
                .append("title", movie.getTitle())
                .append("subTitle", movie.getSubTitle())
                .append("coverFile", movie.getCoverFile())
                .append("year", movie.getYear())
                .append("discId", movie.getDiscId().get())
                .append("price", movie.getPrice())
                .append("duration", movie.getDuration())
                .append("fskId", movie.getFskId().get())
                .append("genreId", movie.getGenreId().get())
                .append("rating", movie.getRating())
                .append("purchaseDate", movie.getPurchaseDate())
                .append("directorIds", movie.getDirectorIds().stream().map(ID::get).collect(Collectors.toList()))
                .append("actorIds", movie.getActorIds().stream().map(ID::get).collect(Collectors.toList()))
                .append("boxId", movie.getBoxId().orElse(null))
                .append("seriesId", movie.getBoxId().orElse(null))
                .append("viewSoon", movie.isViewSoon());

        MongoCollection movieCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        movieCollection.insertOne(document);

        return movie.getId();
    }

    /**
     * aktuelisiert die Daten eines Films
     *
     * @param movie Film
     * @return Erfolgsmeldung
     */
    public static boolean updateMovie(Movie movie) {

        MongoCollection movieCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = movieCollection.updateOne(
                eq("_id", movie.getId().get()),
                combine(
                        set("description", movie.getDescription().orElse("")),
                        set("title", movie.getTitle()),
                        set("subTitle", movie.getSubTitle()),
                        set("coverFile", movie.getCoverFile()),
                        set("year", movie.getYear()),
                        set("discId", movie.getDiscId().get()),
                        set("price", movie.getPrice()),
                        set("duration", movie.getDuration()),
                        set("fskId", movie.getFskId().get()),
                        set("genreId", movie.getGenreId().get()),
                        set("rating", movie.getRating()),
                        set("purchaseDate", movie.getPurchaseDate()),
                        set("directorIds", movie.getDirectorIds().stream().map(ID::get).collect(Collectors.toList())),
                        set("actorIds", movie.getActorIds().stream().map(ID::get).collect(Collectors.toList())),
                        set("boxId", movie.getBoxId().orElse(null)),
                        set("seriesId", movie.getSeriesId().orElse(null)),
                        set("viewSoon", movie.isViewSoon())
                )
        );
        return result.wasAcknowledged();
    }

    /**
     * aktuelisiert die Bewertung des Films
     *
     * @param movieId Film ID
     * @param newRating neue Bewertung
     * @return Erfolgsmeldung
     */
    public static boolean updateRating(ID movieId, int newRating) {

        MongoCollection movieCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = movieCollection.updateOne(
                eq("_id", movieId.get()),
                combine(
                        set("rating", newRating)
                )
        );
        return result.wasAcknowledged();
    }

    /**
     * aktuelisiert die "demnächst Anschauen" markierung
     *
     * @param movieId Film ID
     * @param viewSoon demnächst Anschauen
     * @return Erfolgsmeldung
     */
    public static boolean updateViewSoon(ID movieId, boolean viewSoon) {

        MongoCollection movieCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = movieCollection.updateOne(
                eq("_id", movieId.get()),
                combine(
                        set("viewSoon", viewSoon)
                )
        );
        return result.wasAcknowledged();
    }

    /**
     * löscht einen Film
     *
     * @param movieId Film ID
     * @return Erfolgsmeldung
     */
    public static boolean deleteMovie(ID movieId) {

        MongoCollection movieCollection = Application.getInstance().getDatabaseCollection(COLLECTION);
        if(movieCollection.deleteOne(eq("_id", movieId.get())).getDeletedCount() == 1) {

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
    private static Movie documentToMovie(Document document) {

        Movie element = new Movie();
        element.setId(ID.of(document.getString("_id")));
        element.setDescription(document.getString("description"));
        element.setTitle(document.getString("title"));
        element.setSubTitle(document.getString("subTitle"));
        element.setCoverFile(document.getString("coverFile"));
        element.setYear(document.getInteger("year"));
        element.setDiscId(ID.of(document.getString("discId")));
        element.setPrice(document.getDouble("price"));
        element.setDuration(document.getInteger("duration"));
        element.setFskId(ID.of(document.getString("fskId")));
        element.setGenreId(ID.of(document.getString("genreId")));
        element.setRating(document.getInteger("rating"));
        element.setPurchaseDate(DatabaseDateTimeUtil.dateToLocallDate(document.getDate("purchaseDate")));
        List<String> directorIds = (List<String>) document.get("directorIds");
        element.getDirectorIds().addAll(directorIds.stream().map(ID::of).collect(Collectors.toList()));
        List<String> actorIds = (List<String>) document.get("actorIds");
        element.getActorIds().addAll(actorIds.stream().map(ID::of).collect(Collectors.toList()));
        if(document.getString("boxId") != null) {

            element.setGenreId(ID.of(document.getString("boxId")));
        }
        if(document.getString("seriesId") != null) {

            element.setGenreId(ID.of(document.getString("seriesId")));
        }
        element.setViewSoon(document.getBoolean("viewSoon"));

        element.resetChangedData();
        return element;
    }
}