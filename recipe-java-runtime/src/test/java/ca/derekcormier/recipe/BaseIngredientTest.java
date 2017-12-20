package ca.derekcormier.recipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BaseIngredientTest {
    @Test
    public void testSetProperty_setsProperty() {
        BaseIngredient ingredient = new BaseIngredient("TestIngredient", "A") {};
        ingredient.setProperty("foo", "bar");
        assertEquals("bar", ingredient.getProperty("foo"));
    }

    @Test
    public void testHasProperty_propertyExists() {
        BaseIngredient ingredient = new BaseIngredient("TestIngredient", "A") {};
        ingredient.setProperty("foo", "bar");
        assertTrue(ingredient.hasProperty("foo"));
    }

    @Test
    public void testHasProperty_propertyDoesNotExist() {
        BaseIngredient ingredient = new BaseIngredient("TestIngredient", "A") {};
        assertFalse(ingredient.hasProperty("foo"));
    }
}
