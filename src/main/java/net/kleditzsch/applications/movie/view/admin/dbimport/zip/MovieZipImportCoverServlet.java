package net.kleditzsch.applications.movie.view.admin.dbimport.zip;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class MovieZipImportCoverServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<String> allowedContentType = Arrays.asList("image/jpeg", "image/png", "image/gif");
        String file = req.getParameter("file");
        Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir")).resolve("movieImport");
        Path filePath = tmpDir.resolve("cover").resolve(file);
        if(Files.exists(filePath)) {

            String contentType = Files.probeContentType(filePath);
            if(allowedContentType.contains(contentType)) {

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType(contentType);

                InputStream in = new FileInputStream(filePath.toFile());
                in.transferTo(resp.getOutputStream());
                return;
            }
        }
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
