package net.kleditzsch.applications.recipe.view.user;

import net.kleditzsch.smarthome.utility.image.UploadUtil;

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
import java.util.List;

public class RecipeImageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<String> allowedContentType = UploadUtil.allowedImageContentTypes;
        String file = req.getParameter("file");
        Path filePath = Paths.get("upload/recipe").resolve(file);
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
