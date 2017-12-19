package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class KeyedIngredientSnapshot extends IngredientSnapshot {
    @JsonProperty("key")
    private String key;

    public KeyedIngredientSnapshot(String type) {
        super(type);
    }

    public String getKey() {
        return key;
    }
}
