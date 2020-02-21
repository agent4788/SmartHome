package net.kleditzsch.apps.shoppinglist.view.user;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.utility.form.FormValidation;
import net.kleditzsch.apps.shoppinglist.model.editor.ShoppingListEditor;
import net.kleditzsch.apps.shoppinglist.model.list.ShoppingList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ShoppingListAddListServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        ID newListId = null;

        FormValidation form = FormValidation.create(req);
        String name = form.getString("name", "Listenname", 3, 50);

        if(form.isSuccessful()) {

            ShoppingList list = new ShoppingList();
            list.setId(ID.create());
            list.setName(name);

            //Sortierungs ID
            List<ShoppingList> shoppingLists = ShoppingListEditor.listShoppingLists();
            if (shoppingLists.size() > 0) {

                int nextLevel = shoppingLists.stream().mapToInt(ShoppingList::getOrderId).summaryStatistics().getMax() + 1;
                nextLevel = nextLevel >= 0 ? nextLevel : 0;
                list.setOrderId(nextLevel);
            }

            newListId = ShoppingListEditor.addShoppingList(list);

            req.getSession().setAttribute("success", true);
            req.getSession().setAttribute("message", "Einkaufsliste erfolgreich erstellt");
        } else {

            //Fehlerhafte Eingaben
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
        }
        if(newListId != null) {

            resp.sendRedirect("/shoppinglist/index?edit=1#" + newListId.get());
        }
        resp.sendRedirect("/shoppinglist/index?edit=1");
    }
}
