package net.kleditzsch.SmartHome.view.recipe.user;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.IntegerSetting;
import net.kleditzsch.SmartHome.model.recipe.editor.RecipeEditor;
import net.kleditzsch.SmartHome.model.recipe.recipe.Recipe;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class RecipeIndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/recipe/user/index.html");
        JtwigModel model = JtwigModel.newModel();

        //Einstellungen laden
        int newestRecipeCount = 50;
        SettingsEditor settingsEditor = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        Optional<IntegerSetting> newestRecipeCountOptional = settingsEditor.getIntegerSetting(SettingsEditor.RECIPE_NEWEST_RECIPE_COUNT);
        if (newestRecipeCountOptional.isPresent()) {

            newestRecipeCount = newestRecipeCountOptional.get().getValue();
        }
        settingsLock.unlock();

        Random random = new Random();

        //neuste Rezepte (vier Zufällige Auswählen)
        List<Recipe> newestRecipesAll = RecipeEditor.listNewestRecipes(newestRecipeCount);
        model.with("newestMoviesCount", newestRecipesAll.size());
        if(newestRecipesAll.size() > 4) {

            List<Recipe> newestRecipes = new ArrayList<>(4);
            for(int i = 0; i < 4; i++) {

                int randomIndex = random.nextInt(newestRecipesAll.size());
                newestRecipes.add(newestRecipesAll.get(randomIndex));
                newestRecipesAll.remove(randomIndex);
            }
            model.with("newestRecipes", newestRecipes);
        } else {

            model.with("newestRecipes", newestRecipesAll);
        }

        //Rezepte der Merkliste (vier Zufällige Auswählen)
        List<Recipe> bookmarkRecipesAll = RecipeEditor.listBookmaredRecipes();
        model.with("bookmarkRecipesCount", bookmarkRecipesAll.size());
        if(bookmarkRecipesAll.size() > 4) {

            List<Recipe> bookmarkRecipes = new ArrayList<>(4);
            for(int i = 0; i < 4; i++) {

                int randomIndex = random.nextInt(bookmarkRecipesAll.size());
                bookmarkRecipes.add(bookmarkRecipesAll.get(randomIndex));
                bookmarkRecipesAll.remove(randomIndex);
            }
            model.with("bookmarkRecipes", bookmarkRecipes);
        } else {

            model.with("bookmarkRecipes", bookmarkRecipesAll);
        }
        model.with("newestRecipeIds", RecipeEditor.listNewestRecipeIds(newestRecipeCount).stream().map(ID::toString).collect(Collectors.toList()));

        //zufällige Rezepte (vier Auswählen)
        List<Recipe> recipeAll = RecipeEditor.listRecipes();
        if(recipeAll.size() > 4) {

            List<Recipe> randomRecipe = new ArrayList<>(4);
            for(int i = 0; i < 4; i++) {

                int randomIndex = random.nextInt(recipeAll.size());
                randomRecipe.add(recipeAll.get(randomIndex));
                recipeAll.remove(randomIndex);
            }
            model.with("randomRecipe", randomRecipe);
        } else {

            model.with("randomRecipe", recipeAll);
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
