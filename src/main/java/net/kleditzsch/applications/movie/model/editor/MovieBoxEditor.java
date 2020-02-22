package net.kleditzsch.applications.movie.model.editor;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;
import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.smarthome.utility.datetime.DatabaseDateTimeUtil;
import net.kleditzsch.applications.movie.model.movie.MovieBox;
import net.kleditzsch.applications.movie.model.movie.meta.BoxMovie;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * Filme Verwaltung
 */
public abstract class MovieBoxEditor {

    public static final String COLLECTION = "movie.movieBox";

    /**
     * gibt eine sortierte Liste mit allen Filmboxen zurück
     *
     * @return Liste aller Filme
     */
    public static List<MovieBox> listMovieBoxes() {

        MongoCollection movieBoxCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = movieBoxCollection.find()
                .sort(Sorts.ascending("title"));

        List<MovieBox> movieBoxes = new ArrayList<>(50);
        for (Document document : iterator) {

            movieBoxes.add(documentToMovieBox(document));
        }
        return movieBoxes;
    }

    /**
     * gibt eine sortierte Liste den Filmboxen des Bereiches zurück
     *
     * @param start start Index
     * @param length länge
     * @return Liste der Filme
     */
    public static List<MovieBox> listMovieBoxes(long start, long length) {

        MongoCollection movieBoxCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = movieBoxCollection.find()
                .sort(Sorts.ascending("title"))
                .skip(((Long) start).intValue())
                .limit(((Long) length).intValue());

        List<MovieBox> movieBoxes = new ArrayList<>(50);
        for (Document document : iterator) {

            movieBoxes.add(documentToMovieBox(document));
        }
        return movieBoxes;
    }

    /**
     * sucht die Filmbox in der Datenbank und gibt das Objekt zurück
     *
     * @param id Filmbox ID
     * @return Filmbox Objekt
     */
    public static Optional<MovieBox> getMovieBox(ID id) {

        MongoCollection movieBoxCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = movieBoxCollection.find(new Document().append("_id", id.get()));
        if(iterator.first() != null) {

            return Optional.of(documentToMovieBox(iterator.first()));
        }
        return Optional.empty();
    }

    /**
     * gibt eine Liste mit Film Boxen des Sucheergebnisses zurück
     *
     * @param query Suchbegriffe
     * @return Liste mit Suchergebnissen
     */
    public static List<MovieBox> search(String query) {

        MongoCollection movieBoxCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = movieBoxCollection.find(Filters.text(query))
                .projection(Projections.metaTextScore("score"))
                .sort(Sorts.metaTextScore("score"));

        List<MovieBox> movieBoxes = new ArrayList<>(50);
        for (Document document : iterator) {

            movieBoxes.add(documentToMovieBox(document));
        }
        return movieBoxes;
    }

    /**
     * gibt die Anzahl der Filmboxen zurück
     *
     * @return Anzahl der Filmboxen
     */
    public static long countMovieBoxes() {

        MongoCollection<Document> movieBoxCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        return movieBoxCollection.countDocuments();
    }

    /**
     * speichert eine neue Filmbox in der Datenbank
     *
     * @param movieBox Filmbox
     * @return ID der Filmbox
     */
    public static ID addMovieBox(MovieBox movieBox) {

        movieBox.setId(ID.create());

        //BoxFilme vorbereiten
        List<Document> boxMovies = new ArrayList<>(movieBox.getBoxMovies().size());
        movieBox.getBoxMovies().forEach(boxMovie -> {

            Document boxMovieDocument = new Document();
            boxMovieDocument.append("movieId", boxMovie.getMovieId().get());
            boxMovieDocument.append("orderId", boxMovie.getOrderId());
            boxMovies.add(boxMovieDocument);
        });

        Document document = new Document()
                .append("_id", movieBox.getId().get())
                .append("description", movieBox.getDescription().orElse(""))
                .append("title", movieBox.getTitle())
                .append("subTitle", movieBox.getSubTitle())
                .append("search", movieBox.getTitle() + " " + movieBox.getSubTitle())
                .append("coverFile", movieBox.getCoverFile())
                .append("year", movieBox.getYear())
                .append("discId", movieBox.getDiscId().get())
                .append("price", movieBox.getPrice())
                .append("purchaseDate", movieBox.getPurchaseDate())
                .append("boxMovies", boxMovies);
        MongoCollection movieBoxCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        movieBoxCollection.insertOne(document);

        return movieBox.getId();
    }

    /**
     * aktualisiert die Daten einer Fimbox
     *
     * @param movieBox Filmbox
     * @return Erfolgsmeldung
     */
    public static boolean updateMovieBox(MovieBox movieBox) {

        //BoxFilme vorbereiten
        List<Document> boxMovies = new ArrayList<>(movieBox.getBoxMovies().size());
        movieBox.getBoxMovies().forEach(boxMovie -> {

            Document boxMovieDocument = new Document();
            boxMovieDocument.append("movieId", boxMovie.getMovieId().get());
            boxMovieDocument.append("orderId", boxMovie.getOrderId());
            boxMovies.add(boxMovieDocument);
        });

        MongoCollection movieBoxCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = movieBoxCollection.updateOne(
                eq("_id", movieBox.getId().get()),
                combine(
                        set("description", movieBox.getDescription().orElse("")),
                        set("title", movieBox.getTitle()),
                        set("subTitle", movieBox.getSubTitle()),
                        set("search", movieBox.getTitle() + " " + movieBox.getSubTitle()),
                        set("coverFile", movieBox.getCoverFile()),
                        set("year", movieBox.getYear()),
                        set("discId", movieBox.getDiscId().get()),
                        set("price", movieBox.getPrice()),
                        set("purchaseDate", movieBox.getPurchaseDate()),
                        set("boxMovies", boxMovies)
                )
        );
        return result.wasAcknowledged();
    }

    /**
     * löscht eine Filmbox
     *
     * @param movieBoxId Filmbox ID
     * @return Erfolgsmeldung
     */
    public static boolean deleteMovieBox(ID movieBoxId) {

        MongoCollection movieBoxCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        if(movieBoxCollection.deleteOne(eq("_id", movieBoxId.get())).getDeletedCount() == 1) {

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
    private static MovieBox documentToMovieBox(Document document) {

        MovieBox element = new MovieBox();
        element.setId(ID.of(document.getString("_id")));
        element.setDescription(document.getString("description"));
        element.setTitle(document.getString("title"));
        element.setSubTitle(document.getString("subTitle"));
        element.setCoverFile(document.getString("coverFile"));
        element.setYear(document.getInteger("year"));
        element.setDiscId(ID.of(document.getString("discId")));
        element.setPrice(document.get("price") instanceof Integer ? document.getInteger("price").doubleValue(): document.getDouble("price"));
        element.setPurchaseDate(DatabaseDateTimeUtil.dateToLocalDate(document.getDate("purchaseDate")));

        List<Document> boxMoviesDocuments = (List<Document>) document.get("boxMovies");
        for (Document boxMovieDocument : boxMoviesDocuments) {

            BoxMovie boxMovie = new BoxMovie();
            boxMovie.setMovieId(ID.of(boxMovieDocument.getString("movieId")));
            boxMovie.setOrderId(boxMovieDocument.getInteger("orderId"));
            element.getBoxMovies().add(boxMovie);
        }

        element.resetChangedData();
        return element;
    }
}
