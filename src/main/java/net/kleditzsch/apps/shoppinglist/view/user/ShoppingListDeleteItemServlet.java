package net.kleditzsch.apps.shoppinglist.view.user;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.shoppinglist.model.editor.ShoppingListEditor;
import net.kleditzsch.apps.shoppinglist.model.list.Item;
import net.kleditzsch.apps.shoppinglist.model.list.ShoppingList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ShoppingListDeleteItemServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        ID listId = null;
        try {

            ID itemId = ID.of(req.getParameter("item"));
            listId = ID.of(req.getParameter("list"));

            Optional<ShoppingList> shoppingListOptional = ShoppingListEditor.getShoppingListById(listId);
            if(shoppingListOptional.isPresent()) {

                ShoppingList list = shoppingListOptional.get();
                Optional<Item> itemOptional = list.getItems().stream().filter(item -> item.getId().equals(itemId)).findFirst();
                if(itemOptional.isPresent()) {

                    list.getItems().remove(itemOptional.get());

                    if(ShoppingListEditor.updateShoppingList(list)) {

                        //erfolgreich gelöscht
                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Der Eintrage wurde erfolgreich gelöscht");
                    } else {

                        //löschen fehlgeschlagen
                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Der Eintrage konnte nicht gelöscht werden");
                    }
                } else {

                    //Eintrag nicht gefunden
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Der Eintrage wurde nicht gefunden");
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
