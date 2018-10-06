package net.kleditzsch.SmartHome.view.movie.user.movie;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.*;
import net.kleditzsch.SmartHome.model.movie.movie.Movie;
import net.kleditzsch.SmartHome.model.movie.movie.meta.*;
import net.kleditzsch.SmartHome.util.form.FormValidation;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.util.logger.LoggerUtil;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

        //Meta Daten
        model.with("actorList", ActorEditor.createAndLoad().getData().stream().sorted(Comparator.comparing(Actor::getName)).collect(Collectors.toList()));
        model.with("directorList", DirectorEditor.createAndLoad().getData().stream().sorted(Comparator.comparing(Director::getName)).collect(Collectors.toList()));
        model.with("discList", DiscEditor.createAndLoad().getData().stream().sorted(Comparator.comparingInt(Disc::getOrderId)).collect(Collectors.toList()));
        model.with("fskList", FskEditor.createAndLoad().getData().stream().sorted(Comparator.comparingInt(FSK::getLevel)).collect(Collectors.toList()));
        model.with("genreList", GenreEditor.createAndLoad().getData().stream().sorted(Comparator.comparing(Genre::getName)).collect(Collectors.toList()));
        model.with("yearList", IntStream.rangeClosed(1900, LocalDate.now().getYear()).boxed().sorted(Comparator.reverseOrder()).collect(Collectors.toList()));
        model.with("today", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd")));

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

        //Optionale Parameter
        ID movieId = null;
        Part cover = null;
        List<ID> actors = null, directors = null;

        FormValidation form = FormValidation.create(req);
        boolean addElement = form.getBoolean("addElement", "neues Element");
        if(!addElement) {
            movieId = form.getId("id", "Film ID");
        }
        String title = form.getString("title", "Titel", 3, 50);
        String subTitle = form.getString("subtitle", "Untertietel", 0, 50);
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

            cover = form.getUploadedFile("cover", "Cover", 2_097_152, Arrays.asList("image/jpeg", "image/png", "image/gif"));
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
        if(actors != null && actors.size() > 0) {

            ActorEditor actorEditor = ActorEditor.createAndLoad();
            for(ID actorId : actors) {

                if(!actorEditor.getById(actorId).isPresent()) {

                    form.setInvalid("actors", "Ungültige Schauspieler ID");
                    break;
                }
            }
        }
        if(directors != null && directors.size() > 0) {

            DirectorEditor directorEditor = DirectorEditor.createAndLoad();
            for(ID directorId : directors) {

                if(!directorEditor.getById(directorId).isPresent()) {

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

                if(cover != null) {

                    //Cover Datei
                    Path uploadDir = Paths.get("upload/cover");
                    if(!Files.exists(uploadDir)) {

                        Files.createDirectories(uploadDir);
                    }

                    String filename = ID.create().get();
                    switch (cover.getContentType()) {

                        case "image/jpeg":

                            filename += ".jpeg";
                            break;
                        case "image/png":

                            filename += ".png";
                            break;
                        case "image/gif":

                            filename += ".gif";
                            break;
                    }
                    Path uploadFile = uploadDir.resolve(filename);
                    OutputStream outputStream = new FileOutputStream(uploadFile.toFile());
                    cover.getInputStream().transferTo(outputStream);

                    movie.setCoverFile(filename);
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
                    if(cover != null) {

                        Path uploadDir = Paths.get("upload/cover");
                        if(!Files.exists(uploadDir)) {

                            Files.createDirectories(uploadDir);
                        }

                        String filename = ID.create().get();
                        switch (cover.getContentType()) {

                            case "image/jpeg":

                                filename += ".jpeg";
                                break;
                            case "image/png":

                                filename += ".png";
                                break;
                            case "image/gif":

                                filename += ".gif";
                                break;
                        }
                        Path uploadFile = uploadDir.resolve(filename);
                        OutputStream outputStream = new FileOutputStream(uploadFile.toFile());
                        cover.getInputStream().transferTo(outputStream);

                        //altes Logo löschen
                        if(movie.getCoverFile() != null && movie.getCoverFile().length() > 0) {

                            Files.delete(uploadDir.resolve(movie.getCoverFile()));
                        }

                        //Dateiname des neuen Logos setzen
                        movie.setCoverFile(filename);
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

            //Ungültige Fomulareingaben loggen
            StringBuffer str = new StringBuffer();
            str.append("Ungültige Formulareingaben\n");
            form.getErrorMessages().forEach((name, message) -> {
                str.append(name + " -> " + message + "\n");
            });
            LoggerUtil.getLogger(this).warning(str.toString());

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
