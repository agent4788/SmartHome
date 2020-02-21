package net.kleditzsch.applications.recipe.view.user.search;

import net.kleditzsch.smarthome.utility.form.FormValidation;
import net.kleditzsch.smarthome.utility.jtwig.JtwigFactory;
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
import java.util.stream.Collectors;

public class RecipeSearchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/recipe/user/search/search.html");
        JtwigModel model = JtwigModel.newModel();

        FormValidation form = FormValidation.create(req);
        if(form.fieldNotEmpty("query")) {

            String query = form.getString("query", "Suche");
            model.with("query", query);

            //Rezepte suchen
            List<Recipe> resultRecipes = RecipeEditor.search(query);
            if (resultRecipes.size() > 0) {

                model.with("resultRecipes", resultRecipes.subList(0, (resultRecipes.size() >= 8 ? 8 : resultRecipes.size())));
                model.with("resultRecipesCount", resultRecipes.size());
            }

            //Tags suchen
            List<Tag> tags = TagEditor.listTags();
            List<Tag> resultTags = tags.stream().filter(tag -> tag.getName().toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList());
            if (resultTags.size() > 0) {

                model.with("resultTags", resultTags);
            }
        } else {

            //kein Suchbegriff eingegeben
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
