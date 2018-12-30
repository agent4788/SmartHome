package net.kleditzsch.SmartHome.view.recipe.user.foodlist;

import net.kleditzsch.SmartHome.app.Application;
import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.global.editor.SettingsEditor;
import net.kleditzsch.SmartHome.model.global.settings.StringSetting;
import net.kleditzsch.SmartHome.model.recipe.editor.IngredientEditor;
import net.kleditzsch.SmartHome.model.recipe.editor.RecipeEditor;
import net.kleditzsch.SmartHome.model.recipe.recipe.Ingredient;
import net.kleditzsch.SmartHome.model.recipe.recipe.IngredientAmount;
import net.kleditzsch.SmartHome.model.recipe.recipe.LinkedRecipe;
import net.kleditzsch.SmartHome.model.recipe.recipe.Recipe;
import net.kleditzsch.SmartHome.model.shoppinglist.editor.ShoppingListEditor;
import net.kleditzsch.SmartHome.model.shoppinglist.list.Item;
import net.kleditzsch.SmartHome.model.shoppinglist.list.ShoppingList;
import net.kleditzsch.SmartHome.util.form.FormValidation;
import net.kleditzsch.SmartHome.util.formatter.NumberFormatUtil;
import net.kleditzsch.SmartHome.util.recipe.RecipeUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class RecipeAddShoppingListServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String shoppinListIdStr = "";
        SettingsEditor settingsEditor = Application.getInstance().getSettings();
        ReentrantReadWriteLock.ReadLock settingsLock = settingsEditor.readLock();
        settingsLock.lock();
        Optional<StringSetting> shoppingListIdOptional = settingsEditor.getStringSetting(SettingsEditor.RECIPE_SHOPPING_LIST_ID);
        if (shoppingListIdOptional.isPresent()) {

            shoppinListIdStr = shoppingListIdOptional.get().getValue();
        }
        settingsLock.unlock();

        if(!shoppinListIdStr.isBlank()) {

            try {

                ID shoppingListId = ID.of(shoppinListIdStr);
                ID recipeId = ID.of(req.getParameter("id"));
                FormValidation form = FormValidation.create(req);
                List<ID> ingredients = form.getIDList("ingredients", "Zutaten");

                Optional<Recipe> recipeOptional = RecipeEditor.getRecipeById(recipeId);
                if(recipeOptional.isPresent()) {

                    Recipe recipe = recipeOptional.get();
                    List<Recipe> linkedRecipes = RecipeEditor.listRecipesByIdList(recipe.getLinkedRecipes().stream().map(LinkedRecipe::getRecipeId).collect(Collectors.toList()));
                    List<Recipe> recipes = new ArrayList<>();
                    recipes.add(recipe);
                    recipes.addAll(linkedRecipes);

                    List<IngredientAmount> ingredientAmounts = RecipeUtil.combineRecipeIngredients(recipes);
                    List<Ingredient> ingredientList = IngredientEditor.listIngredients();

                    Optional<ShoppingList> shoppingListOptional = ShoppingListEditor.getShoppingListById(shoppingListId);
                    if(shoppingListOptional.isPresent()) {

                        ShoppingList shoppingList = shoppingListOptional.get();
                        for(ID ingredientId : ingredients) {

                            Optional<IngredientAmount> amount = ingredientAmounts.stream().filter(ia -> ia.getIngredientId().equals(ingredientId)).findFirst();
                            if(amount.isPresent()) {

                                IngredientAmount ingredientAmount = amount.get();
                                Optional<Ingredient> ingredientOptional = ingredientList.stream().filter(in -> in.getId().equals(ingredientAmount.getIngredientId())).findFirst();
                                if(ingredientOptional.isPresent()) {

                                    Ingredient ingredient = ingredientOptional.get();

                                    Item item = new Item();
                                    item.setId(ID.create());
                                    item.setName(ingredient.getName());
                                    item.setAmount(NumberFormatUtil.numberFormat(ingredientAmount.getAmount(), 0) +  " " + ingredient.getUnit());
                                    shoppingList.getItems().add(item);
                                }
                            }
                        }

                        //Einkaufsliste speichern
                        if(ShoppingListEditor.updateShoppingList(shoppingList)) {

                            //speichern erfolgreich
                            RecipeEditor.updateFoodList(recipe.getId(), true, true);

                            req.getSession().setAttribute("success", true);
                            req.getSession().setAttribute("message", "Das Rezept wurde erfolgreich auf die Einkaufsliste und Essensliste gespeichert");
                            resp.sendRedirect("/recipe/recipeview?id=" + recipeId.get());
                        } else {

                            //Speichern fehlgeschlagen
                            req.getSession().setAttribute("success", false);
                            req.getSession().setAttribute("message", "Das Rezept konnte nicht auf die Einkaufs- oder Essensliste gespeichert");
                            resp.sendRedirect("/recipe/recipeview?id=" + recipeId.get());
                        }
                    } else  {

                        //Einkaufsliste nicht gefunden
                        req.getSession().setAttribute("success", false);
                        req.getSession().setAttribute("message", "Die Einkaufsliste konnte nicht gefunden werden");
                        resp.sendRedirect("/recipe/recipeview?id=" + recipeId.get());
                    }
                } else {

                    //Rezept nicht gefunden
                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Das Rezept konnte nicht gefunden werden");
                    resp.sendRedirect("/recipe/recipeview?id=" + recipeId.get());
                }
            } catch (Exception e) {

                //Fehlerhafte Eingaben
                req.getSession().setAttribute("success", false);
                req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
                resp.sendRedirect("/recipe/recipe");
            }
        } else {

            //keine Einkaufsliste ausgewählt
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Keine EInkaufsliste ausgewählt");
            resp.sendRedirect("/recipe/recipe");
        }
    }
}
