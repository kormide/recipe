package ca.derekcormier.recipe;

public abstract class AbstractIngredientHook<T> {
    private final String ingredientName;

    public AbstractIngredientHook(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public abstract void bake(T ingredient);
}
