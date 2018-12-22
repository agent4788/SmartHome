package net.kleditzsch.SmartHome.view.shoppinglist.user;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.shoppinglist.editor.ShoppingListEditor;
import net.kleditzsch.SmartHome.model.shoppinglist.editor.ShoppingItemSuggestionEditor;
import net.kleditzsch.SmartHome.model.shoppinglist.list.Item;
import net.kleditzsch.SmartHome.model.shoppinglist.list.ShoppingList;
import net.kleditzsch.SmartHome.util.form.FormValidation;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class ShoppingListAddItemServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        FormValidation form = FormValidation.create(req);
        ID listId = form.getId("listid", "Listen ID");
        String name = form.getString("name", "Artikel", 1, 50);
        int amount = form.optInteger("amount", "Menge", 0, 0, 1_000_000);
        String unit = form.optString("unit", "Einheit", "", Arrays.asList("Stück", "Pack", "g", "kg", "ml", "l", "cm", "m", ""));

        if(form.isSuccessful()) {

            Optional<ShoppingList> shoppingListOptional = ShoppingListEditor.getShoppingListById(listId);
            if(shoppingListOptional.isPresent()) {

                ShoppingList list = shoppingListOptional.get();

                Item item = new Item();
                item.setId(ID.create());
                item.setName(name);
                if(amount > 0) {

                    item.setAmount(amount + " " + unit);
                }
                list.getItems().add(item);

                if(ShoppingListEditor.updateShoppingList(list)) {

                    //Name als Vorschlag speichern
                    ShoppingItemSuggestionEditor shoppingItemSuggestionEditor = ShoppingItemSuggestionEditor.create();
                    shoppingItemSuggestionEditor.addSuggestion(name);

                    //speichern erfolgreich
                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Der Eintrag wurde der Liste hinzugefügt");
                } else {

                    //speichern fehlgeschlagen
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Der Eintrag konnte nicht hinzugefügt werden");
                }
            } else {

                //Einkaufsliste nicht gefunden
                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Die Einkaufsliste wurde nicht gefunden");
            }
        } else {

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
