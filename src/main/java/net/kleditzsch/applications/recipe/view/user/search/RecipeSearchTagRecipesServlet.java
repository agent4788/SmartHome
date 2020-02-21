package net.kleditzsch.applications.recipe.view.user.search;

import net.kleditzsch.smarthome.SmartHome;
import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.smarthome.model.editor.SettingsEditor;
import net.kleditzsch.smarthome.model.settings.Interface.Settings;
import net.kleditzsch.smarthome.utility.form.FormValidation;
import net.kleditzsch.smarthome.utility.jtwig.JtwigFactory;
import net.kleditzsch.smarthome.utility.pagination.ListPagination;
import net.kleditzsch.applications.recipe.model.editor.RecipeEditor;
import net.kleditzsch.applications.recipe.model.editor.TagEditor;
import net.kleditzsch.applications.recipe.model.recipe.Recipe;
import net.kleditzsch.applications.recipe.model.recipe.Tag;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class RecipeSearchTagRecipesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/recipe/user/search/searchtagrecipes.html");
        JtwigModel model = JtwigModel.newModel();

        //Blätterfunktion
        int index = 0;
        if (req.getParameter("index") != null) {

            index = Integer.parseInt(req.getParameter("index"));
        }

        //Einstellungen laden
        int elementsAtPage = 50;
        int newestRecipeCount = 50;
        SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        elementsAtPage = settingsEditor.getIntegerSetting(Settings.RECIPE_PAGINATION_ELEMENTS_AT_USER_PAGE).getValue();
        newestRecipeCount = settingsEditor.getIntegerSetting(Settings.RECIPE_NEWEST_RECIPE_COUNT).getValue();
        settingsLock.unlock();

        FormValidation form = FormValidation.create(req);
        if(form.fieldNotEmpty("id")) {

            ID id = form.getId("id", "Tag ID");
            if(form.isSuccessful()) {

                Optional<Tag> tagOptional = TagEditor.getTagById(id);
                if(tagOptional.isPresent()) {

                    List<Recipe> tagRecipes = RecipeEditor.listRecipesWithTag(id);

                    //Blätterfunktion
                    ListPagination<Recipe> pagination = new ListPagination<>(tagRecipes, elementsAtPage, index);
                    pagination.setBaseLink("/recipe/searchtagrecipes?id=" + id.get() + "&index=");
                    model.with("tag", tagOptional.get());
                    model.with("pagination", pagination);
                    model.with("newestRecipeIds", RecipeEditor.listNewestRecipeIds(newestRecipeCount).stream().map(ID::toString).collect(Collectors.toList()));
                } else {

                    //Tage nicht gefunden
                    model.with("errorMessage", "Tag nicht gefunden");
                }
            } else {

                //Ungültige Tag ID
                model.with("errorMessage", "Ungültige Tag ID");
            }
        } else {

            //Keine Tag ID übergeben
            model.with("errorMessage", "Ungültige Tag ID");
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
