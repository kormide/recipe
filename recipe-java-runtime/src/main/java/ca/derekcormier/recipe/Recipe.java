package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Recipe extends Ingredient {
    @JsonProperty("context")
    private String context;
    @JsonProperty("ingredients")
    private final List<Ingredient> ingredients;

    public static Recipe prepare(Ingredient... ingredients) {
        return new Recipe(ingredients);
    }

    public static Recipe context(String context, Ingredient... ingredients) {
        Recipe recipe = prepare(ingredients);
        recipe.context = context;
        return recipe;
    }

    public static Recipe context(KeyedIngredient contextIngredient, Ingredient... ingredients) {
        Objects.requireNonNull(contextIngredient);
        return prepare(contextIngredient,
            context(contextIngredient.getKey(), ingredients)
        );
    }

    protected Recipe() {
        super("Recipe");
        this.ingredients = new ArrayList<>();
    }

    protected Recipe(Ingredient...ingredients) {
        super("Recipe");
        this.ingredients = new ArrayList<>();
        for (Ingredient ingredient: ingredients) {
            // flatten context-free recipes
            if (ingredient instanceof Recipe && ((Recipe)ingredient).context == null) {
                this.ingredients.addAll(((Recipe)ingredient).ingredients);
            }
            else {
                this.ingredients.add(ingredient);
            }
        }
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public String getContext() {
        return context;
    }

    public class Segment {
        public String domain;
        public Recipe recipe;
    }

    //Segment the recipe into multiple recipes containing containing contiguous ingredients of the same domain while
    //preserving the recipe and context structure. Baking the returned recipes in order is equivalent to baking the
    //original recipe, but more efficient for sending payloads of ingredients to different services.
    protected List<Segment> segment() {
        List<Segment> segments = new ArrayList<>();
        List<Recipe> recipeStack = new ArrayList<>();
        recipeStack.add(this);
        _segment(new Recipe(), recipeStack, null, segments);
        return segments;
    }

    private void _segment(Recipe currRecipe, List<Recipe> recipeStack, String currDomain, List<Segment> segments) {
        for (Ingredient ingredient: recipeStack.get(0).ingredients) {
            if (ingredient instanceof Recipe) {
                Recipe recipe = new Recipe();
                recipe.context = ((Recipe) ingredient).context;
                recipeStack.add(0, (Recipe) ingredient);
                _segment(recipe, recipeStack, currDomain, segments);

                if (!recipe.ingredients.isEmpty()) {
                    currRecipe.ingredients.add(recipe);
                }
            }
            else {
                if (!ingredient.getDomain().equals(currDomain)) {
                    //copy recipe structure
                    Recipe outerRecipe = null;
                    Recipe recipe = null;
                    for (Recipe r : recipeStack) {
                        if (recipe == null) {
                            outerRecipe = new Recipe();
                            recipe = outerRecipe;
                        }
                        else {
                            outerRecipe = Recipe.prepare(outerRecipe);
                        }
                        outerRecipe.context = r.context;
                    }

                    Segment segmented = new Segment();
                    segmented.domain = ingredient.getDomain();
                    segmented.recipe = outerRecipe;
                    segments.add(segmented);

                    currRecipe = recipe;
                    currDomain = ingredient.getDomain();
                }

                currRecipe.ingredients.add(ingredient);
            }
        }
        recipeStack.remove(0);
    }
}
