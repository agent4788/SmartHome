package net.kleditzsch.SmartHome.view.movie.user.movieseries;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.MovieSeriesEditor;
import net.kleditzsch.SmartHome.model.movie.movie.MovieSeries;
import net.kleditzsch.SmartHome.util.form.FormValidation;
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
import java.util.Arrays;
import java.util.Optional;

public class MovieMovieSeriesFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/user/movieseries/movieseriesform.html");
        JtwigModel model = JtwigModel.newModel();

        boolean addElement = true;
        MovieSeries movieSeries = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            try {

                ID id = ID.of(req.getParameter("id").trim());

                Optional<MovieSeries> movieSeriesOptional = MovieSeriesEditor.getMovieSeries(id);
                if(movieSeriesOptional.isPresent()) {

                    movieSeries = movieSeriesOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Die Film Box wurde nicht gefunden");
            }
        } else {

            movieSeries = new MovieSeries();
            movieSeries.setId(ID.create());
        }
        model.with("addElement", addElement);
        model.with("movieSeries", movieSeries);

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
        ID movieSeriesId = null;
        Part poster = null, banner = null;
        String posterUrl = null, bannerUrl = null;

        FormValidation form = FormValidation.create(req);
        boolean addElement = form.getBoolean("addElement", "neues Element");
        if(!addElement) {
            movieSeriesId = form.getId("id", "Film Reihe ID");
        }
        String title = form.getString("title", "Titel", 3, 50);
        String subTitle = form.getString("subtitle", "Untertietel", 0, 50);
        String description = form.getString("description", "Beschreibung", 3, 100_000);

        if(form.uploadNotEmpty("poster")) {

            poster = form.getUploadedFile("poster", "Poster", 2_097_152, Arrays.asList("image/jpeg", "image/png", "image/gif"));
        }
        if(form.fieldNotEmpty("posterUrl")) {

            posterUrl = form.getString("posterUrl", "Poster URL", 3, 1000);
        }
        if(form.uploadNotEmpty("banner")) {

            banner = form.getUploadedFile("banner", "Banner", 2_097_152, Arrays.asList("image/jpeg", "image/png", "image/gif"));
        }
        if(form.fieldNotEmpty("bannerUrl")) {

            bannerUrl = form.getString("bannerUrl", "Banner URL", 3, 1000);
        }

        if (form.isSuccessful()) {

            if(addElement) {

                //neues Element hinzufügen
                MovieSeries movieSeries = new MovieSeries();
                movieSeries.setId(ID.create());
                movieSeries.setTitle(title);
                movieSeries.setSubTitle(subTitle);
                movieSeries.setDescription(description);

                //Cover Cover
                Path uploadDir = Paths.get("upload/cover");
                if(!Files.exists(uploadDir)) {

                    Files.createDirectories(uploadDir);
                }
                if(poster != null) {

                    //Cover Datei
                    String filename = ID.create().get();
                    switch (poster.getContentType()) {

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
                    try (OutputStream outputStream = new FileOutputStream(uploadFile.toFile())) {

                        poster.getInputStream().transferTo(outputStream);
                    }

                    movieSeries.setPosterFile(filename);
                } else if (posterUrl != null) {

                    //Cover aus dem Internet herunterladen
                    Path tmpDir = Paths.get("upload/tmp");
                    if(!Files.exists(tmpDir)) {

                        Files.createDirectories(tmpDir);
                    }

                    try {

                        HttpClient client = HttpClient.newBuilder()
                                .version(HttpClient.Version.HTTP_2)
                                .followRedirects(HttpClient.Redirect.NORMAL)
                                .build();;

                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(posterUrl))
                                .timeout(Duration.ofSeconds(5))
                                .GET()
                                .build();

                        Path tmpFile = tmpDir.resolve("tmpCover");
                        HttpResponse.BodyHandler<Path> asFile = HttpResponse.BodyHandlers.ofFile(tmpFile);
                        HttpResponse<Path> response = client.send(request, asFile);

                        if(response.statusCode() == 200) {

                            //neuen Dateinamen erstellen und COntent Type prüfen
                            String filename = ID.create().get();
                            switch(response.headers().allValues("Content-Type").get(0)) {

                                case "image/jpeg":

                                    filename += ".jpeg";
                                    break;
                                case "image/png":

                                    filename += ".png";
                                    break;
                                case "image/gif":

                                    filename += ".gif";
                                    break;

                                default:

                                    //Ungültiger Dateityp
                                    Files.delete(tmpFile);
                                    throw new IllegalStateException();
                            }

                            //Datei in Cover Ordner verschieben und Dateinem im Filmobjekt speichern
                            Files.move(tmpFile, uploadDir.resolve(filename));
                            movieSeries.setPosterFile(filename);
                        } else {

                            //Download fehleschlagen
                            throw new IllegalStateException("Download fehlgeschlagen");
                        }
                    } catch (InterruptedException e) {}
                }
                if(banner != null) {

                    //Cover Datei
                    String filename = ID.create().get();
                    switch (banner.getContentType()) {

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
                    try (OutputStream outputStream = new FileOutputStream(uploadFile.toFile())) {

                        banner.getInputStream().transferTo(outputStream);
                    }

                    movieSeries.setPosterFile(filename);
                } else if (bannerUrl != null) {

                    //Cover aus dem Internet herunterladen
                    Path tmpDir = Paths.get("upload/tmp");
                    if(!Files.exists(tmpDir)) {

                        Files.createDirectories(tmpDir);
                    }

                    try {

                        HttpClient client = HttpClient.newBuilder()
                                .version(HttpClient.Version.HTTP_2)
                                .followRedirects(HttpClient.Redirect.NORMAL)
                                .build();;

                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(bannerUrl))
                                .timeout(Duration.ofSeconds(5))
                                .GET()
                                .build();

                        Path tmpFile = tmpDir.resolve("tmpCover");
                        HttpResponse.BodyHandler<Path> asFile = HttpResponse.BodyHandlers.ofFile(tmpFile);
                        HttpResponse<Path> response = client.send(request, asFile);

                        if(response.statusCode() == 200) {

                            //neuen Dateinamen erstellen und COntent Type prüfen
                            String filename = ID.create().get();
                            switch(response.headers().allValues("Content-Type").get(0)) {

                                case "image/jpeg":

                                    filename += ".jpeg";
                                    break;
                                case "image/png":

                                    filename += ".png";
                                    break;
                                case "image/gif":

                                    filename += ".gif";
                                    break;

                                default:

                                    //Ungültiger Dateityp
                                    Files.delete(tmpFile);
                                    throw new IllegalStateException();
                            }

                            //Datei in Cover Ordner verschieben und Dateinem im Filmobjekt speichern
                            Files.move(tmpFile, uploadDir.resolve(filename));
                            movieSeries.setBannerFile(filename);
                        } else {

                            //Download fehleschlagen
                            throw new IllegalStateException("Download fehlgeschlagen");
                        }
                    } catch (InterruptedException e) {}
                }

                MovieSeriesEditor.addMovieSeries(movieSeries);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Die Filmreihe wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/movie/movieseriesview?id=" + movieSeries.getId().get());
            } else {

                //Element bearbeiten
                Optional<MovieSeries> movieSeriesOptional = MovieSeriesEditor.getMovieSeries(movieSeriesId);
                if (movieSeriesOptional.isPresent()) {

                    MovieSeries movieSeries = movieSeriesOptional.get();
                    movieSeries.setTitle(title);
                    movieSeries.setSubTitle(subTitle);
                    movieSeries.setDescription(description);

                    //Logo Datei
                    Path uploadDir = Paths.get("upload/cover");
                    if(!Files.exists(uploadDir)) {

                        Files.createDirectories(uploadDir);
                    }
                    if(poster != null) {

                        String filename = ID.create().get();
                        switch (poster.getContentType()) {

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
                        try (OutputStream outputStream = new FileOutputStream(uploadFile.toFile())) {

                            poster.getInputStream().transferTo(outputStream);
                        }

                        //altes Logo löschen
                        if(movieSeries.getPosterFile() != null && movieSeries.getPosterFile().length() > 0) {

                            try {

                                Files.delete(uploadDir.resolve(movieSeries.getPosterFile()));
                            } catch (Exception e) {}
                        }

                        //Dateiname des neuen Logos setzen
                        movieSeries.setPosterFile(filename);
                    } else if (posterUrl != null) {

                        //Cover aus dem Internet herunterladen
                        Path tmpDir = Paths.get("upload/tmp");
                        if(!Files.exists(tmpDir)) {

                            Files.createDirectories(tmpDir);
                        }

                        try {

                            HttpClient client = HttpClient.newBuilder()
                                    .version(HttpClient.Version.HTTP_2)
                                    .followRedirects(HttpClient.Redirect.NORMAL)
                                    .build();;

                            HttpRequest request = HttpRequest.newBuilder()
                                    .uri(URI.create(posterUrl))
                                    .timeout(Duration.ofSeconds(5))
                                    .GET()
                                    .build();

                            Path tmpFile = tmpDir.resolve("tmpCover");
                            HttpResponse.BodyHandler<Path> asFile = HttpResponse.BodyHandlers.ofFile(tmpFile);
                            HttpResponse<Path> response = client.send(request, asFile);

                            if(response.statusCode() == 200) {

                                //neuen Dateinamen erstellen und COntent Type prüfen
                                String filename = ID.create().get();
                                switch(response.headers().allValues("Content-Type").get(0)) {

                                    case "image/jpeg":

                                        filename += ".jpeg";
                                        break;
                                    case "image/png":

                                        filename += ".png";
                                        break;
                                    case "image/gif":

                                        filename += ".gif";
                                        break;

                                    default:

                                        //Ungültiger Dateityp
                                        Files.delete(tmpFile);
                                        throw new IllegalStateException();
                                }

                                //altes Logo löschen
                                if(movieSeries.getPosterFile() != null && movieSeries.getPosterFile().length() > 0) {

                                    try {

                                        Files.delete(uploadDir.resolve(movieSeries.getPosterFile()));
                                    } catch (Exception e) {}
                                }

                                //Datei in Cover Ordner verschieben und Dateinem im Filmobjekt speichern
                                Files.move(tmpFile, uploadDir.resolve(filename));
                                movieSeries.setPosterFile(filename);
                            } else {

                                //Download fehleschlagen
                                throw new IllegalStateException("Download fehlgeschlagen");
                            }
                        } catch (InterruptedException e) {}
                    }
                    if(banner != null) {

                        String filename = ID.create().get();
                        switch (banner.getContentType()) {

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
                        try (OutputStream outputStream = new FileOutputStream(uploadFile.toFile())) {

                            banner.getInputStream().transferTo(outputStream);
                        }

                        //altes Logo löschen
                        if(movieSeries.getBannerFile() != null && movieSeries.getBannerFile().length() > 0) {

                            try {

                                Files.delete(uploadDir.resolve(movieSeries.getBannerFile()));
                            } catch (Exception e) {}
                        }

                        //Dateiname des neuen Logos setzen
                        movieSeries.setBannerFile(filename);
                    } else if (bannerUrl != null) {

                        //Cover aus dem Internet herunterladen
                        Path tmpDir = Paths.get("upload/tmp");
                        if(!Files.exists(tmpDir)) {

                            Files.createDirectories(tmpDir);
                        }

                        try {

                            HttpClient client = HttpClient.newBuilder()
                                    .version(HttpClient.Version.HTTP_2)
                                    .followRedirects(HttpClient.Redirect.NORMAL)
                                    .build();;

                            HttpRequest request = HttpRequest.newBuilder()
                                    .uri(URI.create(bannerUrl))
                                    .timeout(Duration.ofSeconds(5))
                                    .GET()
                                    .build();

                            Path tmpFile = tmpDir.resolve("tmpCover");
                            HttpResponse.BodyHandler<Path> asFile = HttpResponse.BodyHandlers.ofFile(tmpFile);
                            HttpResponse<Path> response = client.send(request, asFile);

                            if(response.statusCode() == 200) {

                                //neuen Dateinamen erstellen und COntent Type prüfen
                                String filename = ID.create().get();
                                switch(response.headers().allValues("Content-Type").get(0)) {

                                    case "image/jpeg":

                                        filename += ".jpeg";
                                        break;
                                    case "image/png":

                                        filename += ".png";
                                        break;
                                    case "image/gif":

                                        filename += ".gif";
                                        break;

                                    default:

                                        //Ungültiger Dateityp
                                        Files.delete(tmpFile);
                                        throw new IllegalStateException();
                                }

                                //altes Logo löschen
                                if(movieSeries.getBannerFile() != null && movieSeries.getBannerFile().length() > 0) {

                                    try {

                                        Files.delete(uploadDir.resolve(movieSeries.getBannerFile()));
                                    } catch (Exception e) {}
                                }

                                //Datei in Cover Ordner verschieben und Dateinem im Filmobjekt speichern
                                Files.move(tmpFile, uploadDir.resolve(filename));
                                movieSeries.setBannerFile(filename);
                            } else {

                                //Download fehleschlagen
                                throw new IllegalStateException("Download fehlgeschlagen");
                            }
                        } catch (InterruptedException e) {}
                    }

                    MovieSeriesEditor.updateMovieSeries(movieSeries);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Die Filmreihe wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/movie/movieseriesview?id=" + movieSeries.getId().get());
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Die Filmreihe konnte nicht gefunden werden");
                    resp.sendRedirect("/movie/movieseries");
                }
            }

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            if(!addElement && movieSeriesId != null) {

                resp.sendRedirect("/movie/movieseriesview?id=" + movieSeriesId.get());
            }
            resp.sendRedirect("/movie/movieseries");
        }
    }
}
