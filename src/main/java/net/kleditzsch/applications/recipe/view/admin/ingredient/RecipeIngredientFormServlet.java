package net.kleditzsch.applications.recipe.view.admin.ingredient;

import net.kleditzsch.smarthome.model.base.ID;
import net.kleditzsch.smarthome.utility.form.FormValidation;
import net.kleditzsch.smarthome.utility.jtwig.JtwigFactory;
import net.kleditzsch.applications.recipe.model.editor.IngredientEditor;
import net.kleditzsch.applications.recipe.model.recipe.Ingredient;
import net.kleditzsch.applications.recipe.util.RecipeUtil;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class RecipeIngredientFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/recipe/admin/ingredient/ingredientform.html");
        JtwigModel model = JtwigModel.newModel();

        //Daten vorbereiten
        boolean addElement = true;
        Ingredient ingredient = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            //Schaltserver laden
            try {

                ID id = ID.of(req.getParameter("id").trim());

                Optional<Ingredient> ingredientOptional = IngredientEditor.getIngredientById(id);
                if(ingredientOptional.isPresent()) {

                    ingredient = ingredientOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Die Zutat wurde nicht gefunden");
            }
        } else {

            ingredient = new Ingredient();
            ingredient.setId(ID.create());
        }
        model.with("addElement", addElement);
        model.with("ingredient", ingredient);
        model.with("units", RecipeUtil.baseUnits);

        //Template rendern
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        template.render(model, new WriterOutputStream(resp.getWriter()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Optionale Parameter
        ID tagId = null;

        FormValidation form = FormValidation.create(req);
        boolean addElement = form.getBoolean("addElement", "neues Element");
        if(!addElement) {
            tagId = form.getId("id", "ID");
        }
        String name = form.getString("name", "Titel", 3, 50);
        String unit = form.optString("unit", "Einheit", "", new ArrayList<>(RecipeUtil.baseUnits.keySet()));

        if (form.isSuccessful()) {

            if(addElement) {

                //neues Element hinzufügen
                Ingredient ingredient = new Ingredient();
                ingredient.setId(ID.create());
                ingredient.setName(name);
                ingredient.setUnit(unit);

                IngredientEditor.addIngredient(ingredient);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Die Zutat wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/recipe/admin/ingredient");
            } else {

                //Element bearbeiten
                Optional<Ingredient> ingredientOptional = IngredientEditor.getIngredientById(tagId);
                if (ingredientOptional.isPresent()) {

                    Ingredient ingredient = ingredientOptional.get();
                    ingredient.setName(name);
                    ingredient.setUnit(unit);

                    IngredientEditor.updateIngredient(ingredient);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Die Zutat wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/recipe/admin/ingredient");
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Die Zutat konnte nicht gefunden werden");
                    resp.sendRedirect("/recipe/admin/ingredient");
                }
            }

        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            resp.sendRedirect("/recipe/admin/ingredient");
        }
    }
}
