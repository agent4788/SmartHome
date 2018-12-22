package net.kleditzsch.SmartHome.view.shoppinglist.user;

import net.kleditzsch.SmartHome.model.shoppinglist.editor.ShoppingListEditor;
import net.kleditzsch.SmartHome.model.shoppinglist.editor.ShoppingItemSuggestionEditor;
import net.kleditzsch.SmartHome.model.shoppinglist.list.ShoppingList;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class ShoppingListIndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/shoppinglist/user/index.html");
        JtwigModel model = JtwigModel.newModel();

        //bearbeitungsmodus
        boolean edit = false;
        if(req.getParameter("edit") != null && req.getParameter("edit").trim().equals("1")) {

            edit = true;
        }
        model.with("edit", edit);

        //Daten Laden und aufbereiten
        List<ShoppingList> shoppingLists = ShoppingListEditor.listShoppingLists();
        Collections.sort(shoppingLists);
        model.with("shoppingLists", shoppingLists);
        model.with("maxOrderId", shoppingLists.stream().mapToInt(ShoppingList::getOrderId).summaryStatistics().getMax());

        //Verschläge laden
        ShoppingItemSuggestionEditor shoppingItemSuggestionEditor = ShoppingItemSuggestionEditor.create();
        model.with("suggestions", shoppingItemSuggestionEditor.listSuggestions());

        //Meldung
        if(req.getSession().getAttribute("success") != null && req.getSession().getAttribute("message") != null) {

            model.with("success", req.getSession().getAttribute("success"));
            model.with("message", req.getSession().getAttribute("message"));
            req.getSession().removeAttribute("success");
            req.getSession().removeAttribute("message");
        }

        //Viewport
        if(req.getSession().getAttribute("mobileView") != null && req.getSession().getAttribute("mobileView").equals("1")) {

            model.with("mobileView", true);
        } else {

            model.with("mobileView", false);
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
