package net.kleditzsch.SmartHome.view.movie.admin.fsk;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.FskEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.FSK;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class MovieFskDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        boolean success = true;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);
                FskEditor fe = FskEditor.createAndLoad();

                Optional<FSK> fskOptional = fe.getById(id);
                if(fskOptional.isPresent()) {

                    success = fe.delete(fskOptional.get());

                    //Logo Datei löschen
                    Path logoFile = Paths.get("upload/fskLogo").resolve(fskOptional.get().getImageFile());
                    Files.delete(logoFile);
                } else {

                    success = false;
                }

            } catch (Exception e) {

                success = false;
            }
        }

        if (success) {

            //löschem i.O.
            req.getSession().setAttribute("success", true);
            req.getSession().setAttribute("message", "Die Altersfreigabe wurde erfolgreich gelöscht");
            resp.sendRedirect("/movie/admin/fsk");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Die Altersfreigabe konnte nicht gelöscht werden");
            resp.sendRedirect("/movie/admin/fsk");
        }
    }
}
