package net.kleditzsch.SmartHome.view.shoppinglist.user;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.shoppinglist.editor.ShoppingListEditor;
import net.kleditzsch.SmartHome.model.shoppinglist.list.Item;
import net.kleditzsch.SmartHome.model.shoppinglist.list.ShoppingList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ShoppingListClearListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        ID listId = null;
        try {

            listId = ID.of(req.getParameter("id"));

            Optional<ShoppingList> shoppingListOptional = ShoppingListEditor.getShoppingListById(listId);
            if(shoppingListOptional.isPresent()) {

                ShoppingList list = shoppingListOptional.get();
                list.getItems().clear();

                if(ShoppingListEditor.updateShoppingList(list)) {

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Die Artikel wurden erfolgreich entfernt");
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Die Artikel konnten nicht entfernt werden");
                }
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
        if(listId != null) {

            resp.sendRedirect("/shoppinglist/index#" + listId.get());
        }
        resp.sendRedirect("/shoppinglist/index");
    }
}
