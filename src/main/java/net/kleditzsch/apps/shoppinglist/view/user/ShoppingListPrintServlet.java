package net.kleditzsch.apps.shoppinglist.view.user;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import net.kleditzsch.apps.shoppinglist.model.editor.ShoppingListEditor;
import net.kleditzsch.apps.shoppinglist.model.list.ShoppingList;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ShoppingListPrintServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/shoppinglist/user/printlist.html");
        JtwigModel model = JtwigModel.newModel();

        if(req.getParameter("id") != null) {

            try {

                ID listId = ID.of(req.getParameter("id"));
                Optional<ShoppingList> shoppingListOptional = ShoppingListEditor.getShoppingListById(listId);
                if(shoppingListOptional.isPresent()) {

                    model.with("list", shoppingListOptional.get());
                } else {

                    model.with("message", "Die Einkaufsliste wurde icht gefunden");
                }
            } catch (Exception e) {

                model.with("message", "Ungültige ID");
            }
        } else {

            model.with("message", "Keine ID angegeben");
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
