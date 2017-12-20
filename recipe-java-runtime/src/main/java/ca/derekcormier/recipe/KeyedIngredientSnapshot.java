package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class KeyedIngredientSnapshot extends IngredientSnapshot {
    @JsonProperty("key")
    private String key;

    public KeyedIngredientSnapshot(String type) {
        super(type);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
