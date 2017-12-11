package ca.derekcormier.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class Oven {
    private List<BiConsumer<String,String>> dispatchers = new ArrayList<>();

    public void bake(Recipe recipe) {
        for (Ingredient ingredient: recipe.getIngredients()) {
            if (ingredient instanceof Recipe) {
                bake((Recipe)ingredient);
            }
            else {
                for (BiConsumer<String,String> dispatcher: dispatchers) {
                    dispatcher.accept(ingredient.getDomain(), ingredient.toJson());
                }
            }
        }
    }

    public void addDispatcher(BiConsumer<String,String> dispatcher) {
        dispatchers.add(dispatcher);
    }
}
