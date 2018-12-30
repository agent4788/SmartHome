package net.kleditzsch.SmartHome.view.recipe.admin.tag;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.DiscEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Disc;
import net.kleditzsch.SmartHome.model.recipe.editor.TagEditor;
import net.kleditzsch.SmartHome.model.recipe.recipe.Tag;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class RecipeTagDeleteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        boolean success = true;

        String idStr = req.getParameter("id");
        if(idStr != null) {

            try {

                ID id = ID.of(idStr);

                Optional<Tag> tagOptional = TagEditor.getTagById(id);
                if(tagOptional.isPresent()) {

                    success = TagEditor.deleteTag(tagOptional.get().getId());
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
            req.getSession().setAttribute("message", "Das Tag wurde erfolgreich gelöscht");
            resp.sendRedirect("/recipe/admin/tag");
        } else {

            //löschem n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Das Tag konnte nicht gelöscht werden");
            resp.sendRedirect("/recipe/admin/tag");
        }
    }
}