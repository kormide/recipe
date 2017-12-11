package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Recipe extends Ingredient {
    @JsonProperty("ingredients")
    private final List<Ingredient> ingredients;

    public static Recipe prepare(Ingredient...ingredients) {
        return new Recipe(ingredients);
    }

    public Recipe() {
        super("Recipe");
        this.ingredients = new ArrayList<>();
    }

    private Recipe(Ingredient...ingredients) {
        super("Recipe");
        this.ingredients = Arrays.asList(ingredients);
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }
}
