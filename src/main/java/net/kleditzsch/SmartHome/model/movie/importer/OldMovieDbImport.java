package net.kleditzsch.SmartHome.model.movie.importer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OldMovieDbImport {

    /**
     * Film Box
     */
    public static class OldMovieBox {

        private Map<String, String> boxData = new HashMap<>();
        private List<Map<String, String>> boxMovies = new ArrayList<>();

        public Map<String, String> getBoxData() {
            return boxData;
        }

        public void setBoxData(Map<String, String> boxData) {
            this.boxData = boxData;
        }

        public List<Map<String, String>> getBoxMovies() {
            return boxMovies;
        }

        public void setBoxMovies(List<Map<String, String>> boxMovies) {
            this.boxMovies = boxMovies;
        }
    }

    /**
     * liest alle Filme aus der Json Datei aus
     *
     * @param moviesJsonFile Json Datei
     * @return Liste der Filme
     */
    public static List<Map<String, String>> listMovies(Path moviesJsonFile) throws IOException {

        String json = Files.readString(moviesJsonFile);
        JsonElement je = new JsonParser().parse(json);

        JsonArray moviesJson = je.getAsJsonArray();
        List<Map<String, String>> movies = new ArrayList<>();
        for (JsonElement movieJsonElement : moviesJson) {

            JsonObject movieJson = movieJsonElement.getAsJsonObject();
            Map<String, String> movie = new HashMap<>();
            movie.put("id", movieJson.get("_id").getAsString());
            movie.put("title", movieJson.get("_title").getAsString());
            movie.put("subTitle", movieJson.get("_subTitle").getAsString());
            movie.put("description", movieJson.get("_description").getAsString());
            movie.put("coverImg", movieJson.get("_coverImg").getAsString());
            movie.put("year", movieJson.get("_year").getAsString());
            movie.put("disc", movieJson.get("_disc").getAsString());
            movie.put("price", movieJson.get("_price").getAsString());
            movie.put("duration", movieJson.get("_duration").getAsString());
            movie.put("fsk", movieJson.get("_fsk").getAsString());
            movie.put("genre", movieJson.get("_genre").getAsString());
            movie.put("rating", movieJson.get("_rating").getAsString());
            movie.put("registredDate", movieJson.get("_registredDate").getAsString());
            movie.put("type", movieJson.get("_type").getAsString());

            //Regiseure
            if(movieJson.keySet().contains("_directors")) {

                JsonArray directors = movieJson.get("_directors").getAsJsonArray();
                StringBuilder directorStr = new StringBuilder();
                String separator = "";
                for(JsonElement director : directors) {

                    directorStr.append(separator).append(director.getAsString());
                    separator = ";;";
                }
                movie.put("directors", directorStr.toString());
            } else {

                movie.put("directors", "");
            }

            //Schauspieler
            if(movieJson.keySet().contains("_actors")) {

                JsonArray actors = movieJson.get("_actors").getAsJsonArray();
                StringBuilder actorStr = new StringBuilder();
                String separator = "";
                for(JsonElement actor : actors) {

                    actorStr.append(separator).append(actor.getAsString());
                    separator = ";;";
                }
                movie.put("actors", actorStr.toString());
            } else {

                movie.put("actors", "");
            }


            movies.add(movie);
        }
        return movies;
    }

    /**
     * list alle Filmboxen aus der Json Datei
     *
     * @param movieBoxesJsonFile Json Datei
     * @return Liste der Filmboxen
     */
    public static List<OldMovieBox> listMovieBoxes(Path movieBoxesJsonFile) throws IOException {

        String json = Files.readString(movieBoxesJsonFile);
        JsonElement je = new JsonParser().parse(json);

        JsonArray movieBoxesJson = je.getAsJsonArray();
        List<OldMovieBox> movieBoxes = new ArrayList<>();
        for (JsonElement movieBoxJsonElement : movieBoxesJson) {

            OldMovieBox oldMovieBox = new OldMovieBox();

            //Box Daten
            JsonObject movieBoxJson = movieBoxJsonElement.getAsJsonObject();
            Map<String, String> movieBox = new HashMap<>();
            movieBox.put("id", movieBoxJson.get("_id").getAsString());
            movieBox.put("title", movieBoxJson.get("_title").getAsString());
            movieBox.put("subTitle", movieBoxJson.get("_subTitle").getAsString());
            movieBox.put("coverImg", movieBoxJson.get("_coverImg").getAsString());
            movieBox.put("year", movieBoxJson.get("_year").getAsString());
            movieBox.put("disc", movieBoxJson.get("_disc").getAsString());
            movieBox.put("price", movieBoxJson.get("_price").getAsString());
            oldMovieBox.setBoxData(movieBox);

            //Filme
            for(JsonElement boxMovieJsonElement : movieBoxJson.get("_movies").getAsJsonArray()) {

                Map<String, String> boxMovie = new HashMap<>();
                JsonObject boxMovieJson = boxMovieJsonElement.getAsJsonObject();
                boxMovie.put("id", boxMovieJson.get("_id").getAsString());
                boxMovie.put("title", boxMovieJson.get("_title").getAsString());
                boxMovie.put("subTitle", boxMovieJson.get("_subTitle").getAsString());
                boxMovie.put("description", boxMovieJson.get("_description").getAsString());
                boxMovie.put("coverImg", boxMovieJson.get("_coverImg").getAsString());
                boxMovie.put("year", boxMovieJson.get("_year").getAsString());
                boxMovie.put("disc", boxMovieJson.get("_disc").getAsString());
                boxMovie.put("price", boxMovieJson.get("_price").getAsString());
                boxMovie.put("duration", boxMovieJson.get("_duration").getAsString());
                boxMovie.put("fsk", boxMovieJson.get("_fsk").getAsString());
                boxMovie.put("genre", boxMovieJson.get("_genre").getAsString());
                boxMovie.put("rating", boxMovieJson.get("_rating").getAsString());
                boxMovie.put("registredDate", boxMovieJson.get("_registredDate").getAsString());
                boxMovie.put("type", boxMovieJson.get("_type").getAsString());

                //Regiseure
                if(boxMovieJson.keySet().contains("_directors")) {

                    JsonArray directors = boxMovieJson.get("_directors").getAsJsonArray();
                    StringBuilder directorStr = new StringBuilder();
                    String separator = "";
                    for(JsonElement director : directors) {

                        directorStr.append(separator).append(director.getAsString());
                        separator = ";;";
                    }
                    boxMovie.put("directors", directorStr.toString());
                } else {

                    boxMovie.put("directors", "");
                }

                //Schauspieler
                if(boxMovieJson.keySet().contains("_actors")) {

                    JsonArray actors = boxMovieJson.get("_actors").getAsJsonArray();
                    StringBuilder actorStr = new StringBuilder();
                    String separator = "";
                    for(JsonElement actor : actors) {

                        actorStr.append(separator).append(actor.getAsString());
                        separator = ";;";
                    }
                    boxMovie.put("actors", actorStr.toString());
                } else {

                    boxMovie.put("actors", "");
                }

                oldMovieBox.getBoxMovies().add(boxMovie);
            }
            movieBoxes.add(oldMovieBox);
        }
        return movieBoxes;
    }

    /**
     * speichert die bereits importierten IDs in eine Textliste
     *
     * @param importedIds Liste mit den IDs
     * @param file Zieldatei
     */
    public static void dumpImportedIdLsit(List<String> importedIds, Path file) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.toFile(), false)))) {

            for(String id : importedIds) {

                writer.write(id + "\n");
            }
        }
    }

    /**
     * liest die Liste mit den bereits importierten IDs aus der Textdate
     *
     * @param file Datei
     * @return Liste mit den IDs
     */
    public static List<String> readImportedIdList(Path file) throws IOException {

        if (Files.exists(file)) {

            List<String> lines = Files.readAllLines(file);
            lines.forEach(l -> l = l.trim());
            return lines;
        }
        return new ArrayList<>();
    }
}
