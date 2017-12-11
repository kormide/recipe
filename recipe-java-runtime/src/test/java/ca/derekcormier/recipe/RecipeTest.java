package ca.derekcormier.recipe;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RecipeTest {
    @Test
    public void testToJson_emptyRecipe() {
        Recipe recipe = Recipe.prepare();

        assertEquals("{\"Recipe\":{\"ingredients\":[]}}", recipe.toJson());
    }

    @Test
    public void testToJson_recipeWithIngredient() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setRequired("required", 5);
        ingredient.setOptional("optional", false, false);

        Recipe recipe = Recipe.prepare(ingredient);

        assertEquals("{\"Recipe\":{\"ingredients\":[" + ingredient.toJson() + "]}}", recipe.toJson());
    }

    @Test
    public void testToJson_multipleIngredients() {
        Ingredient ingredient1 = new Ingredient("TestIngredient1") {};
        ingredient1.setOptional("optional", false, true);
        Ingredient ingredient2 = new Ingredient("TestIngredient2") {};
        ingredient2.setOptional("optional", false, -2);

        Recipe recipe = Recipe.prepare(ingredient1, ingredient2);

        assertEquals("{\"Recipe\":{\"ingredients\":[{\"TestIngredient1\":{\"optional\":true}},{\"TestIngredient2\":{\"optional\":-2}}]}}", recipe.toJson());
    }

    @Test
    public void testToJson_nestedEmptyRecipe() {
        Recipe recipe = Recipe.prepare(Recipe.prepare());

        assertEquals("{\"Recipe\":{\"ingredients\":[{\"Recipe\":{\"ingredients\":[]}}]}}", recipe.toJson());
    }
}
