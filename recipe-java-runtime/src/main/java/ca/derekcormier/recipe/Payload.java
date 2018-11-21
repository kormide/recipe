package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Payload {
    private Recipe recipe;
    private Cake cake;

    @JsonCreator
    public Payload(
        @JsonProperty("recipe") Recipe recipe,
        @JsonProperty("cake") Cake cake
    ) {
        this.recipe = recipe;
        this.cake = cake == null ? new Cake(): cake;
    }
    public Ingredient getRecipe() {
        return recipe;
    }

    public Cake getCake() {
        return cake;
    }
}
