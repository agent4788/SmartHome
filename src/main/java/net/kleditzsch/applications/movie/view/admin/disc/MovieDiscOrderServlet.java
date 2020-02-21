package net.kleditzsch.applications.movie.view.admin.disc;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.smarthome.utility.collection.CollectionUtil;
import net.kleditzsch.applications.movie.model.editor.DiscEditor;
import net.kleditzsch.applications.movie.model.movie.meta.Disc;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MovieDiscOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if(req.getParameter("direction") != null && req.getParameter("id") != null) {

            try {

                boolean moveUp = req.getParameter("direction").equals("up");
                ID id = ID.of(req.getParameter("id"));

                //Sortierungen anpassen
                DiscEditor discEditor = DiscEditor.createAndLoad();

                Optional<Disc> discOptional = discEditor.getById(id);
                if(discOptional.isPresent()) {

                    List<Disc> discs = discEditor.getDiscsSorted();
                    int currentIndex = discs.indexOf(discOptional.get());
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
                        CollectionUtil.moveItem(currentIndex, newIndex, discs);
                        int orderId = 0;
                        for (Disc disc : discs) {

                            Optional<Disc> discOptional1 = discEditor.getById(disc.getId());
                            if (discOptional1.isPresent()) {

                                discOptional1.get().setOrderId(orderId);
                                discEditor.update(discOptional1.get());
                                orderId++;
                            }
                        }

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Die Sortierung wurde erfolgreich bearbeitet");
                        resp.sendRedirect("/movie/admin/disc");
                    } else {

                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Falscher Index");
                        resp.sendRedirect("/movie/admin/disc");
                    }
                }

            } catch (Exception e) {

                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Fehlerhafte Daten");
                resp.sendRedirect("/movie/admin/disc");
            }
        } else {

            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Daten");
            resp.sendRedirect("/movie/admin/disc");
        }
    }
}
