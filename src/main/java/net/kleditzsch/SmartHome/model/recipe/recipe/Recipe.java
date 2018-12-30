package net.kleditzsch.SmartHome.model.recipe.recipe;

import net.kleditzsch.SmartHome.global.base.Element;
import net.kleditzsch.SmartHome.global.base.ID;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Rezept
 */
public class Recipe extends Element implements Comparable<Recipe> {

    /**
     * Liste mit den Standardbildern
     */
    public static Map<String, String> defaultImages;
    static {

        Map<String, String> tmpDefaultImages = new HashMap<>();
        tmpDefaultImages.put("bild1.png", "Bild 1");
        tmpDefaultImages.put("bild2.png", "Bild 2");
        defaultImages = Collections.unmodifiableMap(tmpDefaultImages);
    }

    /**
     * Typ
     */
    public enum Type {

        MAIN_DISH,
        STARTER,
        DESSERT,
        CAKE,
        COOKIES,
        GARNISH
    }

    /**
     * Schwierigkeit
     */
    public enum Difficulty {

        LIGHT,
        MEDIUM,
        HEAVY
    }

    /**
     * Gesamtzeit in Minuten
     */
    private int totalDuration = 0;

    /**
     * Arbeitszeit in Minuten
     */
    private int workDuration = 0;

    /**
     * Schwierigkeit
     */
    private Difficulty difficulty = Difficulty.LIGHT;

    /**
     * Auf der Merkliste
     */
    private boolean bookmark = false;

    /**
     * auf der Essensliste
     */
    private boolean foodList = false;

    /**
     * Anzahl Portionen (ausgelegt auf die Menge der Zutaten
     */
    private int baseServings = 2;

    /**
     * Name der Bild Datei
     */
    private String imageFile = "";

    /**
     * Eintragungsdatim
     */
    private LocalDateTime insertDate = LocalDateTime.now();

    /**
     * Typ
     */
    private Type type = Type.MAIN_DISH;

    /**
     * Zutaten
     */
    private List<IngredientAmount> ingredientAmounts = new ArrayList<>();

    /**
     * Arbeitsschritte
     */
    private List<WorkStep> workSteps = new ArrayList<>();

    /**
     * Verknüpfte Rezepte
     */
    private List<LinkedRecipe> linkedRecipes = new ArrayList<>();

    /**
     * Tags
     */
    private List<ID> tags = new ArrayList<>();

    /**
     * gibt die gesamte Zubereitungszeit zurück
     *
     * @return gesamte Zubereitungszeit
     */
    public int getTotalDuration() {
        return totalDuration;
    }

    /**
     * setzt die gesamte Zubereitungszeit
     *
     * @param totalDuration gesamte Zubereitungszeit
     */
    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    /**
     * gibt die aktive Zubereitungszeit zurück
     *
     * @return aktive Zubereitungszeit
     */
    public Optional<Integer> getWorkDuration() {

        if(workDuration > 0) {

            return Optional.of(workDuration);
        }
        return Optional.empty();
    }

    /**
     * setzt die aktive Zubereitungszeit
     *
     * @param workDuration aktive Zubereitungszeit
     */
    public void setWorkDuration(int workDuration) {
        this.workDuration = workDuration;
    }

    /**
     * gibt die Schwierigkeit zurück
     *
     * @return Schwierigkeit
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * setzt die Schwierigkeit
     *
     * @param difficulty Schwierigkeit
     */
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * gib an ob das Rezept auf der Merkliste steht
     *
     * @return Rezept auf der Merkliste
     */
    public boolean isBookmark() {
        return bookmark;
    }

    /**
     * setzt das Rezept auf die Merkliste
     *
     * @param bookmark Rezept auf die Merkliste
     */
    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }

    /**
     * gibt an ob das Rezept auf der Essensliste ist
     *
     * @return Rezept auf der Essensliste
     */
    public boolean isFoodList() {
        return foodList;
    }

    /**
     * setzt das Rezept auf die Essensliste
     *
     * @param foodList Rezept auf der Essensliste
     */
    public void setFoodList(boolean foodList) {
        this.foodList = foodList;
    }

    /**
     * gibt die Anzahl an Portionen zurück
     *
     * @return Anzahl an Portionen
     */
    public int getBaseServings() {
        return baseServings;
    }

    /**
     * setzt die Anzahl an Portionen
     *
     * @param baseServings Anzahl an Portionen
     */
    public void setBaseServings(int baseServings) {
        this.baseServings = baseServings;
    }

    /**
     * gibt die Bilddatei zurück
     *
     * @return Bilddatei
     */
    public String getImageFile() {
        return imageFile;
    }

    /**
     * setzt die Bilddatei
     *
     * @param imageFile Bilddatei
     */
    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    /**
     * gibt das Datum der Eintragung zurück
     *
     * @return Datum der Eintragung
     */
    public LocalDateTime getInsertDate() {
        return insertDate;
    }

    /**
     * setzt das Datum der Eintragung
     *
     * @param insertDate Datum der Eintragung
     */
    public void setInsertDate(LocalDateTime insertDate) {
        this.insertDate = insertDate;
    }

    /**
     * gibt den Rezept Typ zurück
     *
     * @return Rezept Typ
     */
    public Type getType() {
        return type;
    }

    /**
     * setzt den Rezept Typ
     *
     * @param type Rezept Typ
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * gibt die Liste der Zutaten zurück
     *
     * @return Liste der Zutaten
     */
    public List<IngredientAmount> getIngredientAmounts() {
        return ingredientAmounts;
    }

    /**
     * git die Liste der Arbeitsschritte zurück
     *
     * @return Liste der Arbeitsschritte
     */
    public List<WorkStep> getWorkSteps() {
        return workSteps;
    }

    /**
     * gibt die Liste der Verknüpften Rezepte zurück
     *
     * @return Liste der Verknüpften Rezepte
     */
    public List<LinkedRecipe> getLinkedRecipes() {
        return linkedRecipes;
    }

    /**
     * gibt die Liste mit den Rezept Tags zurück
     *
     * @return Liste mit den Rezept Tags
     */
    public List<ID> getTags() {
        return tags;
    }

    @Override
    public int compareTo(Recipe o) {

        return getName().compareToIgnoreCase(o.getName());
    }
}
