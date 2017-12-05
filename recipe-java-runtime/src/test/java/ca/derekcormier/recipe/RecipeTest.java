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
    public void testToJson_ingredientWithNoProperties() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        Recipe recipe = Recipe.prepare(ingredient);

        assertEquals("{\"Recipe\":{\"ingredients\":[{\"TestIngredient\":{}}]}}", recipe.toJson());
    }

    @Test
    public void testToJson_ingredientWithRequired() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setRequired("required", 5);
        Recipe recipe = Recipe.prepare(ingredient);

        assertEquals("{\"Recipe\":{\"ingredients\":[{\"TestIngredient\":{\"required\":5}}]}}", recipe.toJson());
    }

    @Test
    public void testToJson_ingredientWithOptional() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setOptional("optional", false, false);
        Recipe recipe = Recipe.prepare(ingredient);

        assertEquals("{\"Recipe\":{\"ingredients\":[{\"TestIngredient\":{\"optional\":false}}]}}", recipe.toJson());
    }

    @Test
    public void testToJson_ingredientWithRepeatableOptional() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setOptional("optional", true, false);
        Recipe recipe = Recipe.prepare(ingredient);

        assertEquals("{\"Recipe\":{\"ingredients\":[{\"TestIngredient\":{\"optional\":[false]}}]}}", recipe.toJson());
    }

    @Test
    public void testToJson_ingredientWithRepeatableOptional_multipleValues() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setOptional("optional", true, false);
        ingredient.setOptional("optional", true, true);

        Recipe recipe = Recipe.prepare(ingredient);

        assertEquals("{\"Recipe\":{\"ingredients\":[{\"TestIngredient\":{\"optional\":[false,true]}}]}}", recipe.toJson());
    }

    @Test
    public void testToJson_ingredientWithCompoundOptional() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setCompoundOptional("optional", false, "a", 1, "b", "foo");
        Recipe recipe = Recipe.prepare(ingredient);

        assertEquals("{\"Recipe\":{\"ingredients\":[{\"TestIngredient\":{\"optional\":{\"a\":1,\"b\":\"foo\"}}}]}}", recipe.toJson());
    }

    @Test
    public void testToJson_ingredientWithRepeatableCompoundOptional() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setCompoundOptional("optional", true, "a", 1, "b", "foo");
        Recipe recipe = Recipe.prepare(ingredient);

        assertEquals("{\"Recipe\":{\"ingredients\":[{\"TestIngredient\":{\"optional\":{\"a\":[1],\"b\":[\"foo\"]}}}]}}", recipe.toJson());
    }

    @Test
    public void testToJson_ingredientWithRepeatableCompoundOptional_multipleValues() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setCompoundOptional("optional", true, "a", 1, "b", "foo");
        ingredient.setCompoundOptional("optional", true, "a", 2, "b", "bar");

        Recipe recipe = Recipe.prepare(ingredient);

        assertEquals("{\"Recipe\":{\"ingredients\":[{\"TestIngredient\":{\"optional\":{\"a\":[1,2],\"b\":[\"foo\",\"bar\"]}}}]}}", recipe.toJson());
    }

    @Test
    public void testToJson_ingredientWithIntParam() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setOptional("optional", false, 10);
        Recipe recipe = Recipe.prepare(ingredient);

        assertEquals("{\"Recipe\":{\"ingredients\":[{\"TestIngredient\":{\"optional\":10}}]}}", recipe.toJson());
    }

    @Test
    public void testToJson_ingredientWithBooleanParam() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setOptional("optional", false, true);
        Recipe recipe = Recipe.prepare(ingredient);

        assertEquals("{\"Recipe\":{\"ingredients\":[{\"TestIngredient\":{\"optional\":true}}]}}", recipe.toJson());
    }

    @Test
    public void testToJson_ingredientWithStringParam() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setOptional("optional", false, "foobar");
        Recipe recipe = Recipe.prepare(ingredient);

        assertEquals("{\"Recipe\":{\"ingredients\":[{\"TestIngredient\":{\"optional\":\"foobar\"}}]}}", recipe.toJson());
    }

    @Test
    public void testToJson_ingredientWithEnumParam() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setOptional("optional", false, TestEnum.C);
        Recipe recipe = Recipe.prepare(ingredient);

        assertEquals("{\"Recipe\":{\"ingredients\":[{\"TestIngredient\":{\"optional\":\"C\"}}]}}", recipe.toJson());
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

    private enum TestEnum {
        A,
        B,
        C
    }
}
