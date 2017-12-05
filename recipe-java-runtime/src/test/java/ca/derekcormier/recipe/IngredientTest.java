package ca.derekcormier.recipe;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.HashMap;

public class IngredientTest {
    @Test
    public void testGetProperties_wrapsPropertiesWithType() {
        TestIngredient testIngredient = new TestIngredient();
        testIngredient.setProperty("foo", "bar");

        HashMap expected = new HashMap<>();
        expected.put("TestIngredient", new HashMap());
        ((HashMap)expected.get("TestIngredient")).put("foo", "bar");

        assertEquals(expected, testIngredient.getProperties());
    }

    @Test
    public void testGetProperties_wrapsPropertiesWithType_noProperties() {
        TestIngredient testIngredient = new TestIngredient();

        HashMap expected = new HashMap<>();
        expected.put("TestIngredient", new HashMap());

        assertEquals(expected, testIngredient.getProperties());
    }
}

class TestIngredient extends Ingredient {
    public TestIngredient() {
        super("TestIngredient");
    }
}
