package net.kleditzsch.SmartHome.view.movie.admin.fsk;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.FskEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.FSK;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MovieFskFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/fsk/fskform.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten vorbereiten
        FskEditor fe = FskEditor.createAndLoad();
        boolean addElement = true;
        FSK fsk = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            //Schaltserver laden
            try {

                ID id = ID.of(req.getParameter("id").trim());

                Optional<FSK> fskOptional = fe.getById(id);
                if(fskOptional.isPresent()) {

                    fsk = fskOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Die Altersfreigabe wurde nicht gefunden");
            }
        } else {

            fsk = new FSK();
            fsk.setId(ID.create());
        }
        model.with("addElement", addElement);
        model.with("fsk", fsk);

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

        String idStr = req.getParameter("id");
        String addElementStr = req.getParameter("addElement");
        String name = req.getParameter("name");
        String description = req.getParameter("description");

        //Daten vorbereiten
        boolean addElement = true;
        ID id = null;
        Part logo = null;

        //Daten prüfen
        boolean success = true;
        try {

            if(!(idStr != null)) {

                success = false;
            }
            id = ID.of(idStr);
            if(!(addElementStr != null && (addElementStr.equals("1") || addElementStr.equals("0")))) {

                success = false;
            } else {

                addElement = addElementStr.equals("1");
            }
            if(!(name != null && name.length() >= 3 && name.length() <= 50)) {

                success = false;
            }
            if(!(description != null && description.length() <= 250)) {

                success = false;
            }

            //Logo Datei
            if(req.getPart("logo") != null && req.getPart("logo").getSize() > 0) {

                List<String> allowedContentType = Arrays.asList("image/jpeg", "image/png", "image/gif");

                logo = req.getPart("logo");
                //max. Dateigröße 2 MB und Content Typ prüfen
                if(logo.getSize() > 2_097_152 || !allowedContentType.contains(logo.getContentType())) {

                    //Falscher Content Typ oder zu große Datei
                    success = false;
                }
            } else if(addElement) {

                //beim erstellen ist das Logo ein Pflichtfeld
                success = false;
            }

        } catch (Exception e) {

            success = false;
        }

        if (success) {

            FskEditor fe = FskEditor.createAndLoad();
            if(addElement) {

                //neues Element hinzufügen
                FSK fsk = new FSK();
                fsk.setId(ID.create());
                fsk.setName(name);
                fsk.setDescription(description);
                if (fe.getData().size() > 0) {

                    int nextLevel = fe.getData().stream().mapToInt(FSK::getLevel).summaryStatistics().getMax() + 1;
                    fsk.setLevel(nextLevel);
                }

                //Logo Datei
                Path uploadDir = Paths.get("upload/fskLogo");
                if(!Files.exists(uploadDir)) {

                    Files.createDirectories(uploadDir);
                }

                String filename = ID.create().get();
                switch (logo.getContentType()) {

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
                logo.getInputStream().transferTo(outputStream);

                fsk.setImageFile(filename);

                fe.add(fsk);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Die Altersfreigabe wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/movie/admin/fsk");
            } else {

                //Element bearbeiten
                Optional<FSK> fskOptional = fe.getById(id);
                if (fskOptional.isPresent()) {

                    FSK fsk = fskOptional.get();
                    fsk.setName(name);
                    fsk.setDescription(description);

                    //Logo Datei
                    if(logo != null) {

                        Path uploadDir = Paths.get("upload/fskLogo");
                        if(!Files.exists(uploadDir)) {

                            Files.createDirectories(uploadDir);
                        }

                        String filename = ID.create().get();
                        switch (logo.getContentType()) {

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
                        logo.getInputStream().transferTo(outputStream);

                        //altes Logo löschen
                        Files.delete(uploadDir.resolve(fsk.getImageFile()));

                        //Dateiname des neuen Logos setzen
                        fsk.setImageFile(filename);
                    }

                    fe.update(fsk);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Die Altersfreigabe wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/movie/admin/fsk");
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Die Altersfreigabe konnte nicht gefunden werden");
                    resp.sendRedirect("/movie/admin/fsk");
                }
            }

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/movie/admin/fsk");
        }
    }
}
