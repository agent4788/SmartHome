package net.kleditzsch.SmartHome.view.recipe.admin.ingredient;

import com.google.common.html.HtmlEscapers;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.IntegerSetting;
import net.kleditzsch.SmartHome.model.recipe.editor.IngredientEditor;
import net.kleditzsch.SmartHome.model.recipe.editor.TagEditor;
import net.kleditzsch.SmartHome.model.recipe.recipe.Ingredient;
import net.kleditzsch.SmartHome.model.recipe.recipe.Tag;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.util.pagination.ListPagination;
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

public class RecipeIngredientListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/recipe/admin/ingredient/ingredientlist.html");
        JtwigModel model = JtwigModel.newModel();

        List<Ingredient> ingredientList = IngredientEditor.listIngredients();

        //filtern
        String filterStr = null;
        if(req.getParameter("filter") != null) {

            String filter = req.getParameter("filter").trim();
            filterStr = filter;
            model.with("filterStr", filterStr);
            ingredientList = ingredientList.stream().filter(e -> e.getName().toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toList());
        }

        //Blätterfunktion
        int index = 0;
        if (req.getParameter("index") != null) {

            index = Integer.parseInt(req.getParameter("index"));
        }

        //EInstellungen laden
        int elementsAtPage = 25;
        SettingsEditor settingsEditor = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        Optional<IntegerSetting> elementsAtPageOptional = settingsEditor.getIntegerSetting(SettingsEditor.RECIPE_PAGINATION_ELEMENTS_AT_ADMIN_PAGE);
        if (elementsAtPageOptional.isPresent()) {

            elementsAtPage = elementsAtPageOptional.get().getValue();
        }
        settingsLock.unlock();

        ListPagination<Ingredient> pagination = new ListPagination<>(ingredientList, elementsAtPage, index);
        if(filterStr != null) {

            pagination.setBaseLink("/recipe/admin/ingredient?filter=" + HtmlEscapers.htmlEscaper().escape(filterStr) + "&index=");
        } else {

            pagination.setBaseLink("/recipe/admin/ingredient?index=");
        }
        model.with("pagination", pagination);

        //Meldung
        if(req.getSession().getAttribute("success") != null && req.getSession().getAttribute("message") != null) {

            model.with("success", req.getSession().getAttribute("success"));
            model.with("message", req.getSession().getAttribute("message"));
            req.getSession().removeAttribute("success");
            req.getSession().removeAttribute("message");
        }

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }
}
