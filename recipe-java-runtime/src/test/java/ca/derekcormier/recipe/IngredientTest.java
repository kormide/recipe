package ca.derekcormier.recipe;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class IngredientTest {
    @Test
    public void testToJson_ingredientWithNoProperties() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};

        assertEquals("{\"TestIngredient\":{}}", ingredient.toJson());
    }

    @Test
    public void testToJson_ingredientWithRequired() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setRequired("required", 5);

        assertEquals("{\"TestIngredient\":{\"required\":5}}", ingredient.toJson());
    }

    @Test
    public void testToJson_ingredientWithOptional() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setOptional("optional", false, false);

        assertEquals("{\"TestIngredient\":{\"optional\":false}}", ingredient.toJson());
    }

    @Test
    public void testToJson_ingredientWithRepeatableOptional() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setOptional("optional", true, false);

        assertEquals("{\"TestIngredient\":{\"optional\":[false]}}", ingredient.toJson());
    }

    @Test
    public void testToJson_ingredientWithRepeatableOptional_multipleValues() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setOptional("optional", true, false);
        ingredient.setOptional("optional", true, true);

        assertEquals("{\"TestIngredient\":{\"optional\":[false,true]}}", ingredient.toJson());
    }

    @Test
    public void testToJson_ingredientWithCompoundOptional() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setCompoundOptional("optional", false, "a", 1, "b", "foo");

        assertEquals("{\"TestIngredient\":{\"optional\":{\"a\":1,\"b\":\"foo\"}}}", ingredient.toJson());
    }

    @Test
    public void testToJson_ingredientWithRepeatableCompoundOptional() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setCompoundOptional("optional", true, "a", 1, "b", "foo");

        assertEquals("{\"TestIngredient\":{\"optional\":{\"a\":[1],\"b\":[\"foo\"]}}}", ingredient.toJson());
    }

    @Test
    public void testToJson_ingredientWithRepeatableCompoundOptional_multipleValues() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setCompoundOptional("optional", true, "a", 1, "b", "foo");
        ingredient.setCompoundOptional("optional", true, "a", 2, "b", "bar");

        assertEquals("{\"TestIngredient\":{\"optional\":{\"a\":[1,2],\"b\":[\"foo\",\"bar\"]}}}", ingredient.toJson());
    }

    @Test
    public void testToJson_ingredientWithIntParam() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setOptional("optional", false, 10);

        assertEquals("{\"TestIngredient\":{\"optional\":10}}", ingredient.toJson());
    }

    @Test
    public void testToJson_ingredientWithBooleanParam() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setOptional("optional", false, true);

        assertEquals("{\"TestIngredient\":{\"optional\":true}}", ingredient.toJson());
    }

    @Test
    public void testToJson_ingredientWithStringParam() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setOptional("optional", false, "foobar");

        assertEquals("{\"TestIngredient\":{\"optional\":\"foobar\"}}", ingredient.toJson());
    }

    @Test
    public void testToJson_ingredientWithEnumParam() {
        Ingredient ingredient = new Ingredient("TestIngredient") {};
        ingredient.setOptional("optional", false, TestEnum.C);

        assertEquals("{\"TestIngredient\":{\"optional\":\"C\"}}", ingredient.toJson());
    }

    private enum TestEnum {
        A,
        B,
        C
    }
}
