package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Recipe extends Ingredient {
    @JsonProperty("context")
    private String context;
    @JsonProperty("contextIngredient")
    private KeyedIngredient contextIngredient;
    @JsonProperty("ingredients")
    private final List<Ingredient> ingredients;

    public static Recipe prepare(Ingredient... ingredients) {
        return new Recipe(ingredients);
    }

    public static Recipe context(String context, Ingredient... ingredients) {
        Recipe recipe = new Recipe(ingredients);
        recipe.context = context;
        return recipe;
    }

    public static Recipe context(KeyedIngredient context, Ingredient... ingredients) {
        Recipe recipe = new Recipe(ingredients);
        recipe.contextIngredient = context;
        return recipe;
    }

    public Recipe() {
        super("Recipe");
        this.ingredients = new ArrayList<>();
    }

    private Recipe(Ingredient...ingredients) {
        super("Recipe");
        this.ingredients = Arrays.asList(ingredients);
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public String getContext() {
        return context;
    }

    public KeyedIngredient getContextIngredient() {
        return contextIngredient;
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
        if (recipeStack.get(0).contextIngredient != null) {
            KeyedIngredient contextIngredient = recipeStack.get(0).contextIngredient;
            if (!contextIngredient.getDomain().equals(currDomain)) {
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
                    outerRecipe.contextIngredient = r.contextIngredient;
                }

                Segment segmented = new Segment();
                segmented.domain = contextIngredient.getDomain();
                segmented.recipe = outerRecipe;
                segments.add(segmented);

                currRecipe = recipe;
                currDomain = contextIngredient.getDomain();
            }

            currRecipe.contextIngredient = contextIngredient;
        }
        for (Ingredient ingredient: recipeStack.get(0).ingredients) {
            if (ingredient instanceof Recipe) {
                Recipe recipe = new Recipe();
                recipe.contextIngredient = ((Recipe) ingredient).contextIngredient;
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
                        outerRecipe.contextIngredient = r.contextIngredient;
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
