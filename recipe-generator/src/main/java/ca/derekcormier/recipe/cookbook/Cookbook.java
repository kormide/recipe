package ca.derekcormier.recipe.cookbook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Cookbook {
    private List<Ingredient> ingredients;

    @JsonCreator
    public Cookbook(
        @JsonProperty(value = "ingredients", required = true) List<Ingredient> ingredients
    ) {
        this.ingredients = ingredients;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }
}
