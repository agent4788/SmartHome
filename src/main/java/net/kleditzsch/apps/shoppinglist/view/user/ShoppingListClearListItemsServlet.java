package net.kleditzsch.apps.shoppinglist.view.user;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.utility.form.FormValidation;
import net.kleditzsch.apps.shoppinglist.model.editor.ShoppingListEditor;
import net.kleditzsch.apps.shoppinglist.model.list.Item;
import net.kleditzsch.apps.shoppinglist.model.list.ShoppingList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ShoppingListClearListItemsServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        ID listId = null;
        try {

            FormValidation form = FormValidation.create(req);
            listId = form.getId("list", "Listen ID");
            List<ID> itemIDs = form.getIDList("clear", "Items");

            Optional<ShoppingList> shoppingListOptional = ShoppingListEditor.getShoppingListById(listId);
            if(shoppingListOptional.isPresent()) {

                ShoppingList list = shoppingListOptional.get();
                int initSize = list.getItems().size();
                itemIDs.forEach(itemId -> {

                    Optional<Item> itemOptional = list.getItems().stream().filter(item -> item.getId().equals(itemId)).findFirst();
                    itemOptional.ifPresent(item -> list.getItems().remove(item));
                });
                int removedSize = list.getItems().size();

                if((initSize - removedSize) == itemIDs.size() && ShoppingListEditor.updateShoppingList(list)) {

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
