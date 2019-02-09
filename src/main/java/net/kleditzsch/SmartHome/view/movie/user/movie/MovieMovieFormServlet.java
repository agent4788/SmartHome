package net.kleditzsch.SmartHome.view.movie.user.movie;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.StringSetting;
import net.kleditzsch.SmartHome.model.movie.editor.*;
import net.kleditzsch.SmartHome.model.movie.movie.Movie;
import net.kleditzsch.SmartHome.model.movie.movie.meta.*;
import net.kleditzsch.SmartHome.util.api.tmdb.SimpleTmdbRestClient;
import net.kleditzsch.SmartHome.util.form.FormValidation;
import net.kleditzsch.SmartHome.util.image.UploadUtil;
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

public class MovieMovieFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/user/movie/movieform.html");
        JtwigModel model = JtwigModel.newModel();

        boolean addElement = true;
        Movie movie = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            //Schaltserver laden
            try {

                ID id = ID.of(req.getParameter("id").trim());

                Optional<Movie> movieOptional = MovieEditor.getMovie(id);
                if(movieOptional.isPresent()) {

                    movie = movieOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Der Film wurde nicht gefunden");
            }
        } else {

            movie = new Movie();
            movie.setId(ID.create());
        }
        model.with("addElement", addElement);
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
        if(!addElement) {
            movieId = form.getId("id", "Film ID");
        }
        String title = form.getString("title", "Titel", 1, 100);
        String subTitle = form.getString("subtitle", "Untertietel", 0, 100);
        String description = form.getString("description", "Beschreibung", 3, 100_000);
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
        if(form.uploadNotEmpty("cover")) {

            cover = form.getUploadedFile("cover", "Cover", 2_097_152, UploadUtil.allowedImageContentTypes);
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

        if (form.isSuccessful()) {

            if(addElement) {

                //neues Element hinzufügen
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

                Path uploadDir = Paths.get("upload/cover");
                if(cover != null) {

                    //Cover Datei hochgeladen
                    Path targetFile = UploadUtil.handleUploadedImage(cover, uploadDir);
                    movie.setCoverFile(targetFile.getFileName().toString());
                    cover.delete();
                } else if (coverUrl != null) {

                    //Cover aus dem Internet herunterladen
                    Path targetFile = null;
                    try {

                        targetFile = UploadUtil.handleImageUrl(coverUrl, uploadDir);
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

                MovieEditor.addMovie(movie);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Der Film wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/movie/movieview?id=" + movie.getId().get());
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

                    //Logo Datei
                    Path uploadDir = Paths.get("upload/cover");
                    if(cover != null) {

                        Path targetFile = UploadUtil.handleUploadedImage(cover, uploadDir);

                        //altes Logo löschen
                        if(movie.getCoverFile() != null && movie.getCoverFile().length() > 0) {

                            Files.delete(uploadDir.resolve(movie.getCoverFile()));
                        }

                        //Dateiname des neuen Logos setzen
                        movie.setCoverFile(targetFile.getFileName().toString());
                        cover.delete();
                    } else if (coverUrl != null) {

                        //Cover aus dem Internet herunterladen
                        Path targetFile = null;
                        try {

                            targetFile = UploadUtil.handleImageUrl(coverUrl, uploadDir);

                            //altes Logo löschen
                            if(movie.getCoverFile() != null && movie.getCoverFile().length() > 0) {

                                Files.delete(uploadDir.resolve(movie.getCoverFile()));
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
                            if (UploadUtil.fileTypes.containsKey(contentType)) {

                                filename += UploadUtil.fileTypes.get(contentType);
                            } else {

                                throw new IOException("Ungültiger Content Type: " + contentType);
                            }

                            //altes Logo löschen
                            if (movie.getCoverFile() != null && movie.getCoverFile().length() > 0) {

                                try {

                                    Files.delete(uploadDir.resolve(movie.getCoverFile()));
                                } catch (Exception e) {
                                }
                            }

                            //Datei in Cover Ordner verschieben und Dateinem im Filmobjekt speichern
                            Files.move(file, uploadDir.resolve(filename));
                            movie.setCoverFile(filename);
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }

                    MovieEditor.updateMovie(movie);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Der Film wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/movie/movieview?id=" + movie.getId().get());
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Der Film konnte nicht gefunden werden");
                    resp.sendRedirect("/movie/movie");
                }
            }

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            if(!addElement && movieId != null) {

                resp.sendRedirect("/movie/movieview?id=" + movieId.get());
            }
            resp.sendRedirect("/movie/movie");
        }
    }
}
