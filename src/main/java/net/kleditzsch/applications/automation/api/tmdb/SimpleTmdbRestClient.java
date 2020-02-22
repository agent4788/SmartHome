package net.kleditzsch.applications.automation.api.tmdb;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kleditzsch.applications.automation.api.tmdb.data.MovieInfo;
import net.kleditzsch.applications.automation.api.tmdb.data.SearchResult;
import net.kleditzsch.applications.automation.api.tmdb.exception.TmdbException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Einfacher Client für die The Movie DB API
 */
public class SimpleTmdbRestClient {

    /**
     * API Schlüssel
     */
    private String apiKey = "";

    /**
     * Sprache
     */
    private String lang = "de-De";

    /**
     * Basis URI API
     */
    private final String BASE_REST_URI = "https://api.themoviedb.org/3/";

    /**
     * Basis URI Image
     */
    private final String BASE_IMAGE_URI = "https://image.tmdb.org/t/p/";

    /**
     * Timeout
     */
    private final Duration TIMEOUT = Duration.ofSeconds(5);

    /**
     * HTTP Client
     */
    private final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    /**
     * String Body Handler
     */
    private final HttpResponse.BodyHandler<String> asString = HttpResponse.BodyHandlers.ofString();

    /**
     * @param apiKey API Schlüssel
     * @param lang Sprache
     */
    private SimpleTmdbRestClient(String apiKey, String lang) {

        this.apiKey = apiKey;
        this.lang = lang;
    }

    /**
     * Factory
     *
     * @param apiKey API Schlüssel
     */
    public static SimpleTmdbRestClient create(String apiKey) {

        return new SimpleTmdbRestClient(apiKey, "de-De");
    }

    /**
     * Factory
     *
     * @param apiKey API Schlüssel
     * @param lang Sprache
     */
    public static SimpleTmdbRestClient create(String apiKey, String lang) {

        return new SimpleTmdbRestClient(apiKey, lang);
    }

    /**
     * Sucht einen Film in der Filmdatenbank
     *
     * @param query Suchwörter
     * @return Antwortobjekt
     */
    public SearchResult searchMovie(String query) throws IOException, InterruptedException, TmdbException {

        return searchMovie(query, 1);
    }

    /**
     * Sucht einen Film in der Filmdatenbank
     *
     * @param query Suchwörter
     * @param page Seite
     * @return Antwortobjekt
     */
    public SearchResult searchMovie(String query, int page) throws IOException, InterruptedException, TmdbException {

        String encodedQuery = URLEncoder.encode(query, "UTF-8");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create( //Set the appropriate endpoint
                        new StringBuilder(BASE_REST_URI)
                                .append("search/movie").append("?")
                                .append("api_key=").append(apiKey)
                                .append("&language=").append(lang)
                                .append("&query=").append(encodedQuery)
                                .append("&page=").append(page)
                                .append("&include_adult=").append("false")
                                .toString()))
                .timeout(TIMEOUT)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, asString);
        String jsonStr = response.body();
        JsonObject o = JsonParser.parseString(jsonStr).getAsJsonObject();

        if(response.statusCode() == 200) {

            //Erfolgreich
            SearchResult searchResult = new SearchResult();

            searchResult.setPage(o.get("page").getAsInt());
            searchResult.setResultCount(o.get("total_results").getAsInt());
            searchResult.setPages(o.get("total_pages").getAsInt());

            JsonArray results = o.getAsJsonArray("results");
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for(JsonElement e : results) {

                JsonObject result = e.getAsJsonObject();
                MovieInfo movieInfo = new MovieInfo();
                movieInfo.setId(result.get("id").getAsInt());
                movieInfo.setTitle(result.get("title").getAsString());
                if(result.get("overview") != null && !result.get("overview").isJsonNull())
                    movieInfo.setDescription(result.get("overview").getAsString());
                if(result.get("poster_path") != null && !result.get("poster_path").isJsonNull() && result.get("poster_path").getAsString().length() > 5)
                    movieInfo.setPosterPath(result.get("poster_path").getAsString());
                if(result.get("backdrop_path") != null && !result.get("backdrop_path").isJsonNull() && result.get("backdrop_path").getAsString().length() > 5)
                    movieInfo.setBackgroundPath(result.get("backdrop_path").getAsString());
                if(result.get("release_date") != null && !result.get("release_date").isJsonNull() && !result.get("release_date").getAsString().equals(""))
                    movieInfo.setReleaseDate(LocalDate.parse(result.get("release_date").getAsString(), format));
                searchResult.getResults().add(movieInfo);
            }

            return searchResult;
        } else {

            //nicht erfolgreich
            throw new TmdbException(o.get("status_message").getAsString(), o.get("status_code").getAsInt());
        }
    }

    /**
     * gibt die Detailinfos zu dem Film zurück
     *
     * @param movieId ID des Films
     * @return Filminfo Objekt
     */
    public Optional<MovieInfo> getMovieInfo(int movieId) throws IOException, InterruptedException, TmdbException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create( //Set the appropriate endpoint
                        new StringBuilder(BASE_REST_URI)
                                .append("movie/").append(movieId).append("?")
                                .append("api_key=").append(apiKey)
                                .append("&language=").append(lang)
                                .toString()))
                .timeout(TIMEOUT)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = HTTP_CLIENT.send(request, asString);
        String jsonStr = response.body();
        JsonObject result = JsonParser.parseString(jsonStr).getAsJsonObject();

        if(response.statusCode() == 200) {

            //Erfolgreich
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            MovieInfo movieInfo = new MovieInfo();
            movieInfo.setId(result.get("id").getAsInt());
            movieInfo.setTitle(result.get("title").getAsString());
            if(!result.get("overview").isJsonNull())
                movieInfo.setDescription(result.get("overview").getAsString());
            if(!result.get("poster_path").isJsonNull())
                movieInfo.setPosterPath(result.get("poster_path").getAsString());
            if(!result.get("backdrop_path").isJsonNull())
                movieInfo.setBackgroundPath(result.get("backdrop_path").getAsString());
            if(!result.get("release_date").isJsonNull())
                movieInfo.setReleaseDate(LocalDate.parse(result.get("release_date").getAsString(), format));
            if(!result.get("runtime").isJsonNull())
                movieInfo.setDuration(result.get("runtime").getAsInt());
            if(!result.get("genres").isJsonNull()) {

                JsonArray genres = result.getAsJsonArray("genres");
                for(JsonElement e : genres) {

                    JsonObject genre = e.getAsJsonObject();
                    movieInfo.getGenres().add(genre.get("name").getAsString());
                }
            }

            return Optional.of(movieInfo);
        } else if(response.statusCode() == 404) {

            return Optional.empty();
        } else {

            //nicht erfolgreich
            throw new TmdbException(result.get("status_message").getAsString(), result.get("status_code").getAsInt());
        }
    }

    /**
     * gibt die URI des Bildes zurück
     *
     * @param path Dateiname
     * @return URI
     */
    public String getImageURI(String path) {

        return BASE_IMAGE_URI + "original" + path;
    }

    /**
     * gibt die URI des Bildes zurück
     *
     * @param path Dateiname
     * @param width Breite
     * @return URI
     */
    public String getImageURI(String path, int width) {

        return BASE_IMAGE_URI + "w" + width + path;
    }

    /**
     * lädt das Bild vom Server herunter und speichert es in einer Datei
     *
     * @param path Dateiname
     * @return Erfolgsmeldung
     */
    public boolean downloadImage(String path, Path targetPath) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getImageURI(path)))
                .timeout(TIMEOUT)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse.BodyHandler<Path> asFile = HttpResponse.BodyHandlers.ofFile(targetPath);
        HttpResponse<Path> response = HTTP_CLIENT.send(request, asFile);

        if(response.statusCode() == 200) {

            return true;
        }
        return false;
    }

    /**
     * lädt das Bild vom Server herunter und speichert es in einer Datei
     *
     * @param path Dateiname
     * @param width Breite
     * @return Erfolgsmeldung
     */
    public boolean downloadImage(String path, int width, Path targetPath) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getImageURI(path, width)))
                .timeout(TIMEOUT)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse.BodyHandler<Path> asFile = HttpResponse.BodyHandlers.ofFile(targetPath);
        HttpResponse<Path> response = HTTP_CLIENT.send(request, asFile);

        if(response.statusCode() == 200) {

            return true;
        }
        return false;
    }
}
