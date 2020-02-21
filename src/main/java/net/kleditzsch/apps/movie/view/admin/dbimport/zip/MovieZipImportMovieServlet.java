package net.kleditzsch.apps.movie.view.admin.dbimport.zip;

import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.model.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.settings.Interface.Settings;
import net.kleditzsch.SmartHome.utility.form.FormValidation;
import net.kleditzsch.SmartHome.utility.image.UploadUtil;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import net.kleditzsch.apps.automation.api.tmdb.SimpleTmdbRestClient;
import net.kleditzsch.apps.automation.api.tmdb.data.SearchResult;
import net.kleditzsch.apps.automation.api.tmdb.exception.TmdbException;
import net.kleditzsch.apps.movie.model.editor.*;
import net.kleditzsch.apps.movie.model.importer.OldMovieDbImport;
import net.kleditzsch.apps.movie.model.movie.Movie;
import net.kleditzsch.apps.movie.model.movie.meta.Disc;
import net.kleditzsch.apps.movie.model.movie.meta.FSK;
import net.kleditzsch.apps.movie.model.movie.meta.Genre;
import net.kleditzsch.apps.movie.model.movie.meta.Person;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MovieZipImportMovieServlet extends HttpServlet {

    public static Map<String, String> discs;
    public static Map<String, String> fsk;
    public static Map<String, String> genres;
    static {

        //alt -> neu
        Map<String, String> discList = new HashMap<>();
        discList.put("DVD", "DVD");
        discList.put("Blu-ray", "Blu-ray");
        discList.put("3D Blu-ray", "3D Blu-ray");
        discList.put("4K UHD Blu-ray", "4K UHD Blu-ray");
        discList.put("SD Datei", "SD Datei");
        discList.put("HD Datei", "HD Datei");
        discList.put("4K UHD Datei", "4K UHD Datei");
        discs = Collections.unmodifiableMap(discList);

        Map<String, String> fskList = new HashMap<>();
        fskList.put("0", "Ohne Altersbeschränkung");
        fskList.put("6", "ab 6 Jahre");
        fskList.put("12", "ab 12 Jahre");
        fskList.put("16", "ab 16 Jahre");
        fskList.put("18", "ab 18 Jahre");
        fsk = Collections.unmodifiableMap(fskList);

        Map<String, String> genreList = new HashMap<>();
        genreList.put("Action", "Action");
        genreList.put("Comedy", "Comedy");
        genreList.put("Dokumentation", "Dokumentation");
        genreList.put("Drama", "Drama");
        genreList.put("Fantasy", "Fantasy");
        genreList.put("Horror", "Horror");
        genreList.put("Kriegsfilm", "Kriegsfilm");
        genreList.put("Mafia", "Mafia");
        genreList.put("Musik", "Musik");
        genreList.put("Märchen", "Märchen");
        genreList.put("Serien", "Serien");
        genreList.put("Si-Fi", "Si-Fi");
        genreList.put("Thriller", "Thriller");
        genreList.put("Western", "Western");
        genreList.put("Zeichentrick", "Zeichentrick");
        genres = Collections.unmodifiableMap(genreList);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/dbimport/zip/movie.html");
        JtwigModel model = JtwigModel.newModel();

        //Einstellungen laden
        String tmdbApiKey = "";
        SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        tmdbApiKey = settingsEditor.getStringSetting(Settings.MOVIE_TMDB_API_KEY).getValue();
        settingsLock.unlock();
        model.with("tmdbApiKey", tmdbApiKey);

        Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir")).resolve("movieImport");
        Path importedIdsFile = tmpDir.resolve("imported.txt");
        if(Files.exists(tmpDir)) {

            //Daten laden
            List<Map<String, String>> movies = OldMovieDbImport.listMovies(tmpDir.resolve("movies.json"));
            List<String> importedIds = OldMovieDbImport.readImportedIdList(importedIdsFile);

            //nächsten Film importieren
            Map<String, String> importMovie = null;
            for(Map<String, String> importMovieEntry : movies) {

                if(importMovieEntry.get("type").equals("1") && !importedIds.contains(importMovieEntry.get("id"))) {

                    importMovie = importMovieEntry;
                    break;
                }
            }

            if(importMovie != null) {

                //import
                final Map<String, String> finalImportMovie = importMovie;
                model.with("movie", finalImportMovie);
                model.with("purchaseDate", LocalDate.parse(finalImportMovie.get("registredDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                //Medium umsetzen
                DiscEditor discEditor = DiscEditor.createAndLoad();
                model.with("disc", discEditor.getData().stream()
                        .filter(disc -> disc.getName().equalsIgnoreCase(discs.get(finalImportMovie.get("disc"))))
                        .findFirst()
                        .orElse(null)
                );

                //FSK umsetzen
                FskEditor fskEditor = FskEditor.createAndLoad();
                model.with("fsk", fskEditor.getData().stream()
                        .filter(fsk -> fsk.getName().equalsIgnoreCase(this.fsk.get(finalImportMovie.get("fsk"))))
                        .findFirst()
                        .orElse(null)
                );

                //Genre umsetzen
                GenreEditor genreEditor = GenreEditor.createAndLoad();
                model.with("genre", genreEditor.getData().stream()
                        .filter(genre -> genre.getName().equalsIgnoreCase(genres.get(finalImportMovie.get("genre"))))
                        .findFirst()
                        .orElse(null)
                );

                //Schauspieler / Regiseure umsetzen (ggf. erstellen wenn nicht vorhanden
                PersonEditor personEditor = PersonEditor.createAndLoad();
                List<ID> directorIds = new ArrayList<>();
                if(finalImportMovie.get("directors").length() > 0) {

                    List<String> directors = Arrays.asList(finalImportMovie.get("directors").split(";;"));
                    directors.forEach(director -> {

                        //Person suchen, wenn nicht vorhanden erstellen
                        Optional<Person> personOptional = personEditor.getData().stream()
                                .filter(person -> person.getName().equalsIgnoreCase(director))
                                .findFirst();

                        if(personOptional.isPresent()) {

                            directorIds.add(personOptional.get().getId());
                        } else {

                            Person person = new Person();
                            person.setId(ID.create());
                            person.setName(director);
                            ID newPersonId = personEditor.add(person);
                            directorIds.add(newPersonId);
                        }
                    });
                }
                model.with("directorIds", directorIds);
                List<ID> actorIds = new ArrayList<>();
                if(finalImportMovie.get("actors").length() > 0) {

                    List<String> actors = Arrays.asList(finalImportMovie.get("actors").split(";;"));
                    actors.forEach(actor -> {

                        //Person suchen, wenn nicht vorhanden erstellen
                        Optional<Person> personOptional = personEditor.getData().stream()
                                .filter(person -> person.getName().equalsIgnoreCase(actor))
                                .findFirst();

                        if(personOptional.isPresent()) {

                            actorIds.add(personOptional.get().getId());
                        } else {

                            Person person = new Person();
                            person.setId(ID.create());
                            person.setName(actor);
                            ID newPersonId = personEditor.add(person);
                            actorIds.add(newPersonId);
                        }
                    });
                }
                model.with("actorIds", actorIds);

                //Bewertung umsetzen
                model.with("rating", finalImportMovie.get("rating").equals("1") ? "0" : finalImportMovie.get("rating"));

                model.with("personList", PersonEditor.createAndLoad().getData().stream().sorted(Comparator.comparing(Person::getName)).collect(Collectors.toList()));
                model.with("discList", discEditor.getData().stream().sorted(Comparator.comparingInt(Disc::getOrderId)).collect(Collectors.toList()));
                model.with("fskList", fskEditor.getData().stream().sorted(Comparator.comparingInt(FSK::getLevel)).collect(Collectors.toList()));
                model.with("genreList", genreEditor.getData().stream().sorted(Comparator.comparing(Genre::getName)).collect(Collectors.toList()));
                model.with("yearList", IntStream.rangeClosed(1900, LocalDate.now().getYear()).boxed().sorted(Comparator.reverseOrder()).collect(Collectors.toList()));

                //TMDB Suchanfrage
                if(tmdbApiKey.length() > 0) {

                    SimpleTmdbRestClient tmdb = SimpleTmdbRestClient.create(tmdbApiKey);
                    try {

                        Map<String, String> coverUrlList = new HashMap<>();
                        SearchResult result = tmdb.searchMovie(finalImportMovie.get("title") + " " + finalImportMovie.get("subTitle"));
                        if(result.getResults().size() > 0) {

                            result.getResults().forEach(r -> {

                                if(r.getPosterPath().isPresent()) {

                                    coverUrlList.put(r.getPosterPath().get(), tmdb.getImageURI(r.getPosterPath().get()));
                                }
                            });

                            //weitere Seiten abfragen
                            if(result.getPages() > 1) {

                                int pages = result.getPages() > 5 ? 5 : result.getPages();
                                for(int i = 2; i <= pages; i++) {

                                    SearchResult pageResult = tmdb.searchMovie(finalImportMovie.get("title") + " " + finalImportMovie.get("subTitle"), i);
                                    pageResult.getResults().forEach(r -> {

                                        if(r.getPosterPath().isPresent()) {

                                            coverUrlList.put(r.getPosterPath().get(), tmdb.getImageURI(r.getPosterPath().get()));
                                        }
                                    });
                                }
                            }
                        } else {

                            SearchResult altResult = tmdb.searchMovie(finalImportMovie.get("title"));
                            altResult.getResults().forEach(r -> {

                                if(r.getPosterPath().isPresent()) {

                                    coverUrlList.put(r.getPosterPath().get(), tmdb.getImageURI(r.getPosterPath().get()));
                                }
                            });

                            //weitere Seiten abfragen
                            if(altResult.getPages() > 1) {

                                int pages = result.getPages() > 5 ? 5 : result.getPages();
                                for(int i = 2; i <= pages; i++) {

                                    SearchResult pageResult = tmdb.searchMovie(finalImportMovie.get("title"), i);
                                    pageResult.getResults().forEach(r -> {

                                        if(r.getPosterPath().isPresent()) {

                                            coverUrlList.put(r.getPosterPath().get(), tmdb.getImageURI(r.getPosterPath().get()));
                                        }
                                    });
                                }
                            }
                        }
                        model.with("coverUrlList", coverUrlList);

                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    } catch (TmdbException e) {

                        e.printStackTrace();
                    }
                }
            } else {

                //keine weitere Daten zum importieren -> zu den Filmboxen weiter leiten
                resp.sendRedirect("/movie/admin/zipimport/moviebox");
            }
        } else {

            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Daten");
        }

        //Meldung
        if(req.getSession().getAttribute("success") != null && req.getSession().getAttribute("message") != null) {

            model.with("success", req.getSession().getAttribute("success"));
            model.with("message", req.getSession().getAttribute("message"));
            req.getSession().removeAttribute("success");
            req.getSession().removeAttribute("message");
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Einstellungen laden
        String tmdbApiKey = "";
        SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        tmdbApiKey = settingsEditor.getStringSetting(Settings.MOVIE_TMDB_API_KEY).getValue();
        settingsLock.unlock();

        List<ID> actors = null, directors = null;

        FormValidation form = FormValidation.create(req);
        String oldId = form.getString("id", "Alte ID", 10, 50);
        String title = form.getString("title", "Titel", 3, 50);
        String subTitle = form.getString("subtitle", "Untertietel", 0, 50);
        String description = form.getString("description", "Beschreibung", 3, 100_000);
        String cover = form.getString("cover", "Cover", 3, 255);
        int year = form.getInteger("year", "Produktionsjahr", 1900, LocalDate.now().getYear());
        ID discID = form.getId("disc", "Medium");
        double price = form.getDouble("price", "Preis", 0.0, 1000.0);
        int duration = form.getInteger("duration", "Laufzeit", 1, 15_000);
        ID fskID = form.getId("fsk", "Altersfreigabe");
        ID genreID = form.getId("genre", "Genre");
        int rating = form.getInteger("rating", "Bewertung", 0, 5);
        LocalDate purchaseDate = form.getLocalDate("purchaseDate", "Kaufdatum", DateTimeFormatter.ofPattern("yyyy-MM-dd"), LocalDate.of(1900, 01, 01), LocalDate.now());
        if(form.fieldNotEmpty("actors")) {

            actors = form.getIDList("actors", "Schauspieler");
        }
        if(form.fieldNotEmpty("directors")) {

            directors = form.getIDList("directors", "Regisseur");
        }

        //IDs prüfen
        if(!FskEditor.createAndLoad().getById(fskID).isPresent()) {

            form.setInvalid("fsk", "Ungültige FSK ID");
        }
        if(!GenreEditor.createAndLoad().getById(genreID).isPresent()) {

            form.setInvalid("genre", "Ungültige Genre ID");
        }
        if(!DiscEditor.createAndLoad().getById(discID).isPresent()) {

            form.setInvalid("disc", "Ungültige Disc ID");
        }
        PersonEditor personEditor = PersonEditor.createAndLoad();
        if(actors != null && actors.size() > 0) {

            for(ID actorId : actors) {

                if(!personEditor.getById(actorId).isPresent()) {

                    form.setInvalid("actors", "Ungültige Schauspieler ID");
                    break;
                }
            }
        }
        if(directors != null && directors.size() > 0) {

            for(ID directorId : directors) {

                if(!personEditor.getById(directorId).isPresent()) {

                    form.setInvalid("directors", "Ungültige Regiseur ID");
                    break;
                }
            }
        }

        if(form.isSuccessful()) {

            Movie movie = new Movie();
            movie.setId(ID.create());
            movie.setTitle(title);
            movie.setSubTitle(subTitle);
            movie.setDescription(description);
            movie.setYear(year);
            movie.setDiscId(discID);
            movie.setPrice(price);
            movie.setDuration(duration);
            movie.setFskId(fskID);
            movie.setGenreId(genreID);
            movie.setRating(rating);
            movie.setPurchaseDate(purchaseDate);
            movie.getActorIds().clear();
            if(actors != null) {

                movie.getActorIds().addAll(actors);
            }
            movie.getDirectorIds().clear();
            if(directors != null) {

                movie.getDirectorIds().addAll(directors);
            }

            //Cover
            Path uploadDir = Paths.get("upload/cover");
            if(cover.startsWith("origin://")) {

                //Cover der alten Datenbank verwenden
                String oldCoverFileName = cover.substring(9);
                String newFilenName = ID.create().get() + cover.substring(cover.indexOf("."));
                Files.copy(
                        Paths.get(System.getProperty("java.io.tmpdir")).resolve("movieImport").resolve("cover").resolve(oldCoverFileName),
                        uploadDir.resolve(newFilenName)
                );
                movie.setCoverFile(newFilenName);

            } else {

                //TMDB Cover
                Path tmpDir = Paths.get("upload/tmp");
                if(!Files.exists(tmpDir)) {

                    Files.createDirectories(tmpDir);
                }

                try {

                    //Datei herunterladen
                    SimpleTmdbRestClient tmdb = SimpleTmdbRestClient.create(tmdbApiKey);
                    Path file = tmpDir.resolve(cover.substring(1));
                    tmdb.downloadImage(cover, file);

                    //neuen Dateinamen erstellen und COntent Type prüfen
                    String filename = ID.create().get();
                    String contentType = Files.probeContentType(file);
                    if(UploadUtil.fileTypes.containsKey(contentType)) {

                        filename += UploadUtil.fileTypes.get(contentType);
                    } else {

                        throw new IOException("Ungültiger Content Type: " + contentType);
                    }

                    //Datei in Cover Ordner verschieben und Dateinem im Filmobjekt speichern
                    Files.move(file, uploadDir.resolve(filename));
                    movie.setCoverFile(filename);
                } catch (InterruptedException | IllegalStateException e) {

                    e.printStackTrace();
                }
            }

            //Film speichern
            MovieEditor.addMovie(movie);

            //Import Speichern
            Path importedIdsFile = Paths.get(System.getProperty("java.io.tmpdir")).resolve("movieImport").resolve("imported.txt");
            List<String> ids = OldMovieDbImport.readImportedIdList(importedIdsFile);
            ids.add(oldId);
            OldMovieDbImport.dumpImportedIdLsit(ids, importedIdsFile);

            //weiter zum nächsten Film
            req.getSession().setAttribute("success", true);
            req.getSession().setAttribute("message", "Der Film \"" + movie.getTitle() + (!movie.getSubTitle().isBlank() ? " - " + movie.getSubTitle() : "") + "\" wurde erfolgreich importiert");
            resp.sendRedirect("/movie/admin/zipimport/movie");
        } else {

            //ungültige Formulardaten
            req.getSession().setAttribute("success", true);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/movie/admin/zipimport/movie");
        }
    }
}
