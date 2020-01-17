package net.kleditzsch.SmartHome.view.recipe.user.recipe;

import net.kleditzsch.SmartHome.global.base.ID;
import net.kleditzsch.SmartHome.model.recipe.editor.IngredientEditor;
import net.kleditzsch.SmartHome.model.recipe.editor.RecipeEditor;
import net.kleditzsch.SmartHome.model.recipe.editor.TagEditor;
import net.kleditzsch.SmartHome.model.recipe.recipe.Recipe;
import net.kleditzsch.SmartHome.util.form.FormValidation;
import net.kleditzsch.SmartHome.util.image.UploadUtil;
import net.kleditzsch.SmartHome.util.jtwig.JtwigFactory;
import org.eclipse.jetty.io.WriterOutputStream;
import org.eclipse.jetty.server.Request;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class RecipeRecipeFormServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //Template Engine initalisieren
        JtwigTemplate template = JtwigFactory.fromClasspath("/webserver/template/recipe/user/recipe/recipeform.html");
        JtwigModel model = JtwigModel.newModel();

        boolean addElement = true;
        Recipe recipe = null;

        if(req.getParameter("id") != null) {

            addElement = false;

            //Schaltserver laden
            try {

                ID id = ID.of(req.getParameter("id").trim());

                Optional<Recipe> recipeOptional = RecipeEditor.getRecipeById(id);
                if(recipeOptional.isPresent()) {

                    recipe = recipeOptional.get();
                } else {

                    //Element nicht gefunden
                    throw new Exception();
                }

            } catch (Exception e) {

                model.with("error", "Das Rezept wurde nicht gefunden");
            }
        } else {

            recipe = new Recipe();
            recipe.setId(ID.create());
        }
        model.with("addElement", addElement);
        model.with("recipe", recipe);

        model.with("ingredients", IngredientEditor.listIngredients());
        model.with("tags", TagEditor.listTags());
        model.with("garnishRecipes", RecipeEditor.listGarnishRecipes());
        model.with("defaultImages", Recipe.defaultImages);
        if(Recipe.defaultImages.containsKey(recipe.getImageFile()) ) {

            model.with("defaultImage", recipe.getImageFile());
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Dateiupload initalisieren
        req.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, new MultipartConfigElement(
                System.getProperty("java.io.tmpdir"),    //Temp Ordner
                1024L * 1024L * 50L,          //Maximale Dateigröße
                1024L * 1024L * 60L,       //Maximale größe der gesmten Anfrage
                1024 * 1024 * 5           //Dateigröße ab der in den Temp Ordner geschrieben wird
        ));

        ID recipeId = null;
        Part image = null;
        String imageUrl = null, defaultImage = null;

        //Formulardaten Valisieren
        FormValidation form = FormValidation.create(req);
        boolean addElement = form.getBoolean("addElement", "neues Element");
        if(!addElement) {
            recipeId = form.getId("id", "Rezept ID");
        }
        String name = form.getString("name", "name", 3, 100);
        String description = form.optString("description", "Beschreibung", "", 0, 100_000);

        if(form.uploadNotEmpty("imgae")) {

            image = form.getUploadedFile("imgae", "Bild", 2_097_152, UploadUtil.allowedImageContentTypes);
        }
        imageUrl = form.optString("imageUrl", "Bild URL", null, 3, 1000);
        List<String> whiteList = new ArrayList<String>(Recipe.defaultImages.keySet());
        whiteList.add("none");
        defaultImage = form.optString("defaultImage", "Standard Bild", null, whiteList);
        if(defaultImage.equals("none")) {

            defaultImage = null;
        }

        int totalDuration = form.getInteger("totalDuration", "Gesamtzeit", 1, 10_000);
        int workDuration = form.getInteger("workDuration", "Arbeitszeit", 0, 10_000);
        int baseServings = form.getInteger("baseServings", "Portionen", 1, 100);
        Recipe.Type type = form.getEnum("type", "Typ", Recipe.Type.class);
        Recipe.Difficulty difficulty = form.getEnum("difficulty", "Schwierigkeit", Recipe.Difficulty.class);

        if (form.isSuccessful()) {

            if (addElement) {

                //neues Element
                Recipe recipe = new Recipe();
                recipe.setId(ID.create());
                recipe.setName(name);
                recipe.setDescription(description);
                recipe.setTotalDuration(totalDuration);
                recipe.setWorkDuration(workDuration);
                recipe.setBaseServings(baseServings);
                recipe.setType(type);
                recipe.setDifficulty(difficulty);
                recipe.setInsertDate(LocalDateTime.now());

                Path targetDirectory = Paths.get("upload/recipe");
                if(image != null) {

                    //Datei hochgeladen
                    Path targetFile = UploadUtil.handleUploadedImage(image, targetDirectory);
                    recipe.setImageFile(targetFile.getFileName().toString());
                    image.delete();
                } else if(imageUrl != null) {

                    //Datei aus dem Internet herunterladen
                    Path targetFile = null;
                    try {

                        targetFile = UploadUtil.handleImageUrl(imageUrl, targetDirectory);
                        recipe.setImageFile(targetFile.getFileName().toString());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if(defaultImage != null) {

                    //Standard Bild
                    recipe.setImageFile(defaultImage);
                }

                RecipeEditor.addRecipe(recipe);

                req.getSession().setAttribute("success", true);
                req.getSession().setAttribute("message", "Das Rezept wurde erfolgreich hinzugefügt");
                resp.sendRedirect("/recipe/recipeview?id=" + recipe.getId().get());
            } else {

                //Element bearbeiten
                Optional<Recipe> recipeOptional = RecipeEditor.getRecipeById(recipeId);
                if (recipeOptional.isPresent()) {

                    Recipe recipe = recipeOptional.get();
                    recipe.setName(name);
                    recipe.setDescription(description);
                    recipe.setTotalDuration(totalDuration);
                    recipe.setWorkDuration(workDuration);
                    recipe.setBaseServings(baseServings);
                    recipe.setType(type);
                    recipe.setDifficulty(difficulty);

                    Path targetDirectory = Paths.get("upload/recipe");
                    if(image != null) {

                        //Datei hochgeladen
                        Path targetFile = UploadUtil.handleUploadedImage(image, targetDirectory);

                        //altes Bild löschen und neues speichern
                        if(recipe.getImageFile() != null && recipe.getImageFile().length() > 0 && !Recipe.defaultImages.containsKey(recipe.getImageFile())) {

                            Files.delete(targetDirectory.resolve(recipe.getImageFile()));
                        }
                        recipe.setImageFile(targetFile.getFileName().toString());

                        image.delete();
                    } else if(imageUrl != null) {

                        //Datei aus dem Internet herunterladen
                        Path targetFile = null;
                        try {

                            targetFile = UploadUtil.handleImageUrl(imageUrl, targetDirectory);

                            //altes Bild löschen und neues speichern
                            if(recipe.getImageFile() != null && recipe.getImageFile().length() > 0 && !Recipe.defaultImages.containsKey(recipe.getImageFile())) {

                                Files.delete(targetDirectory.resolve(recipe.getImageFile()));
                            }
                            recipe.setImageFile(targetFile.getFileName().toString());
                        } catch (InterruptedException e) {}
                    } else if(defaultImage != null) {

                        //Standard Bild
                        //altes Bild löschen und neues speichern
                        if(recipe.getImageFile() != null && recipe.getImageFile().length() > 0 && !Recipe.defaultImages.containsKey(recipe.getImageFile())) {

                            Files.delete(targetDirectory.resolve(recipe.getImageFile()));
                        }
                        recipe.setImageFile(defaultImage);
                    }

                    RecipeEditor.updateRecipe(recipe);

                    req.getSession().setAttribute("success", true);
                    req.getSession().setAttribute("message", "Das Rezept wurde erfolgreich bearbeitet");
                    resp.sendRedirect("/recipe/recipeview?edit=1&id=" + recipe.getId().get());
                } else {

                    req.getSession().setAttribute("success", false);
                    req.getSession().setAttribute("message", "Das Rezept konnte nicht gefunden werden");
                    resp.sendRedirect("/recipe/recipe");
                }
            }
        } else {

            //Eingaben n.i.O.
            req.getSession().setAttribute("success", false);
            req.getSession().setAttribute("message", "Fehlerhafte Eingaben");
            if(!addElement && recipeId != null) {

                resp.sendRedirect("/recipe/recipeview?id=" + recipeId.get());
            }
            resp.sendRedirect("/recipe/recipe");
        }
    }
}
