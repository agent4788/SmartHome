package net.kleditzsch.apps.recipe.view.user.recipe;

import com.google.common.html.HtmlEscapers;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.model.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.settings.IntegerSetting;
import net.kleditzsch.apps.recipe.model.editor.RecipeEditor;
import net.kleditzsch.apps.recipe.model.recipe.Recipe;
import net.kleditzsch.apps.recipe.model.recipe.RecipeFilter;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import net.kleditzsch.SmartHome.utility.pagination.Pagination;
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

public class RecipeRecipeListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/recipe/user/recipe/recipelist.html");
        JtwigModel model = JtwigModel.newModel();

        //Blätterfunktion
        int index = 0;
        if (req.getParameter("index") != null) {

            index = Integer.parseInt(req.getParameter("index"));
        }

        //Filter
        RecipeFilter filter = new RecipeFilter();
        if(req.getParameter("difficultly") != null) {

            switch(req.getParameter("difficultly")) {

                case "light":

                    filter.setDifficulty(Recipe.Difficulty.LIGHT);
                    model.with("filtered", true);
                    model.with("difficultlyFilter", "light");
                    break;
                case "medium":

                    filter.setDifficulty(Recipe.Difficulty.MEDIUM);
                    model.with("filtered", true);
                    model.with("difficultlyFilter", "medium");
                    break;
                case "heavy":

                    filter.setDifficulty(Recipe.Difficulty.HEAVY);
                    model.with("filtered", true);
                    model.with("difficultlyFilter", "heavy");
                    break;
            }
        } else if(req.getParameter("type") != null) {

            switch(req.getParameter("type")) {

                case "maindish":

                    filter.setType(Recipe.Type.MAIN_DISH);
                    model.with("filtered", true);
                    model.with("typeFilter", "maindish");
                    break;
                case "starter":

                    filter.setType(Recipe.Type.STARTER);
                    model.with("filtered", true);
                    model.with("typeFilter", "starter");
                    break;
                case "dessert":

                    filter.setType(Recipe.Type.DESSERT);
                    model.with("filtered", true);
                    model.with("typeFilter", "dessert");
                    break;
                case "cake":

                    filter.setType(Recipe.Type.CAKE);
                    model.with("filtered", true);
                    model.with("typeFilter", "cake");
                    break;
                case "cookies":

                    filter.setType(Recipe.Type.COOKIES);
                    model.with("filtered", true);
                    model.with("typeFilter", "cookies");
                    break;
                case "garnish":

                    filter.setType(Recipe.Type.GARNISH);
                    model.with("filtered", true);
                    model.with("typeFilter", "garnish");
                    break;
            }
        }

        //Einstellungen laden
        int elementsAtPage = 25;
        int newestRecipeCount = 25;
        SettingsEditor settingsEditor = SmartHome.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        Optional<IntegerSetting> elementsAtPageOptional = settingsEditor.getIntegerSetting(SettingsEditor.RECIPE_PAGINATION_ELEMENTS_AT_USER_PAGE);
        if (elementsAtPageOptional.isPresent()) {

            elementsAtPage = elementsAtPageOptional.get().getValue();
        }
        Optional<IntegerSetting> newesRecipeCountOptional = settingsEditor.getIntegerSetting(SettingsEditor.RECIPE_NEWEST_RECIPE_COUNT);
        if (newesRecipeCountOptional.isPresent()) {

            newestRecipeCount = newesRecipeCountOptional.get().getValue();
        }
        settingsLock.unlock();

        Pagination pagination = new Pagination(RecipeEditor.countRecipes(filter), elementsAtPage, index);
        if(filter.getDifficulty().isPresent()) {

            pagination.setBaseLink("/recipe/recipe?difficultly=" + HtmlEscapers.htmlEscaper().escape(req.getParameter("difficultly")) + "&index=");
        } else if(filter.getType().isPresent()) {

            pagination.setBaseLink("/recipe/recipe?type=" + HtmlEscapers.htmlEscaper().escape(req.getParameter("type")) + "&index=");
        } else {

            pagination.setBaseLink("/recipe/recipe?index=");
        }
        List<Recipe> recipesAtPage = RecipeEditor.listRecipes(filter, pagination.getCurrentPageIndex(), pagination.getElementsAtPage());
        model.with("pagination", pagination);
        model.with("recipesAtPage", recipesAtPage);
        model.with("newestRecipes", RecipeEditor.listNewestRecipeIds(newestRecipeCount).stream().map(ID::toString).collect(Collectors.toList()));

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
