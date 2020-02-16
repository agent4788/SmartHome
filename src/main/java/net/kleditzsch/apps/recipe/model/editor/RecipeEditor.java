package net.kleditzsch.apps.recipe.model.editor;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.UpdateResult;
import net.kleditzsch.SmartHome.SmartHome;
import net.kleditzsch.SmartHome.model.base.ID;
import net.kleditzsch.apps.recipe.model.recipe.*;
import net.kleditzsch.SmartHome.utility.datetime.DatabaseDateTimeUtil;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.*;

/**
 * Rezepteveraltung
 */
public abstract class RecipeEditor {

    public static final String COLLECTION = "recipe.recipe";

    /**
     * gibt eine Liste aller Rezepte zurück
     *
     * @return Liste aller Rezepte
     */
    public static List<Recipe> listRecipes() {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find()
                .sort(Sorts.ascending("name"));

        List<Recipe> recipes = new ArrayList<>(50);
        for(Document document : iterator) {

            recipes.add(documentToRecipe(document));
        }
        return recipes;
    }

    /**
     * gibt eine Liste mit den Rezepten des Bereiches zurück
     *
     * @param start start Index
     * @param length länge
     * @return Liste der Rezepte
     */
    public static List<Recipe> listRecipes(long start, long length) {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find()
                .sort(Sorts.ascending("name"))
                .skip(((Long) start).intValue())
                .limit(((Long) length).intValue());

        List<Recipe> recipes = new ArrayList<>(50);
        for(Document document : iterator) {

            recipes.add(documentToRecipe(document));
        }
        return recipes;
    }

    /**
     * gibt eine gefilterte Liste mit den Rezepten des Bereiches zurück
     *
     * @param filter Filter
     * @param start start Index
     * @param length länge
     * @return Liste der Rezepte
     */
    public static List<Recipe> listRecipes(RecipeFilter filter, long start, long length) {

        Bson bsonFilter = null;
        if(filter.getDifficulty().isPresent()) {

            bsonFilter = Filters.eq("difficulty", filter.getDifficulty().get().toString());
        } else if(filter.getType().isPresent()) {

            bsonFilter = Filters.eq("type", filter.getType().get().toString());
        }

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = null;

        if(bsonFilter != null) {

            iterator = collection.find(bsonFilter)
                    .sort(Sorts.ascending("name"))
                    .skip(((Long) start).intValue())
                    .limit(((Long) length).intValue());
        } else {

            iterator = collection.find()
                    .sort(Sorts.ascending("name"))
                    .skip(((Long) start).intValue())
                    .limit(((Long) length).intValue());
        }

        List<Recipe> recipes = new ArrayList<>(50);
        for(Document document : iterator) {

            recipes.add(documentToRecipe(document));
        }
        return recipes;
    }

    /**
     * gibt eine Liste mit allen Beilagen Rezepten zurück
     *
     * @return Liste mit allen Beilagen Rezepten
     */
    public static List<Recipe> listGarnishRecipes() {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find(eq("type", Recipe.Type.GARNISH.toString()))
                .sort(Sorts.ascending("name"));

        List<Recipe> recipes = new ArrayList<>(50);
        for(Document document : iterator) {

            recipes.add(documentToRecipe(document));
        }
        return recipes;
    }

    /**
     * gibt eine Liste mit den Rezepten der IDs zurück
     *
     * @param idList Liste mit den IDs
     * @return Liste der Rezepte
     */
    public static List<Recipe> listRecipesByIdList(List<ID> idList) {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find(
                in("_id", idList.stream().map(ID::toString).collect(Collectors.toList()))
        );

        List<Recipe> recipes = new ArrayList<>(50);
        for(Document document : iterator) {

            recipes.add(documentToRecipe(document));
        }
        return recipes;
    }

    /**
     * sucht ein Rezept in der Datenbank und gibt dessen Objekt zurück
     *
     * @param id Rezept ID
     * @return Rezept Objekt
     */
    public static Optional<Recipe> getRecipeById(ID id) {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable iterator = collection.find(new Document().append("_id", id.get()));
        if(iterator.first() != null) {

            return Optional.of(documentToRecipe((Document) iterator.first()));
        }
        return Optional.empty();
    }

    /**
     * gibt die Liste mit den Rezepten die das Tag enthalten zurück
     *
     * @param tagId Tag ID
     * @return Liste von Rezepten mit dem Tag
     */
    public static List<Recipe> listRecipesWithTag(ID tagId) {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find(in("tags", tagId.get()))
                .sort(Sorts.descending("name"));

        List<Recipe> recipes = new ArrayList<>(50);
        for(Document document : iterator) {

            recipes.add(documentToRecipe(document));
        }
        return recipes;
    }

    /**
     * gibt die Liste mit den neustsen Rezepten zurück
     *
     * @param length Anzahl der Rezepte
     * @return Liste mit den neustsen Rezepten
     */
    public static List<Recipe> listNewestRecipes(int length) {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find().sort(Sorts.descending("insertDate")).limit(length);

        List<Recipe> recipes = new ArrayList<>(50);
        for(Document document : iterator) {

            recipes.add(documentToRecipe(document));
        }
        return recipes;
    }

    /**
     * gibt die Liste mit den IDs der neustsen Rezepte zurück
     *
     * @param length Anzahl der Rezepte
     * @return Liste mit den IDs der neustsen Rezepte
     */
    public static List<ID> listNewestRecipeIds(int length) {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find().sort(Sorts.descending("insertDate"))
                .limit(length)
                .projection(fields(include("_id")));

        List<ID> recipeIds = new ArrayList<>(50);
        for(Document document : iterator) {

            recipeIds.add(ID.of(document.getString("_id")));
        }
        return recipeIds;
    }

    /**
     * gibt eine Liste mit den Rezepten zurück die als "gemerkt" markiert sind
     *
     * @return Liste mit den Rezepten die als "gemerkt" markiert sind
     */
    public static List<Recipe> listBookmaredRecipes() {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find(eq("bookmark", true)).sort(Sorts.ascending("name"));

        List<Recipe> recipes = new ArrayList<>(50);
        for(Document document : iterator) {

            recipes.add(documentToRecipe(document));
        }
        return recipes;
    }

    /**
     * gibt eine Liste mit den Rezepten zurück die auf die Essensliste gesetzt sind
     *
     * @return Liste mit den Rezepten die auf die Essensliste gesetzt sind
     */
    public static List<Recipe> listFoodListRecipes() {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find(eq("foodList", true)).sort(Sorts.ascending("name"));

        List<Recipe> recipes = new ArrayList<>(50);
        for(Document document : iterator) {

            recipes.add(documentToRecipe(document));
        }
        return recipes;
    }

    /**
     * gibt eine Liste mit den Rezept IDs zurück die als "demnächst anschauen" markiert sind
     *
     * @return Liste mit den Rezept IDs die als "gemerkt" markiert sind
     */
    public static List<ID> listBookmaredRecipeIds() {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find(eq("bookmark", true)).sort(Sorts.ascending("name"));

        List<ID> recipeIds = new ArrayList<>(50);
        for(Document document : iterator) {

            recipeIds.add(ID.of(document.getString("_id")));
        }
        return recipeIds;
    }

    /**
     * Rezept suchen
     *
     * @param query Suchbegriff
     * @return Liste mit den Suchergebnissen
     */
    public static List<Recipe> search(String query) {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        FindIterable<Document> iterator = collection.find(text(query))
                .projection(Projections.metaTextScore("score"))
                .sort(Sorts.metaTextScore("score"));

        List<Recipe> recipes = new ArrayList<>(50);
        for(Document document : iterator) {

            recipes.add(documentToRecipe(document));
        }
        return recipes;
    }

    /**
     * gibt die Aznahl der Rezepte zurück
     *
     * @return Anzahl der Rezepte
     */
    public static long countRecipes() {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        return collection.countDocuments();
    }

    /**
     * gibt die Aznahl der Rezepte zurück
     *
     * @return Anzahl der Rezepte
     */
    public static long countRecipes(RecipeFilter filter) {

        Bson bsonFilter = null;
        if(filter.getDifficulty().isPresent()) {

            bsonFilter = Filters.eq("difficulty", filter.getDifficulty().get().toString());
        } else if(filter.getType().isPresent()) {

            bsonFilter = Filters.eq("type", filter.getType().get().toString());
        }

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        if(bsonFilter != null) {

            return collection.countDocuments(bsonFilter);
        }
        return collection.countDocuments();
    }

    /**
     * speichert ein neues Rezept in der Datenbanbk
     *
     * @param recipe Rezept
     * @return ID des Rezeptes
     */
    public static ID addRecipe(Recipe recipe) {

        List<Document> ingredientAmounts = new ArrayList<>(recipe.getIngredientAmounts().size());
        for(IngredientAmount ingredientAmount : recipe.getIngredientAmounts()) {

            Document doc = new Document();
            doc.append("_id", ingredientAmount.getId().get());
            doc.append("ingredientId", ingredientAmount.getIngredientId().get());
            doc.append("amount", ingredientAmount.getAmount());
            ingredientAmounts.add(doc);
        }

        List<Document> workSteps = new ArrayList<>(recipe.getWorkSteps().size());
        for(WorkStep workStep : recipe.getWorkSteps()) {

            Document doc = new Document();
            doc.append("_id", workStep.getId().get());
            doc.append("description", workStep.getDescription().orElse(""));
            doc.append("orderId", workStep.getOrderId());
            doc.append("workDuration", workStep.getWorkDuration().orElse(0));
            workSteps.add(doc);
        }

        List<Document> linkedRecipes = new ArrayList<>(recipe.getWorkSteps().size());
        for(LinkedRecipe linkedRecipe : recipe.getLinkedRecipes()) {

            Document doc = new Document();
            doc.append("_id", linkedRecipe.getId().get());
            doc.append("orderId", linkedRecipe.getOrderId());
            doc.append("recipeId", linkedRecipe.getRecipeId().get());
            doc.append("optional", linkedRecipe.isOptional());
            linkedRecipes.add(doc);
        }

        recipe.setId(ID.create());
        Document document = new Document()
                .append("_id", recipe.getId().get())
                .append("description", recipe.getDescription().orElse(""))
                .append("name", recipe.getName())
                .append("totalDuration", recipe.getTotalDuration())
                .append("workDuration", recipe.getWorkDuration().orElse(0))
                .append("difficulty", recipe.getDifficulty().toString())
                .append("bookmark", recipe.isBookmark())
                .append("foodList", recipe.isFoodList())
                .append("baseServings", recipe.getBaseServings())
                .append("imageFile", recipe.getImageFile())
                .append("insertDate", recipe.getInsertDate())
                .append("type", recipe.getType().toString())
                .append("ingredientAmounts", ingredientAmounts)
                .append("workSteps", workSteps)
                .append("linkedRecipes", linkedRecipes)
                .append("tags", recipe.getTags().stream().map(ID::get).collect(Collectors.toList()));

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        collection.insertOne(document);

        return recipe.getId();
    }

    /**
     * aktualisiert ein Rezept in der Datenbank
     *
     * @param recipe Rezept
     * @return Erfolgsmeldung
     */
    public static boolean updateRecipe(Recipe recipe) {

        List<Document> ingredientAmounts = new ArrayList<>(recipe.getIngredientAmounts().size());
        for(IngredientAmount ingredientAmount : recipe.getIngredientAmounts()) {

            Document doc = new Document();
            doc.append("_id", ingredientAmount.getId().get());
            doc.append("ingredientId", ingredientAmount.getIngredientId().get());
            doc.append("amount", ingredientAmount.getAmount());
            ingredientAmounts.add(doc);
        }

        List<Document> workSteps = new ArrayList<>(recipe.getWorkSteps().size());
        for(WorkStep workStep : recipe.getWorkSteps()) {

            Document doc = new Document();
            doc.append("_id", workStep.getId().get());
            doc.append("description", workStep.getDescription().orElse(""));
            doc.append("orderId", workStep.getOrderId());
            doc.append("workDuration", workStep.getWorkDuration().orElse(0));
            workSteps.add(doc);
        }

        List<Document> linkedRecipes = new ArrayList<>(recipe.getWorkSteps().size());
        for(LinkedRecipe linkedRecipe : recipe.getLinkedRecipes()) {

            Document doc = new Document();
            doc.append("_id", linkedRecipe.getId().get());
            doc.append("orderId", linkedRecipe.getOrderId());
            doc.append("recipeId", linkedRecipe.getRecipeId().get());
            doc.append("optional", linkedRecipe.isOptional());
            linkedRecipes.add(doc);
        }

        MongoCollection movieCollection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = movieCollection.updateOne(
                eq("_id", recipe.getId().get()),
                combine(
                        set("description", recipe.getDescription().orElse("")),
                        set("name", recipe.getName()),
                        set("totalDuration", recipe.getTotalDuration()),
                        set("workDuration", recipe.getWorkDuration().orElse(0)),
                        set("difficulty", recipe.getDifficulty().toString()),
                        set("bookmark", recipe.isBookmark()),
                        set("foodList", recipe.isFoodList()),
                        set("baseServings", recipe.getBaseServings()),
                        set("imageFile", recipe.getImageFile()),
                        set("insertDate", recipe.getInsertDate()),
                        set("type", recipe.getType().toString()),
                        set("ingredientAmounts", ingredientAmounts),
                        set("workSteps", workSteps),
                        set("linkedRecipes", linkedRecipes),
                        set("tags", recipe.getTags().stream().map(ID::get).collect(Collectors.toList()))
                )
        );
        return result.wasAcknowledged();
    }

    /**
     * aktuelisiert die Lesezeichen markierung
     *
     * @param recipeId Rezept ID
     * @param bookmark Lesezeichen
     * @return Erfolgsmeldung
     */
    public static boolean updateBookmark(ID recipeId, boolean bookmark) {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        UpdateResult result = collection.updateOne(
                eq("_id", recipeId.get()),
                combine(
                        set("bookmark", bookmark)
                )
        );
        return result.wasAcknowledged();
    }

    /**
     * aktuelisiert die Essensliste markierung
     *
     * @param recipeId Rezept ID
     * @param foodList Essensliste
     * @return Erfolgsmeldung
     */
    public static boolean updateFoodList(ID recipeId, boolean foodList) {

        return updateFoodList(recipeId, foodList, false);
    }

    /**
     * aktuelisiert die Essensliste markierung
     *
     * @param recipeId Rezept ID
     * @param foodList Essensliste
     * @return Erfolgsmeldung
     */
    public static boolean updateFoodList(ID recipeId, boolean foodList, boolean resetBookmark) {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        if(resetBookmark) {

            UpdateResult result = collection.updateOne(
                    eq("_id", recipeId.get()),
                    combine(
                            set("foodList", foodList),
                            set("bookmark", false)
                    )
            );
            return result.wasAcknowledged();
        }
        UpdateResult result = collection.updateOne(
                eq("_id", recipeId.get()),
                combine(
                        set("foodList", foodList)
                )
        );
        return result.wasAcknowledged();
    }

    /**
     * löscht ein Rezept aus der Datenbank
     *
     * @param id Rezept ID
     * @return Erfolgsmeldung
     */
    public static boolean deleteRecipe(ID id) {

        MongoCollection collection = SmartHome.getInstance().getDatabaseCollection(COLLECTION);
        if(collection.deleteOne(eq("_id", id.get())).getDeletedCount() == 1) {

            return true;
        }
        return false;
    }

    /**
     * liest die Daten eines Dokumentes in ein Rezept Objekt ein
     *
     * @param document Dokument
     * @return Rezept
     */
    private static Recipe documentToRecipe(Document document) {

        Recipe element = new Recipe();
        element.setId(ID.of(document.getString("_id")));
        element.setName(document.getString("name"));
        element.setDescription(document.getString("description"));
        element.setTotalDuration(document.getInteger("totalDuration"));
        element.setWorkDuration(document.getInteger("workDuration"));
        element.setDifficulty(Recipe.Difficulty.valueOf(document.getString("difficulty")));
        element.setBookmark(document.getBoolean("bookmark"));
        element.setFoodList(document.getBoolean("foodList"));
        element.setBaseServings(document.getInteger("baseServings"));
        element.setImageFile(document.getString("imageFile"));
        element.setInsertDate(DatabaseDateTimeUtil.dateToLocalDateTime(document.getDate("insertDate")));
        element.setType(Recipe.Type.valueOf(document.getString("type")));

        //Zutaten
        List<Document> ingredientAmounts = (List<Document>) document.get("ingredientAmounts");
        for(Document doc : ingredientAmounts) {

            IngredientAmount elem = new IngredientAmount();
            elem.setId(ID.of(doc.getString("_id")));
            elem.setIngredientId(ID.of(doc.getString("ingredientId")));
            elem.setAmount(doc.get("amount") instanceof Integer ? doc.getInteger("amount") : doc.getDouble("amount"));
            element.getIngredientAmounts().add(elem);
        }

        //Arbeitsschritte
        List<Document> workSteps = (List<Document>) document.get("workSteps");
        for(Document doc : workSteps) {

            WorkStep elem = new WorkStep();
            elem.setId(ID.of(doc.getString("_id")));
            elem.setDescription(doc.getString("description"));
            elem.setOrderId(doc.getInteger("orderId"));
            elem.setWorkDuration(doc.getInteger("workDuration"));
            element.getWorkSteps().add(elem);
        }

        //Verknüpfte Rezepte
        List<Document> linkedRecipes = (List<Document>) document.get("linkedRecipes");
        for(Document doc : linkedRecipes) {

            LinkedRecipe elem = new LinkedRecipe();
            elem.setId(ID.of(doc.getString("_id")));
            elem.setOrderId(doc.getInteger("orderId"));
            elem.setRecipeId(ID.of(doc.getString("recipeId")));
            elem.setOptional(doc.getBoolean("optional"));
            element.getLinkedRecipes().add(elem);
        }

        //Tags
        List<String> tags = (List<String>) document.get("tags");
        for(String idStr : tags) {

            element.getTags().add(ID.of(idStr));
        }

        return element;
    }
}
