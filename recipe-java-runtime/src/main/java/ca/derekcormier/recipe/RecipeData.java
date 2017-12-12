package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class RecipeData extends IngredientData {
    @JsonProperty("ingredients")
    private final List<IngredientData> ingredients = new ArrayList<>();

    public RecipeData() {
        super("Recipe");
    }

    List<IngredientData> getIngredients() {
        return ingredients;
    }
}
