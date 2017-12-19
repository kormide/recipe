package ca.derekcormier.recipe;

public class Payload {
    private Recipe recipe;
    private Cake cake;

    public Payload(Recipe recipe, Cake cake) {
        this.recipe = recipe;
        this.cake = cake;
    }

    public Ingredient getRecipe() {
        return recipe;
    }

    public Cake getCake() {
        return cake;
    }
}
