package net.kleditzsch.SmartHome.view.movie.admin.dbimport.zip;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.StringSetting;
import net.kleditzsch.SmartHome.model.movie.editor.*;
import net.kleditzsch.SmartHome.model.movie.importer.OldMovieDbImport;
import net.kleditzsch.SmartHome.model.movie.movie.Movie;
import net.kleditzsch.SmartHome.model.movie.movie.MovieBox;
import net.kleditzsch.SmartHome.model.movie.movie.meta.*;
import net.kleditzsch.SmartHome.util.api.tmdb.SimpleTmdbRestClient;
import net.kleditzsch.SmartHome.util.api.tmdb.data.SearchResult;
import net.kleditzsch.SmartHome.util.api.tmdb.exception.TmdbException;
import net.kleditzsch.SmartHome.util.form.FormValidation;
import net.kleditzsch.SmartHome.util.image.UploadUtil;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
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

public class MovieZipImportMovieBoxServlet extends HttpServlet {

    public static Map<String, String> discs;
    public static Map<String, String> fsk;
    public static Map<String, String> genres;
    static {

        //alt -> neu
        Map<String, String> discList = new HashMap<>();
        discList.put("DVD", "DVD");
        discList.put("Blu-ray", "Blu ray");
        discList.put("3D Blu-ray", "3D Blu ray");
        discList.put("4K UHD Blu-ray", "4K UHD Blu ray");
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
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/dbimport/zip/moviebox.html");;
        JtwigModel model = JtwigModel.newModel();

        //Einstellungen laden
        String tmdbApiKey = "";
        SettingsEditor settingsEditor = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        Optional<StringSetting> tmdbApiKeyOptional = settingsEditor.getStringSetting(SettingsEditor.MOVIE_TMDB_API_KEY);
        if (tmdbApiKeyOptional.isPresent()) {

            tmdbApiKey = tmdbApiKeyOptional.get().getValue();
        }
        settingsLock.unlock();
        model.with("tmdbApiKey", tmdbApiKey);

        Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir")).resolve("movieImport");
        Path importedIdsFile = tmpDir.resolve("importedBoxes.txt");
        if(Files.exists(tmpDir)) {

            //Daten laden
            List<OldMovieDbImport.OldMovieBox> movieBoxes = OldMovieDbImport.listMovieBoxes(tmpDir.resolve("movieBoxes.json"));
            List<String> importedIds = OldMovieDbImport.readImportedIdList(importedIdsFile);

            if(req.getParameter("oldid") != null && req.getParameter("newid") != null) {

                //Box Filme importieren
                template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/dbimport/zip/movieboxmovie.html");

                //Filmbox suchen
                String oldId = req.getParameter("oldid");
                ID newId = ID.of(req.getParameter("newid"));
                OldMovieDbImport.OldMovieBox oldMovieBox = null;
                for(OldMovieDbImport.OldMovieBox box : movieBoxes) {

                    if(box.getBoxData().get("id").equals(oldId)) {

                        oldMovieBox = box;
                        break;
                    }
                }

                if(oldMovieBox != null) {

                    //Box gefunden -> nächsten Film zum import suchen
                    Map<String, String> boxMovie = null;
                    for(Map<String, String> bm : oldMovieBox.getBoxMovies()) {

                        if(!importedIds.contains(bm.get("id"))) {

                            //Film Importieren
                            boxMovie = bm;
                            break;
                        }
                    }

                    if(boxMovie != null) {

                        //import
                        model.with("oldBoxId", oldId);
                        model.with("newBoxId", newId.get());

                        final Map<String, String> finalImportMovie = boxMovie;
                        model.with("movie", finalImportMovie);

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

                        //keine weiteren Filme zum importieren -> weiter zur nächsten Box
                        resp.sendRedirect("/movie/admin/zipimport/moviebox");
                    }
                } else {

                    //Box nicht gefunden -> weiter zur nächsten Box
                    resp.sendRedirect("/movie/admin/zipimport/moviebox");
                }

            } else {

                //nächste Filmbox importieren
                OldMovieDbImport.OldMovieBox oldMovieBox = null;
                for(OldMovieDbImport.OldMovieBox box : movieBoxes) {

                    if(!importedIds.contains(box.getBoxData().get("id"))) {

                        oldMovieBox = box;
                        break;
                    }
                }

                if(oldMovieBox != null) {

                    //Box Daten importieren
                    final OldMovieDbImport.OldMovieBox finalOldMovieBox = oldMovieBox;
                    model.with("movieBox", finalOldMovieBox.getBoxData());
                    if(finalOldMovieBox.getBoxMovies().size() > 0) {

                        String registredDate = finalOldMovieBox.getBoxMovies().get(0).get("registredDate");
                        model.with("purchaseDate", LocalDate.parse(registredDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    } else {

                        model.with("purchaseDate", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    }

                    //Medium umsetzen
                    DiscEditor discEditor = DiscEditor.createAndLoad();
                    model.with("disc", discEditor.getData().stream()
                            .filter(disc -> disc.getName().equalsIgnoreCase(discs.get(finalOldMovieBox.getBoxData().get("disc"))))
                            .findFirst()
                            .orElse(null)
                    );

                    model.with("discList", discEditor.getData().stream().sorted(Comparator.comparingInt(Disc::getOrderId)).collect(Collectors.toList()));
                    model.with("yearList", IntStream.rangeClosed(1900, LocalDate.now().getYear()).boxed().sorted(Comparator.reverseOrder()).collect(Collectors.toList()));
                } else {

                    //keine weiteren Boxen zum Import vorhanden -> zur Abschlusseite umleiten
                    resp.sendRedirect("/movie/admin/zipimport/final");
                }
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
        SettingsEditor settingsEditor = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        Optional<StringSetting> tmdbApiKeyOptional = settingsEditor.getStringSetting(SettingsEditor.MOVIE_TMDB_API_KEY);
        if (tmdbApiKeyOptional.isPresent()) {

            tmdbApiKey = tmdbApiKeyOptional.get().getValue();
        }
        settingsLock.unlock();

        FormValidation form = FormValidation.create(req);
        if(form.getString("type", "Typ").equals("box")) {

            //Filmbox
            String oldId = form.getString("id", "Alte ID", 10, 50);
            String title = form.getString("title", "Titel", 3, 50);
            String subTitle = form.getString("subtitle", "Untertietel", 0, 50);
            String cover = form.getString("cover", "Cover", 3, 255);
            int year = form.getInteger("year", "Produktionsjahr", 1900, LocalDate.now().getYear());
            ID discID = form.getId("disc", "Medium");
            double price = form.getDouble("price", "Preis", 0.0, 1000.0);
            LocalDate purchaseDate = form.getLocalDate("purchaseDate", "Kaufdatum", DateTimeFormatter.ofPattern("yyyy-MM-dd"), LocalDate.of(1900, 01, 01), LocalDate.now());

            if(form.isSuccessful()) {

                MovieBox movieBox = new MovieBox();
                movieBox.setId(ID.create());
                movieBox.setTitle(title);
                movieBox.setSubTitle(subTitle);
                movieBox.setYear(year);
                movieBox.setDiscId(discID);
                movieBox.setPrice(price);
                movieBox.setPurchaseDate(purchaseDate);

                //Cover
                Path uploadDir = Paths.get("upload/cover");
                if(!cover.equals("none")) {

                    //Cover der alten Datenbank verwenden
                    String oldCoverFileName = cover;
                    String newFilenName = ID.create().get() + cover.substring(cover.indexOf("."));
                    Files.copy(
                            Paths.get(System.getProperty("java.io.tmpdir")).resolve("movieImport").resolve("cover").resolve(oldCoverFileName),
                            uploadDir.resolve(newFilenName)
                    );
                    movieBox.setCoverFile(newFilenName);
                }

                //Filmbox speichern
                ID newId = MovieBoxEditor.addMovieBox(movieBox);

                //Import Speichern
                Path importedIdsFile = Paths.get(System.getProperty("java.io.tmpdir")).resolve("movieImport").resolve("importedBoxes.txt");
                List<String> ids = OldMovieDbImport.readImportedIdList(importedIdsFile);
                ids.add(oldId);
                OldMovieDbImport.dumpImportedIdLsit(ids, importedIdsFile);

                //zum Filme Import weiter leiten
                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Ddie Filmbox \"" + movieBox.getTitle() + (!movieBox.getSubTitle().isBlank() ? " - " + movieBox.getSubTitle() : "") + "\" wurde erfolgreich importiert");
                resp.sendRedirect("/movie/admin/zipimport/moviebox?oldid=" + oldId + "&newid=" + newId.get());
            }
        } else {

            List<ID> actors = null, directors = null;

            //Film einer Filmbox
            String oldid = form.getString("oldid", "Alte ID", 10, 50);
            String oldmovieid = form.getString("oldmovieid", "Alte ID", 10, 50);
            ID newid = form.getId("newid", "Neue ID");
            String title = form.getString("title", "Titel", 3, 50);
            String subTitle = form.getString("subtitle", "Untertietel", 0, 50);
            String description = form.getString("description", "Beschreibung", 3, 100_000);
            String cover = form.getString("cover", "Cover", 3, 255);
            int year = form.getInteger("year", "Produktionsjahr", 1900, LocalDate.now().getYear());
            ID discID = form.getId("disc", "Medium");
            int duration = form.getInteger("duration", "Laufzeit", 1, 15_000);
            ID fskID = form.getId("fsk", "Altersfreigabe");
            ID genreID = form.getId("genre", "Genre");
            int rating = form.getInteger("rating", "Bewertung", 0, 5);
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

                //Filmbox aus der Datenbank laden
                Optional<MovieBox> movieBoxOptional = MovieBoxEditor.getMovieBox(newid);
                if(movieBoxOptional.isPresent()) {

                    MovieBox movieBox = movieBoxOptional.get();

                    //Film Speichern
                    Movie movie = new Movie();
                    movie.setId(ID.create());
                    movie.setTitle(title);
                    movie.setSubTitle(subTitle);
                    movie.setDescription(description);
                    movie.setYear(year);
                    movie.setDiscId(discID);
                    movie.setDuration(duration);
                    movie.setFskId(fskID);
                    movie.setGenreId(genreID);
                    movie.setRating(rating);
                    movie.setPurchaseDate(movieBox.getPurchaseDate());
                    movie.setInMovieBox(movieBox.getId());
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

                    } else if(cover.startsWith("none")) {

                        //Cover der Box verwenden
                        if(movieBox.getCoverFile() != null && !movieBox.getCoverFile().isBlank()) {

                            movie.setCoverFile(movieBox.getCoverFile());
                        }
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

                    //Film Speichern
                    ID newMovieId = MovieEditor.addMovie(movie);

                    //Film zur Box hinzufügen
                    BoxMovie boxMovie = new BoxMovie();
                    boxMovie.setMovieId(newMovieId);
                    int nextOrderId = movieBox.getBoxMovies().stream().mapToInt(BoxMovie::getOrderId).summaryStatistics().getMax() + 1;
                    boxMovie.setOrderId(nextOrderId >= 0 ? nextOrderId : 0);
                    movieBox.getBoxMovies().add(boxMovie);
                    MovieBoxEditor.updateMovieBox(movieBox);

                    //Import Speichern
                    Path importedIdsFile = Paths.get(System.getProperty("java.io.tmpdir")).resolve("movieImport").resolve("importedBoxes.txt");
                    List<String> ids = OldMovieDbImport.readImportedIdList(importedIdsFile);
                    ids.add(oldmovieid);
                    OldMovieDbImport.dumpImportedIdLsit(ids, importedIdsFile);

                    //weiter zum nächsten Film
                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Der Film \"" + movie.getTitle() + (!movie.getSubTitle().isBlank() ? " - " + movie.getSubTitle() : "") + "\" wurde erfolgreich importiert");
                    resp.sendRedirect("/movie/admin/zipimport/moviebox?oldid=" + oldid + "&newid=" + newid.get());
                } else {

                    //Filmbox nicht gefunden
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Filmbox nicht gefunden");
                    if(newid != null) {

                        resp.sendRedirect("/movie/admin/zipimport/moviebox?oldid=" + oldid + "&newid=" + newid.get());
                    } else {

                        resp.sendRedirect("/movie/admin/zipimport/moviebox");
                    }
                }
            } else {

                //ungültige Formulardaten
                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
                if(newid != null) {

                    resp.sendRedirect("/movie/admin/zipimport/moviebox?oldid=" + oldid + "&newid=" + newid.get());
                } else {

                    resp.sendRedirect("/movie/admin/zipimport/moviebox");
                }
            }

        }
    }
}
