package net.kleditzsch.SmartHome.view.movie.user.moviebox;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.*;
import net.kleditzsch.SmartHome.model.movie.movie.MovieBox;
import net.kleditzsch.SmartHome.model.movie.movie.meta.*;
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
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MovieMovieBoxFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/user/moviebox/movieboxform.html");
        JtwigModel model = JtwigModel.newModel();

        boolean addElement = true;
        MovieBox movieBox = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            //Schaltserver laden
            try {

                ID id = ID.of(req.getParameter("id").trim());

                Optional<MovieBox> movieBoxOptional = MovieBoxEditor.getMovieBox(id);
                if(movieBoxOptional.isPresent()) {

                    movieBox = movieBoxOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Die Film Box wurde nicht gefunden");
            }
        } else {

            movieBox = new MovieBox();
            movieBox.setId(ID.create());
        }
        model.with("addElement", addElement);
        model.with("movieBox", movieBox);

        //Meta Daten
        model.with("discList", DiscEditor.createAndLoad().getData().stream().sorted(Comparator.comparingInt(Disc::getOrderId)).collect(Collectors.toList()));
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

        //Optionale Parameter
        ID movieBoxId = null;
        Part cover = null;
        String coverUrl = null;

        FormValidation form = FormValidation.create(req);
        boolean addElement = form.getBoolean("addElement", "neues Element");
        if(!addElement) {
            movieBoxId = form.getId("id", "Film ID");
        }
        String title = form.getString("title", "Titel", 3, 50);
        String subTitle = form.getString("subtitle", "Untertietel", 0, 50);
        int year = form.getInteger("year", "Produktionsjahr", 1900, LocalDate.now().getYear());
        ID discID = form.getId("disc", "Medium");
        double price = form.getDouble("price", "Preis", 0.0, 1000.0);
        LocalDate purchaseDate = form.getLocalDate("purchaseDate", "Kaufdatum", DateTimeFormatter.ofPattern("yyyy-MM-dd"), LocalDate.of(1900, 01, 01), LocalDate.now());
        if(form.uploadNotEmpty("cover")) {

            cover = form.getUploadedFile("cover", "Cover", 2_097_152, UploadUtil.allowedImageContentTypes);
        }
        if(form.fieldNotEmpty("coverUrl")) {

            coverUrl = form.getString("coverUrl", "Cover URL", 3, 1000);
        }

        //IDs prüfen
        if(!DiscEditor.createAndLoad().getById(discID).isPresent()) {

            form.setInvalid("disc", "Ungültige Disc ID");
        }

        if (form.isSuccessful()) {

            if(addElement) {

                //neues Element hinzufügen
                MovieBox movieBox = new MovieBox();
                movieBox.setId(ID.create());
                movieBox.setTitle(title);
                movieBox.setSubTitle(subTitle);
                movieBox.setYear(year);
                movieBox.setDiscId(discID);
                movieBox.setPrice(price);
                movieBox.setPurchaseDate(purchaseDate);

                //Cover Datei
                Path uploadDir = Paths.get("upload/cover");
                if(!Files.exists(uploadDir)) {

                    Files.createDirectories(uploadDir);
                }
                if(cover != null) {

                    Path targetFile = UploadUtil.handleUploadedImage(cover, uploadDir);
                    movieBox.setCoverFile(targetFile.getFileName().toString());
                    cover.delete();
                } else if (coverUrl != null) {

                    //Cover aus dem Internet herunterladen
                    Path targetFile = null;
                    try {

                        targetFile = UploadUtil.handleImageUrl(coverUrl, uploadDir);
                        movieBox.setCoverFile(targetFile.getFileName().toString());
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                }

                MovieBoxEditor.addMovieBox(movieBox);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Die Filmbox wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/movie/movieboxview?id=" + movieBox.getId().get());
            } else {

                //Element bearbeiten
                Optional<MovieBox> movieBoxOptional = MovieBoxEditor.getMovieBox(movieBoxId);
                if (movieBoxOptional.isPresent()) {

                    MovieBox movieBox = movieBoxOptional.get();
                    movieBox.setTitle(title);
                    movieBox.setSubTitle(subTitle);
                    movieBox.setYear(year);
                    movieBox.setDiscId(discID);
                    movieBox.setPrice(price);
                    movieBox.setPurchaseDate(purchaseDate);

                    //Logo Datei
                    //Cover Datei
                    Path uploadDir = Paths.get("upload/cover");
                    if(!Files.exists(uploadDir)) {

                        Files.createDirectories(uploadDir);
                    }
                    if(cover != null) {

                        Path targetFile = UploadUtil.handleUploadedImage(cover, uploadDir);

                        //altes Logo löschen
                        if(movieBox.getCoverFile() != null && movieBox.getCoverFile().length() > 0) {

                            try {

                                Files.delete(uploadDir.resolve(movieBox.getCoverFile()));
                            } catch (Exception e) {}
                        }

                        //Dateiname des neuen Logos setzen
                        movieBox.setCoverFile(targetFile.getFileName().toString());

                        cover.delete();
                    } else if (coverUrl != null) {

                        //Cover aus dem Internet herunterladen
                        Path targetFile = null;
                        try {

                            targetFile = UploadUtil.handleImageUrl(coverUrl, uploadDir);

                            //altes Logo löschen
                            if(movieBox.getCoverFile() != null && movieBox.getCoverFile().length() > 0) {

                                try {

                                    Files.delete(uploadDir.resolve(movieBox.getCoverFile()));
                                } catch (Exception e) {}
                            }

                            movieBox.setCoverFile(targetFile.getFileName().toString());
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                    }

                    MovieBoxEditor.updateMovieBox(movieBox);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Die Filmbox wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/movie/movieboxview?id=" + movieBox.getId().get());
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Die Filmbox konnte nicht gefunden werden");
                    resp.sendRedirect("/movie/moviebox");
                }
            }

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            if(!addElement && movieBoxId != null) {

                resp.sendRedirect("/movie/movieboxview?id=" + movieBoxId.get());
            }
            resp.sendRedirect("/movie/moviebox");
        }
    }
}
