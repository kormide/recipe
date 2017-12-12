package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class RecipeData extends IngredientSnapshot {
    @JsonProperty("ingredients")
    private final List<IngredientSnapshot> ingredients = new ArrayList<>();

    public RecipeData() {
        super("Recipe");
    }

    List<IngredientSnapshot> getIngredients() {
        return ingredients;
    }
}
