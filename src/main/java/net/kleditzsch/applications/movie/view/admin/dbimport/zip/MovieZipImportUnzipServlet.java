package net.kleditzsch.applications.movie.view.admin.dbimport.zip;

import net.kleditzsch.smarthome.utility.file.FileUtil;
import net.kleditzsch.smarthome.utility.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MovieZipImportUnzipServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/movie/admin/dbimport/zip/unzip.html");
        JtwigModel model = JtwigModel.newModel();

        if(req.getSession().getAttribute("importFile") != null) {

            Path importFile = Paths.get(req.getSession().getAttribute("importFile").toString());
            Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir")).resolve("movieImport");

            //Temp Ordner löschen und neu erstellen
            if(Files.exists(tmpDir)) {

                FileUtil.deleteDirectoryRecursiv(tmpDir);
            }
            Files.createDirectories(tmpDir);

            //Dateien entpacken
            if(Files.exists(importFile)) {

                try (ZipInputStream zip = new ZipInputStream(new FileInputStream(importFile.toFile()))) {

                    ZipEntry entry = zip.getNextEntry();
                    while (entry != null) {

                        if (entry.isDirectory()) {

                            //Ordner
                            Files.createDirectories(tmpDir.resolve(entry.getName()));
                        } else {

                            //Datei
                            Path target = tmpDir.resolve(entry.getName());
                            try (FileOutputStream fos = new FileOutputStream(target.toFile(), false)) {

                                zip.transferTo(fos);
                            }
                        }
                        entry = zip.getNextEntry();
                    }

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Das Archiv wurde erfolgreich entpackt");

                } catch (IOException e) {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Das Archiv konnte nicht entpackt werden");
                }
            }

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
