package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class RecipeSnapshot extends Ingredient {
    @JsonProperty("ingredients")
    private final List<Ingredient> ingredients = new ArrayList<>();
    @JsonProperty("context")
    private String context = null;
    @JsonProperty("contextIngredient")
    private KeyedIngredient contextIngredient = null;

    public RecipeSnapshot() {
        super("Recipe");
    }

    List<Ingredient> getIngredients() {
        return ingredients;
    }

    public String getContext() {
        return context;
    }

    public KeyedIngredient getContextIngredient() {
        return contextIngredient;
    }
}
