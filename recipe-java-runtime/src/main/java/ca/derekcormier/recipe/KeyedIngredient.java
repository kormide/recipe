package ca.derekcormier.recipe;

public abstract class KeyedIngredient extends Ingredient {
    private String key;

    public KeyedIngredient(String name, String domain) {
        super(name, domain);
        this.key = key;
    }

    public KeyedIngredient keyed(String key) {
        this.key = key;
        return this;
    }

    public String getKey() {
        return key;
    }

    public boolean hasKey() {
        return null != key;
    }
}
