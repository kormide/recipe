package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BackendPayload {
    private RecipeSnapshot recipe;
    private Cake cake;

    @JsonCreator
    public BackendPayload(
        @JsonProperty("recipe") RecipeSnapshot recipe,
        @JsonProperty("cake") Cake cake
    ) {
        this.recipe = recipe;
        this.cake = cake;
    }

    public RecipeSnapshot getRecipe() {
        return recipe;
    }

    public Cake getCake() {
        return cake;
    }
}
