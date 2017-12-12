package ca.derekcormier.recipe;

public abstract class AbstractIngredientHook<T> {
    private final String ingredientName;
    private final Class<T> dataClass;

    public AbstractIngredientHook(String ingredientName, Class<T> dataClass) {
        this.ingredientName = ingredientName;
        this.dataClass = dataClass;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public Class<T> getDataClass() {
        return dataClass;
    }

    public abstract void bake(T ingredient);
}
