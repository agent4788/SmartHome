package net.kleditzsch.applications.shoppinglist.view.user;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.applications.shoppinglist.model.editor.ShoppingListEditor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ShoppingListDeleteListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {

            ID listId = ID.of(req.getParameter("id"));
            if(ShoppingListEditor.deleteShoppingList(listId)) {

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Einkaufsliste erfolgreich gelöscht");
            } else {

                //Liste nicht gefunden
                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Die Einkaufsliste wurde nicht gefunden");
            }
        } catch (Exception e) {

            //Fehlerhafte Eingaben
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
        }
        resp.sendRedirect("/shoppinglist/index?edit=1");
    }
}
