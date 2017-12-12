package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

// Unwrap the ingredient name to deserialize ingredients
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.WRAPPER_OBJECT
)
public abstract class IngredientSnapshot extends BaseIngredient {
    public IngredientSnapshot(String type) {
        super(type);
    }
}