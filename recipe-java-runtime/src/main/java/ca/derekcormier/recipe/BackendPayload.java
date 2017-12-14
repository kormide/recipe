package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BackendPayload {
    private IngredientSnapshot ingredient;
    private Cake cake;

    @JsonCreator
    public BackendPayload(
        @JsonProperty("ingredient") IngredientSnapshot ingredient,
        @JsonProperty("cake") Cake cake
    ) {
        this.ingredient = ingredient;
        this.cake = cake;
    }

    public IngredientSnapshot getIngredient() {
        return ingredient;
    }

    public Cake getCake() {
        return cake;
    }
}
