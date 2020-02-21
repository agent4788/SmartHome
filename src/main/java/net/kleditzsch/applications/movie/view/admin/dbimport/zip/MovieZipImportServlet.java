package net.kleditzsch.applications.movie.view.admin.dbimport.zip;

import net.kleditzsch.smarthome.utility.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MovieZipImportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/dbimport/zip/import.html");
        JtwigModel model = JtwigModel.newModel();

        if(req.getSession().getAttribute("importFile") != null) {

            Path importFile = Paths.get(req.getSession().getAttribute("importFile").toString());

            String fileName = importFile.getFileName().toString();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm_ss");
            LocalDateTime creationDate = LocalDateTime.parse(fileName.substring(7, 27), format);

            model.with("fileName", fileName);
            model.with("fileSize", Files.size(importFile));
            model.with("creationDate", creationDate);

        } else {

            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Datei");
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
}
