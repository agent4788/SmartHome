package net.kleditzsch.SmartHome.view.movie.admin.dbimport;

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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class MovieImportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/dbimport/import.html");
        JtwigModel model = JtwigModel.newModel();

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

        //Dateiupload initalisieren
        req.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, new MultipartConfigElement(
                System.getProperty("java.io.tmpdir"),    //Temp Ordner
                1024L * 1024L * 250L,         //Maximale Dateigröße
                1024L * 1024L * 250L,      //Maximale größe der gesmten Anfrage
                1024 * 1024 * 5           //Dateigröße ab der in den Temp Ordner geschrieben wird
        ));

        FormValidation form = FormValidation.create(req);
        List<String> allowedContentTypes = Arrays.asList("application/zip", "application/octet-stream", "text/plain", "text/csv", "application/csv");
        Part importFile = form.getUploadedFile("file", "Importdatei", 1024L * 1024L * 250L, allowedContentTypes);

        if(form.isSuccessful()) {

            Path uploadDir = Paths.get("upload/tmp");
            Path targetFile = UploadUtil.handleUploadedFile(importFile, uploadDir, allowedContentTypes);
            importFile.delete();

            req.getSession().setAttribute("importFile", targetFile.toAbsolutePath().toString());
            if(Arrays.asList("application/zip", "application/octet-stream").contains(importFile.getContentType())) {

                //Zip Datei importieren
                resp.sendRedirect("/movie/admin/zipimport/start");
            } else {

                //CSV Datei Importieren
                resp.sendRedirect("/movie/admin/csvimport/start");
            }
        } else {

            //Fehlerhafte Datei oder Upload fehlgeschlagen
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Datei oder Upload fehlgeschlagen");
            resp.sendRedirect("/movie/admin/import");
        }
    }
}
