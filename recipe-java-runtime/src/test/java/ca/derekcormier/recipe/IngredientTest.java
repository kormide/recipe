package ca.derekcormier.recipe;

import org.junit.Test;

public class IngredientTest {
    @Test(expected = IllegalArgumentException.class)
    public void testSetCompoundOptional_throwsOnNonEvenNumberOfKeyValuePairs() {
        Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
        ingredient.setCompoundOptional("optional", false, "a", 1, "b", true, "c");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetCompoundOptional_throwsOnNonKeyNotAString() {
        Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
        ingredient.setCompoundOptional("optional", false, "a", 1, 2, "foo");
    }
}
