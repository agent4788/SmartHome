package net.kleditzsch.SmartHome.model.recipe.recipe;

import java.util.Optional;

/**
 * Rezept Filter
 */
public class RecipeFilter {

    /**
     * Typ filter
     */
    private Recipe.Type type = null;

    /**
     * schwierigkeit Filtern
     */
    private Recipe.Difficulty difficulty = null;

    /**
     * gibt den zu filternden Typ zurück
     *
     * @return Typ
     */
    public Optional<Recipe.Type> getType() {
        return Optional.ofNullable(type);
    }

    /**
     * setzt den zu filternden Typ
     *
     * @param type Typ
     */
    public void setType(Recipe.Type type) {
        this.type = type;
    }

    /**
     * gibt die zu filternde Schwierigkeit zurück
     *
     * @return Schwierigkeit
     */
    public Optional<Recipe.Difficulty> getDifficulty() {
        return Optional.ofNullable(difficulty);
    }

    /**
     * setzt die zu filternde Schwierigkeit
     *
     * @param difficulty Schwierigkeit
     */
    public void setDifficulty(Recipe.Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
