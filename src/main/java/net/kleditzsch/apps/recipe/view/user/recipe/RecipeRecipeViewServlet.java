package net.kleditzsch.apps.recipe.view.user.recipe;

import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.SmartHome.utility.jtwig.JtwigFactory;
import net.kleditzsch.apps.recipe.model.editor.IngredientEditor;
import net.kleditzsch.apps.recipe.model.editor.RecipeEditor;
import net.kleditzsch.apps.recipe.model.editor.TagEditor;
import net.kleditzsch.apps.recipe.model.recipe.*;
import net.kleditzsch.apps.recipe.util.RecipeUtil;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeRecipeViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/recipe/user/recipe/recipeview.html");
        JtwigModel model = JtwigModel.newModel();

        //bearbeitungsmodus
        String edit = "none";
        if(req.getParameter("edit") != null && !req.getParameter("edit").isBlank()) {

            edit = req.getParameter("edit").trim();
        }
        model.with("edit", edit);

        try {

            ID recipeId = ID.of(req.getParameter("id").trim());
            Optional<Recipe> recipeOptional = RecipeEditor.getRecipeById(recipeId);
            if(recipeOptional.isPresent()) {

                Recipe recipe = recipeOptional.get();
                model.with("recipe", recipe);
                List<Tag> tags = TagEditor.listTags();
                model.with("tags", tags);
                model.with("tagMap", tags.stream().collect(Collectors.toMap(tag -> tag.getId().get(), tag -> tag)));
                model.with("recipeTags", recipe.getTags().stream().map(ID::get).collect(Collectors.toList()));
                List<Ingredient> ingredients = IngredientEditor.listIngredients();
                model.with("ingredients", ingredients);
                model.with("ingredientMap", ingredients.stream().collect(Collectors.toMap(in -> in.getId().get(), in -> in)));
                model.with("units", RecipeUtil.baseUnits);
                List<WorkStep> workSteps = recipe.getWorkSteps().stream().sorted().collect(Collectors.toList());
                model.with("workSteps", workSteps);
                model.with("workStepsMaxOrderId", workSteps.stream().mapToInt(WorkStep::getOrderId).summaryStatistics().getMax());
                List<Recipe> linkedRecipes = RecipeEditor.listRecipesByIdList(
                        recipe.getLinkedRecipes()
                                .stream()
                                .map(link -> link.getRecipeId())
                                .collect(Collectors.toList())
                );
                Collections.sort(linkedRecipes);
                Map<String, Recipe> linkedRecipesMap = linkedRecipes.stream().collect(Collectors.toMap(re -> re.getId().get(), re -> re));
                model.with("linkedRecipes", linkedRecipes);
                model.with("linkedRecipesMap", linkedRecipesMap);
                if(edit.equals("linkedrecipe")) {

                    model.with("garnishRecipes", RecipeEditor.listGarnishRecipes());
                }
                List<Recipe> recipes = new ArrayList<>();
                recipes.add(recipe);
                recipes.addAll(linkedRecipes);
                List<IngredientAmount> ingredientAmounts = RecipeUtil.combineRecipeIngredients(recipes);
                model.with("combinedIngredientAmounts", ingredientAmounts);

            } else {

                throw new Exception();
            }
        } catch (Exception e) {}

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
