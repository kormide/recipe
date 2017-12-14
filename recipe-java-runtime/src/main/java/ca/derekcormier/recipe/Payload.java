package ca.derekcormier.recipe;

public class Payload {
    private Ingredient ingredient;
    private Cake cake;

    public Payload(Ingredient ingredient, Cake cake) {
        this.ingredient = ingredient;
        this.cake = cake;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public Cake getCake() {
        return cake;
    }
}
