package net.kleditzsch.applications.automation.view.admin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kleditzsch.smarthome.utility.icon.IconUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AutomationIconChooserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Antwort initalisieren
        resp.setContentType("application/json");

        JsonObject iconJson = new JsonObject();

        try {

            if(req.getParameter("cat") != null) {

                //Icons der Kategorie auslesen
                List<String> categorys = IconUtil.listIconCategorys();
                Optional<String> category = categorys.stream().filter(cat -> cat.equalsIgnoreCase(req.getParameter("cat"))).findFirst();
                if(category.isPresent()) {

                    String categoryDirectory = category.get().trim();
                    List<String> icons = IconUtil.listCategoryIconFiles(categoryDirectory);
                    JsonArray array = new JsonArray();
                    icons.forEach(icon -> array.add(categoryDirectory + "/" + icon.trim()));
                    iconJson.add("icons", array);
                } else {

                    //Kategorie nicht gefunden
                    iconJson = new JsonObject();
                    iconJson.addProperty("message", "Die Kategorie konnten nicht gefunden werden");
                }
            } else if(req.getParameter("icon") != null) {

                //Kategorie ermitteln und Icons auslesen
            }
        } catch (Exception e) {

            iconJson = new JsonObject();
            iconJson.addProperty("message", "Die Icons konnten nicht gelesen werden");
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(iconJson.toString());
    }
}
