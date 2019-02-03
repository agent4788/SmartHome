package net.kleditzsch.SmartHome.view.movie.admin.dbimport.zip;

import net.kleditzsch.SmartHome.util.file.FileUtil;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MovieZipImportFinalServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/dbimport/zip/final.html");
        JtwigModel model = JtwigModel.newModel();

        if(req.getParameter("cleanup") != null) {

            //Daten bereinigen

            //Temp Ordner löschen
            Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir")).resolve("movieImport");
            if(Files.exists(tmpDir)) {

                FileUtil.deleteDirectoryRecursiv(tmpDir);
            }

            //Archiv löschen
            Files.newDirectoryStream(Paths.get("upload/tmp")).forEach(entry -> {

                if(Files.isRegularFile(entry) && entry.getFileName().toString().startsWith("backup_") && entry.getFileName().toString().endsWith(".zip")) {

                    try {

                        Files.delete(entry);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            resp.sendRedirect("/movie/admin/index");
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
