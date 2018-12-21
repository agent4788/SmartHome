package net.kleditzsch.SmartHome.view.shoppinglist.user;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.movie.editor.DiscEditor;
import net.kleditzsch.SmartHome.model.movie.movie.meta.Disc;
import net.kleditzsch.SmartHome.model.shoppinglist.editor.ShoppingListEditor;
import net.kleditzsch.SmartHome.model.shoppinglist.list.ShoppingList;
import net.kleditzsch.SmartHome.util.collection.CollectionUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ShoppingListOrderListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        if(req.getParameter("direction") != null && req.getParameter("id") != null) {

            try {

                boolean moveUp = req.getParameter("direction").equals("up");
                ID id = ID.of(req.getParameter("id"));

                //Sortierungen anpassen
                List<ShoppingList> shoppingLists = ShoppingListEditor.listShoppingLists();

                Optional<ShoppingList> shoppingListOptional = shoppingLists.stream().filter(list -> list.getId().equals(id)).findFirst();
                if(shoppingListOptional.isPresent()) {

                    Collections.sort(shoppingLists);
                    int currentIndex = shoppingLists.indexOf(shoppingListOptional.get());
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
                        CollectionUtil.moveItem(currentIndex, newIndex, shoppingLists);
                        int orderId = 0;
                        for (ShoppingList list : shoppingLists) {

                            Optional<ShoppingList> shoppingListOptional1 = shoppingLists.stream().filter(list1 -> list1.getId().equals(list.getId())).findFirst();;
                            if (shoppingListOptional1.isPresent()) {

                                shoppingListOptional1.get().setOrderId(orderId);
                                ShoppingListEditor.updateShoppingList(shoppingListOptional1.get());
                                orderId++;
                            }
                        }

                        req.getSession().setAttribute("success", true);
                        req.getSession().setAttribute("message", "Die Sortierung wurde erfolgreich bearbeitet");
                        resp.sendRedirect("/shoppinglist/index?edit=1");
                    } else {

                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Falscher Index");
                        resp.sendRedirect("/shoppinglist/index?edit=1");
                    }
                }

            } catch (Exception e) {

                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Fehlerhafte Daten");
                resp.sendRedirect("/shoppinglist/index?edit=1");
            }
        } else {

            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Daten");
            resp.sendRedirect("/shoppinglist/index?edit=1");
        }
    }
}
