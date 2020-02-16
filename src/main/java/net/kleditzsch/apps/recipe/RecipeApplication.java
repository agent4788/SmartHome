package net.kleditzsch.apps.recipe;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.apps.recipe.model.editor.RecipeEditor;
import net.kleditzsch.apps.recipe.view.admin.RecipeAdminIndexServlet;
import net.kleditzsch.apps.recipe.view.admin.ingredient.RecipeIngredientDeleteServlet;
import net.kleditzsch.apps.recipe.view.admin.ingredient.RecipeIngredientFormServlet;
import net.kleditzsch.apps.recipe.view.admin.ingredient.RecipeIngredientListServlet;
import net.kleditzsch.apps.recipe.view.admin.settings.RecipeSettingsServlet;
import net.kleditzsch.apps.recipe.view.admin.tag.RecipeTagDeleteServlet;
import net.kleditzsch.apps.recipe.view.admin.tag.RecipeTagFormServlet;
import net.kleditzsch.apps.recipe.view.admin.tag.RecipeTagListServlet;
import net.kleditzsch.apps.recipe.view.user.RecipeBookmarkRecipeListServlet;
import net.kleditzsch.apps.recipe.view.user.RecipeImageServlet;
import net.kleditzsch.apps.recipe.view.user.RecipeIndexServlet;
import net.kleditzsch.apps.recipe.view.user.RecipeNewRecipeListServlet;
import net.kleditzsch.apps.recipe.view.user.foodlist.RecipeAddShoppingListServlet;
import net.kleditzsch.apps.recipe.view.user.foodlist.RecipeFoodListServlet;
import net.kleditzsch.apps.recipe.view.user.foodlist.RecipeUpdateFoodListServlet;
import net.kleditzsch.apps.recipe.view.user.recipe.*;
import net.kleditzsch.apps.recipe.view.user.search.RecipeSearchAllServlet;
import net.kleditzsch.apps.recipe.view.user.search.RecipeSearchServlet;
import net.kleditzsch.apps.recipe.view.user.search.RecipeSearchTagRecipesServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hauptklasse der Rezepteverwaltung
 */
public class RecipeApplication implements Application {

    /**
     * Initlisiert die Anwendungsdaten
     */
    public void init() {

        //Index anlegen
        if (RecipeEditor.countRecipes() == 0) {

            MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(RecipeEditor.COLLECTION);
            collection.createIndex(Indexes.text("name"), new IndexOptions().defaultLanguage("german"));
        }
    }

    /**
     * initalisiert die Webinhalte der Anwendung
     *
     * @param contextHandler Context Handler
     */
    public void initWebContext(ServletContextHandler contextHandler) {

        contextHandler.addServlet(RecipeIndexServlet.class, "/recipe/");
        contextHandler.addServlet(RecipeIndexServlet.class, "/recipe/index");
        contextHandler.addServlet(RecipeSearchServlet.class, "/recipe/search");
        contextHandler.addServlet(RecipeSearchAllServlet.class, "/recipe/searchall");
        contextHandler.addServlet(RecipeNewRecipeListServlet.class, "/recipe/newrecipes");
        contextHandler.addServlet(RecipeBookmarkRecipeListServlet.class, "/recipe/bookmarkrecipes");
        contextHandler.addServlet(RecipeRecipeListServlet.class, "/recipe/recipe");
        contextHandler.addServlet(RecipeRecipeViewServlet.class, "/recipe/recipeview");
        contextHandler.addServlet(RecipeRecipeFormServlet.class, "/recipe/recipeform");
        contextHandler.addServlet(RecipeRecipeDeleteServlet.class, "/recipe/recipedelete");
        contextHandler.addServlet(RecipeImageServlet.class, "/recipe/image");
        contextHandler.addServlet(RecipeUpdateBookmarkServlet.class, "/recipe/updatebookmark");
        contextHandler.addServlet(RecipeUpdateTagServlet.class, "/recipe/updatetag");
        contextHandler.addServlet(RecipeRecipeAddIngredientAmountServlet.class, "/recipe/addingredientamount");
        contextHandler.addServlet(RecipeRecipeDeleteIngredientAmountServlet.class, "/recipe/deleteingredientamount");
        contextHandler.addServlet(RecipeRecipeWorkStepFormServlet.class, "/recipe/workstepform");
        contextHandler.addServlet(RecipeRecipeWorkStepOrderServlet.class, "/recipe/worksteporder");
        contextHandler.addServlet(RecipeRecipeDeleteWorkStepServlet.class, "/recipe/deleteworkstep");
        contextHandler.addServlet(RecipeRecipeWorkStepDataServlet.class, "/recipe/workstepdata");
        contextHandler.addServlet(RecipeRecipeUpdateGarnishServlet.class, "/recipe/updategarnish");
        contextHandler.addServlet(RecipeSearchTagRecipesServlet.class, "/recipe/searchtagrecipes");
        contextHandler.addServlet(RecipeFoodListServlet.class, "/recipe/foodlist");
        contextHandler.addServlet(RecipeUpdateFoodListServlet.class, "/recipe/updatefoodlist");
        contextHandler.addServlet(RecipeAddShoppingListServlet.class, "/recipe/addshoppinglist");

        contextHandler.addServlet(RecipeAdminIndexServlet.class, "/recipe/admin/index");
        contextHandler.addServlet(RecipeIngredientListServlet.class, "/recipe/admin/ingredient");
        contextHandler.addServlet(RecipeIngredientFormServlet.class, "/recipe/admin/ingredientform");
        contextHandler.addServlet(RecipeIngredientDeleteServlet.class, "/recipe/admin/ingredientdelete");
        contextHandler.addServlet(RecipeTagListServlet.class, "/recipe/admin/tag");
        contextHandler.addServlet(RecipeTagFormServlet.class, "/recipe/admin/tagform");
        contextHandler.addServlet(RecipeTagDeleteServlet.class, "/recipe/admin/tagdelete");
        contextHandler.addServlet(RecipeSettingsServlet.class, "/recipe/admin/settings");
    }

    /**
     * startet die Anwendung
     */
    public void start() {


    }

    /**
     * Speichert alle Anwendungsdaten
     */
    public void dump() {


    }

    /**
     * Beendet die Anwendung
     */
    public void stop() {


    }
}
