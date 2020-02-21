package net.kleditzsch.apps.recipe.view.admin.settings;

import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.model.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.settings.Interface.Settings;
import net.kleditzsch.SmartHome.utility.form.FormValidation;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import net.kleditzsch.apps.shoppinglist.model.editor.ShoppingListEditor;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class RecipeSettingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/recipe/admin/settings/settings.html");
        JtwigModel model = JtwigModel.newModel();

        //Einstellungen laden
        SettingsEditor se = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock lock = se.readLock();
        lock.lock();

        //Einstellungen laden
        model.with("newesRecipeCount", se.getIntegerSetting(Settings.RECIPE_NEWEST_RECIPE_COUNT).getValue());
        model.with("paginationUcp", se.getIntegerSetting(Settings.RECIPE_PAGINATION_ELEMENTS_AT_USER_PAGE).getValue());
        model.with("paginationAcp", se.getIntegerSetting(Settings.RECIPE_PAGINATION_ELEMENTS_AT_ADMIN_PAGE).getValue());
        model.with("shoppingListId", se.getStringSetting(Settings.RECIPE_SHOPPING_LIST_ID).getValue());

        model.with("shoppingListMap", ShoppingListEditor.listShoppingLists().stream().collect(Collectors.toMap(s -> s.getId().get(), s -> s)));

        //Meldung
        if(req.getSession().getAttribute("success") != null) {

            model.with("success", req.getSession().getAttribute("success"));
            req.getSession().removeAttribute("success");
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));

        lock.unlock();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        FormValidation form = FormValidation.create(req);
        final int newesRecipeCount = form.getInteger("newesRecipeCount", "Anzahl der neusten Rezepte", 0, 250);
        final int paginationUcp = form.getInteger("paginationUcp", "Anzahl der Filme pro Seite (UCP)", 0, 250);
        final int paginationAcp = form.getInteger("paginationAcp", "Anzahl der Filme pro Seite (ACP)", 0, 250);
        final ID shoppingListId = form.getId("shoppingList", "Einkaufsliste");

        if (form.isSuccessful()) {

            //Einstellungen speichern
            SettingsEditor se = SmartHome.getInstance().getSettings();
            ReentrantReadWriteLock.ReadLock lock = se.readLock();
            lock.lock();

            //Einstellungen laden
            se.getIntegerSetting(Settings.RECIPE_NEWEST_RECIPE_COUNT).setValue(newesRecipeCount);
            se.getIntegerSetting(Settings.RECIPE_PAGINATION_ELEMENTS_AT_USER_PAGE).setValue(paginationUcp);
            se.getIntegerSetting(Settings.RECIPE_PAGINATION_ELEMENTS_AT_ADMIN_PAGE).setValue(paginationAcp);
            se.getStringSetting(Settings.RECIPE_SHOPPING_LIST_ID).setValue(shoppingListId.get());

            lock.unlock();

            req.getSession().setAttribute("success", true);
            resp.sendRedirect("/recipe/admin/settings");
        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            resp.sendRedirect("/recipe/admin/settings");
        }
        resp.sendRedirect("/recipe/admin/settings");
    }
}
