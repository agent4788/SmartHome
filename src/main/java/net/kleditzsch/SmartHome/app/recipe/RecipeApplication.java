package net.kleditzsch.SmartHome.app.recipe;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.app.SubApplication;
import net.kleditzsch.SmartHome.model.recipe.editor.RecipeEditor;
import net.kleditzsch.SmartHome.view.recipe.admin.RecipeAdminIndexServlet;
import net.kleditzsch.SmartHome.view.recipe.admin.ingredient.RecipeIngredientDeleteServlet;
import net.kleditzsch.SmartHome.view.recipe.admin.ingredient.RecipeIngredientFormServlet;
import net.kleditzsch.SmartHome.view.recipe.admin.ingredient.RecipeIngredientListServlet;
import net.kleditzsch.SmartHome.view.recipe.admin.settings.RecipeSettingsServlet;
import net.kleditzsch.SmartHome.view.recipe.admin.tag.RecipeTagDeleteServlet;
import net.kleditzsch.SmartHome.view.recipe.admin.tag.RecipeTagFormServlet;
import net.kleditzsch.SmartHome.view.recipe.admin.tag.RecipeTagListServlet;
import net.kleditzsch.SmartHome.view.recipe.user.RecipeBookmarkRecipeListServlet;
import net.kleditzsch.SmartHome.view.recipe.user.RecipeImageServlet;
import net.kleditzsch.SmartHome.view.recipe.user.RecipeIndexServlet;
import net.kleditzsch.SmartHome.view.recipe.user.RecipeNewRecipeListServlet;
import net.kleditzsch.SmartHome.view.recipe.user.foodlist.RecipeAddShoppingListServlet;
import net.kleditzsch.SmartHome.view.recipe.user.foodlist.RecipeFoodListServlet;
import net.kleditzsch.SmartHome.view.recipe.user.foodlist.RecipeUpdateFoodListServlet;
import net.kleditzsch.SmartHome.view.recipe.user.recipe.*;
import net.kleditzsch.SmartHome.view.recipe.user.search.RecipeSearchAllServlet;
import net.kleditzsch.SmartHome.view.recipe.user.search.RecipeSearchServlet;
import net.kleditzsch.SmartHome.view.recipe.user.search.RecipeSearchTagRecipesServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Hauptklasse der Rezepteverwaltung
 */
public class RecipeApplication implements SubApplication {

    /**
     * Initlisiert die Anwendungsdaten
     */
    public void init() {

        //Index anlegen
        if (RecipeEditor.countRecipes() == 0) {

            MongoCollection collection = Application.getInstance().getDatabaseCollection(RecipeEditor.COLLECTION);
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
