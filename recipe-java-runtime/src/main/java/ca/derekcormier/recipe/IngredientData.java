package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class IngredientData extends PropertyMap {
    @JsonIgnore
    private final String type;

    public IngredientData(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
