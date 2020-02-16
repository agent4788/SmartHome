package net.kleditzsch.apps.movie.view.admin.fsk;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.movie.model.editor.FskEditor;
import net.kleditzsch.apps.movie.model.movie.meta.FSK;
import net.kleditzsch.SmartHome.utility.collection.CollectionUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MovieFskOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if(req.getParameter("direction") != null && req.getParameter("id") != null) {

            try {

                boolean moveUp = req.getParameter("direction").equals("up");
                ID id = ID.of(req.getParameter("id"));

                //Sortierungen anpassen
                FskEditor fskEditor = FskEditor.createAndLoad();

                Optional<FSK> fskOptional = fskEditor.getById(id);
                if(fskOptional.isPresent()) {

                    List<FSK> fskList = new ArrayList<>(fskEditor.getFskSorted());
                    int currentIndex = fskList.indexOf(fskOptional.get());
                    int newIndex = 0;
                    if(moveUp) {

                        //nach oben verschieben
                        newIndex = currentIndex -1;
                    } else {

                        //nach unten verschieben
                        newIndex = currentIndex + 1;
                    }
                    if(newIndex >= 0 && newIndex <= 10_000) {

                        //neu sortieren und Speichern;
                        CollectionUtil.moveItem(currentIndex, newIndex, fskList);
                        int level = 0;
                        for (FSK fsk : fskList) {

                            Optional<FSK> fskOptional1 = fskEditor.getById(fsk.getId());
                            if (fskOptional1.isPresent()) {

                                //fskOptional1.get().setLevel(level);
                                fskEditor.updateLevel(fskOptional1.get().getId(), level);
                                level++;
                            }
                        }

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Die Sortierung wurde erfolgreich bearbeitet");
                        resp.sendRedirect("/movie/admin/fsk");
                    } else {

                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Falscher Index");
                        resp.sendRedirect("/movie/admin/fsk");
                    }
                }

            } catch (Exception e) {

                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Fehlerhafte Daten");
                resp.sendRedirect("/movie/admin/fsk");
            }
        } else {

            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Daten");
            resp.sendRedirect("/movie/admin/fsk");
        }
    }
}
