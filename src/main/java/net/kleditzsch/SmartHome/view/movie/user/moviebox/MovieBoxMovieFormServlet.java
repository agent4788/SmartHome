package net.kleditzsch.SmartHome.view.movie.user.moviebox;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.StringSetting;
import net.kleditzsch.SmartHome.model.movie.editor.*;
import net.kleditzsch.SmartHome.model.movie.movie.Movie;
import net.kleditzsch.SmartHome.model.movie.movie.MovieBox;
import net.kleditzsch.SmartHome.model.movie.movie.meta.*;
import net.kleditzsch.SmartHome.util.api.tmdb.SimpleTmdbRestClient;
import net.kleditzsch.SmartHome.util.form.FormValidation;
import net.kleditzsch.SmartHome.util.image.ImageUtil;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.eclipse.jetty.server.Request;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MovieBoxMovieFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/user/moviebox/boxmovieform.html");
        JtwigModel model = JtwigModel.newModel();

        boolean addElement = true;
        MovieBox movieBox = null;
        Movie movie = null;

        if(req.getParameter("boxid") != null && req.getParameter("movieid") != null) {

            addElement = false;

            //Schaltserver laden
            try {

                ID movieBoxId = ID.of(req.getParameter("boxid").trim());
                ID movieId = ID.of(req.getParameter("movieid").trim());

                Optional<MovieBox> movieBoxOptional = MovieBoxEditor.getMovieBox(movieBoxId);
                if(movieBoxOptional.isPresent()) {

                    Optional<Movie> movieOptional = MovieEditor.getMovie(movieId);
                    if(movieOptional.isPresent() && movieOptional.get().getBoxId().isPresent() && movieOptional.get().getBoxId().get().equals(movieBoxId)) {

                        movieBox = movieBoxOptional.get();
                        movie = movieOptional.get();
                    } else {

                        //Element nicht gefunden oder ungültig
                        throw new Exception();
                    }
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Der Film wurde nicht gefunden");
            }
        } else if(req.getParameter("boxid") != null) {

            ID movieBoxId = ID.of(req.getParameter("boxid").trim());

            Optional<MovieBox> movieBoxOptional = MovieBoxEditor.getMovieBox(movieBoxId);
            if(movieBoxOptional.isPresent()) {

                movieBox = movieBoxOptional.get();
                movie = new Movie();
                movie.setId(ID.create());
            }
        }
        model.with("addElement", addElement);
        model.with("movieBox", movieBox);
        model.with("movie", movie);

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

        //Meta Daten
        model.with("personList", PersonEditor.createAndLoad().getData().stream().sorted(Comparator.comparing(Person::getName)).collect(Collectors.toList()));
        model.with("discList", DiscEditor.createAndLoad().getData().stream().sorted(Comparator.comparingInt(Disc::getOrderId)).collect(Collectors.toList()));
        model.with("fskList", FskEditor.createAndLoad().getData().stream().sorted(Comparator.comparingInt(FSK::getLevel)).collect(Collectors.toList()));
        model.with("genreList", GenreEditor.createAndLoad().getData().stream().sorted(Comparator.comparing(Genre::getName)).collect(Collectors.toList()));
        model.with("yearList", IntStream.rangeClosed(1900, LocalDate.now().getYear()).boxed().sorted(Comparator.reverseOrder()).collect(Collectors.toList()));
        model.with("today", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Dateiupload initalisieren
        req.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, new MultipartConfigElement(
                System.getProperty("java.io.tmpdir"),    //Temp Ordner
                1024L * 1024L * 50L,          //Maximale Dateigröße
                1024L * 1024L * 60L,       //Maximale größe der gesmten Anfrage
                1024 * 1024 * 5           //Dateigröße ab der in den Temp Ordner geschrieben wird
        ));

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

        //Optionale Parameter
        ID movieId = null;
        Part cover = null;
        String coverPath = null, coverUrl = null;
        List<ID> actors = null, directors = null;

        FormValidation form = FormValidation.create(req);
        boolean addElement = form.getBoolean("addElement", "neues Element");
        ID movieBoxId = form.getId("boxid", "Film Box");
        if(!addElement) {
            movieId = form.getId("movieid", "Film Box Film");
        }
        String title = form.getString("title", "Titel", 3, 50);
        String subTitle = form.getString("subtitle", "Untertietel", 0, 50);
        String description = form.getString("description", "Beschreibung", 3, 100_000);
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
        if(form.uploadNotEmpty("cover")) {

            cover = form.getUploadedFile("cover", "Cover", 2_097_152, ImageUtil.allowedContentTypes);
        }
        if(form.fieldNotEmpty("coverPath")) {

            coverPath = form.getString("coverPath", "Cover Pfad", 3, 100);
        }
        if(form.fieldNotEmpty("coverUrl")) {

            coverUrl = form.getString("coverUrl", "Cover URL", 3, 1000);
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

        form.getErrorMessages().forEach((k, v) -> System.out.println(k + " -> " + v));

        if (form.isSuccessful()) {

            Optional<MovieBox> movieBoxOptional = MovieBoxEditor.getMovieBox(movieBoxId);
            if(movieBoxOptional.isPresent()) {

                MovieBox movieBox = movieBoxOptional.get();
                if(addElement) {

                    //neues Element hinzufügen
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

                    Path uploadDir = Paths.get("upload/cover");
                    if(cover != null) {

                        //Cover Datei
                        Path targetFile = ImageUtil.handleUploadedImage(cover, uploadDir);
                        movie.setCoverFile(targetFile.getFileName().toString());
                    } else if (coverUrl != null) {

                        //Cover aus dem Internet herunterladen
                        Path targetFile = null;
                        try {

                            targetFile = ImageUtil.handleImageUrl(coverUrl, uploadDir);
                            movie.setCoverFile(targetFile.getFileName().toString());
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                    } else if (coverPath != null && !tmdbApiKey.isEmpty()) {

                        //Cover von The Movie DB herunterladen
                        Path tmpDir = Paths.get("upload/tmp");
                        if(!Files.exists(tmpDir)) {

                            Files.createDirectories(tmpDir);
                        }

                        try {

                            //Datei herunterladen
                            SimpleTmdbRestClient tmdb = SimpleTmdbRestClient.create(tmdbApiKey);
                            Path file = tmpDir.resolve(coverPath.substring(1));
                            tmdb.downloadImage(coverPath, file);

                            //neuen Dateinamen erstellen und COntent Type prüfen
                            String filename = ID.create().get();
                            String contentType = Files.probeContentType(file);
                            if(ImageUtil.fileTypes.containsKey(contentType)) {

                                filename += ImageUtil.fileTypes.get(contentType);
                            } else {

                                throw new IOException("Ungültiger Content Type: " + contentType);
                            }

                            //Datei in Cover Ordner verschieben und Dateinem im Filmobjekt speichern
                            Files.move(file, uploadDir.resolve(filename));
                            movie.setCoverFile(filename);
                        } catch (InterruptedException | IllegalStateException e) {

                            e.printStackTrace();
                        }
                    } else if(movieBox.getCoverFile() != null && movieBox.getCoverFile().length() > 0) {

                        //Box Cover übernehmen wenn kein Cover hochgeladen wurde
                        movie.setCoverFile(movieBox.getCoverFile());
                    }

                    ID newMovieId = MovieEditor.addMovie(movie);

                    //Flimbox aktualisieren
                    BoxMovie boxMovie = new BoxMovie();
                    boxMovie.setMovieId(newMovieId);
                    int nextOrderId = movieBox.getBoxMovies().stream().mapToInt(BoxMovie::getOrderId).summaryStatistics().getMax() + 1;
                    boxMovie.setOrderId(nextOrderId >= 0 ? nextOrderId : 0);
                    movieBox.getBoxMovies().add(boxMovie);
                    MovieBoxEditor.updateMovieBox(movieBox);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Der Film wurde erfolgreich hinzugefügt");
                    resp.sendRedirect("/movie/movieboxview?edit=1&id=" + movieBox.getId().get() + "#" + movie.getId().get());
                } else {

                    //Element bearbeiten
                    Optional<Movie> movieOptional = MovieEditor.getMovie(movieId);
                    if (movieOptional.isPresent()) {

                        Movie movie = movieOptional.get();
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
                        movie.getActorIds().clear();
                        if(actors != null) {

                            movie.getActorIds().addAll(actors);
                        }
                        movie.getDirectorIds().clear();
                        if(directors != null) {

                            movie.getDirectorIds().addAll(directors);
                        }

                        //Logo Datei
                        Path uploadDir = Paths.get("upload/cover");
                        if(cover != null) {

                            Path targetFile = ImageUtil.handleUploadedImage(cover, uploadDir);

                            //altes Logo löschen
                            if(movie.getCoverFile() != null && movie.getCoverFile().length() > 0 && !movieBox.getCoverFile().equals(movie.getCoverFile())) {

                                try {

                                    Files.delete(uploadDir.resolve(movie.getCoverFile()));
                                } catch (Exception e) {}
                            }

                            //Dateiname des neuen Logos setzen
                            movie.setCoverFile(targetFile.getFileName().toString());
                        } else if (coverUrl != null) {

                            //Cover aus dem Internet herunterladen
                            Path targetFile = null;
                            try {

                                targetFile = ImageUtil.handleImageUrl(coverUrl, uploadDir);

                                //altes Logo löschen
                                if(movie.getCoverFile() != null && movie.getCoverFile().length() > 0 && !movieBox.getCoverFile().equals(movie.getCoverFile())) {

                                    try {

                                        Files.delete(uploadDir.resolve(movie.getCoverFile()));
                                    } catch (Exception e) {}
                                }

                                movie.setCoverFile(targetFile.getFileName().toString());
                            } catch (InterruptedException e) {

                                e.printStackTrace();
                            }
                        } else if (coverPath != null && !tmdbApiKey.isEmpty()) {

                            //Cover von The Movie DB herunterladen
                            Path tmpDir = Paths.get("upload/tmp");
                            if(!Files.exists(tmpDir)) {

                                Files.createDirectories(tmpDir);
                            }

                            try {

                                //Datei herunterladen
                                SimpleTmdbRestClient tmdb = SimpleTmdbRestClient.create(tmdbApiKey);
                                Path file = tmpDir.resolve(coverPath.substring(1));
                                tmdb.downloadImage(coverPath, file);

                                //neuen Dateinamen erstellen und COntent Type prüfen
                                String filename = ID.create().get();
                                String contentType = Files.probeContentType(file);
                                if (ImageUtil.fileTypes.containsKey(contentType)) {

                                    filename += ImageUtil.fileTypes.get(contentType);
                                } else {

                                    throw new IOException("Ungültiger Content Type: " + contentType);
                                }

                                //altes Logo löschen
                                if(movie.getCoverFile() != null && movie.getCoverFile().length() > 0 && !movieBox.getCoverFile().equals(movie.getCoverFile())) {

                                    try {

                                        Files.delete(uploadDir.resolve(movie.getCoverFile()));
                                    } catch (Exception e) {}
                                }

                                //Datei in Cover Ordner verschieben und Dateinem im Filmobjekt speichern
                                Files.move(file, uploadDir.resolve(filename));
                                movie.setCoverFile(filename);
                            } catch (Exception e) {

                                e.printStackTrace();
                            }
                        } else if(movieBox.getCoverFile() != null && movieBox.getCoverFile().length() > 0) {

                            //Box Cover übernehmen wenn kein Cover hochgeladen wurde
                            movie.setCoverFile(movieBox.getCoverFile());
                        }

                        MovieEditor.updateMovie(movie);

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Der Film wurde erfolgreich bearbeitet");
                        resp.sendRedirect("/movie/movieboxview?edit=1&id=" + movieBox.getId().get() + "#" + movie.getId().get());
                    } else {

                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Der Film konnte nicht gefunden werden");
                        resp.sendRedirect("/movie/moviebox");
                    }
                }
            } else {

                //Filmbox nicht gefunden
                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Die Filmbox wurde nicht gefunden");
                resp.sendRedirect("/movie/moviebox");
            }

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/movie/moviebox");
        }
    }
}
