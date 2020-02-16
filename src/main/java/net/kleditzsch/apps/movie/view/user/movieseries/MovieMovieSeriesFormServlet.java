package net.kleditzsch.apps.movie.view.user.movieseries;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.movie.model.editor.MovieSeriesEditor;
import net.kleditzsch.apps.movie.model.movie.MovieSeries;
import net.kleditzsch.SmartHome.utility.form.FormValidation;
import net.kleditzsch.SmartHome.utility.image.UploadUtil;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
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
        String title = form.getString("title", "Titel", 1, 100);
        String subTitle = form.getString("subtitle", "Untertietel", 0, 100);
        String description = form.getString("description", "Beschreibung", 3, 100_000);

        if(form.uploadNotEmpty("poster")) {

            poster = form.getUploadedFile("poster", "Poster", 2_097_152, UploadUtil.allowedImageContentTypes);
        }
        if(form.fieldNotEmpty("posterUrl")) {

            posterUrl = form.getString("posterUrl", "Poster URL", 3, 1000);
        }
        if(form.uploadNotEmpty("banner")) {

            banner = form.getUploadedFile("banner", "Banner", 2_097_152, UploadUtil.allowedImageContentTypes);
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
                    Path targetFile = UploadUtil.handleUploadedImage(poster, uploadDir);
                    movieSeries.setPosterFile(targetFile.getFileName().toString());
                    poster.delete();
                } else if (posterUrl != null) {

                    //Cover aus dem Internet herunterladen
                    Path targetFile = null;
                    try {

                        targetFile = UploadUtil.handleImageUrl(posterUrl, uploadDir);
                        movieSeries.setPosterFile(targetFile.getFileName().toString());
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
                }
                if(banner != null) {

                    //Cover Datei
                    Path targetFile = UploadUtil.handleUploadedImage(banner, uploadDir);
                    movieSeries.setBannerFile(targetFile.getFileName().toString());
                    banner.delete();
                } else if (bannerUrl != null) {

                    //Cover aus dem Internet herunterladen
                    Path targetFile = null;
                    try {

                        targetFile = UploadUtil.handleImageUrl(bannerUrl, uploadDir);
                        movieSeries.setBannerFile(targetFile.getFileName().toString());
                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }
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

                        Path targetFile = UploadUtil.handleUploadedImage(poster, uploadDir);

                        //altes Logo löschen
                        if(movieSeries.getPosterFile() != null && movieSeries.getPosterFile().length() > 0) {

                            try {

                                Files.delete(uploadDir.resolve(movieSeries.getPosterFile()));
                            } catch (Exception e) {}
                        }

                        //Dateiname des neuen Logos setzen
                        movieSeries.setPosterFile(targetFile.getFileName().toString());

                        poster.delete();
                    } else if (posterUrl != null) {

                        //Cover aus dem Internet herunterladen
                        Path targetFile = null;
                        try {

                            targetFile = UploadUtil.handleImageUrl(posterUrl, uploadDir);

                            //altes Logo löschen
                            if(movieSeries.getPosterFile() != null && movieSeries.getPosterFile().length() > 0) {

                                try {

                                    Files.delete(uploadDir.resolve(movieSeries.getPosterFile()));
                                } catch (Exception e) {}
                            }

                            movieSeries.setPosterFile(targetFile.getFileName().toString());
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                    }
                    if(banner != null) {

                        Path targetFile = UploadUtil.handleUploadedImage(banner, uploadDir);

                        //altes Logo löschen
                        if(movieSeries.getBannerFile() != null && movieSeries.getBannerFile().length() > 0) {

                            try {

                                Files.delete(uploadDir.resolve(movieSeries.getBannerFile()));
                            } catch (Exception e) {}
                        }

                        //Dateiname des neuen Logos setzen
                        movieSeries.setBannerFile(targetFile.getFileName().toString());

                        banner.delete();
                    } else if (bannerUrl != null) {

                        //Cover aus dem Internet herunterladen
                        Path targetFile = null;
                        try {

                            targetFile = UploadUtil.handleImageUrl(bannerUrl, uploadDir);

                            //altes Logo löschen
                            if(movieSeries.getBannerFile() != null && movieSeries.getBannerFile().length() > 0) {

                                try {

                                    Files.delete(uploadDir.resolve(movieSeries.getBannerFile()));
                                } catch (Exception e) {}
                            }

                            movieSeries.setBannerFile(targetFile.getFileName().toString());
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
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
