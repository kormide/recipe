package ca.derekcormier.recipe;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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

    @Test
    public void testGetProperty_getsExistingValue() {
        BaseIngredient ingredient = new BaseIngredient("TestIngredient", "A") {};
        ingredient.setProperty("foo", TestEnum.B);
        TestEnum value = ingredient.getProperty("foo");
        assertEquals(TestEnum.B, value);
    }

    @Test
    public void testGetProperty_returnsNullOnNonExistentKey() {
        BaseIngredient ingredient = new BaseIngredient("TestIngredient", "A") {};
        assertNull(ingredient.getProperty("foo"));
    }

    @Test
    public void testGetProperty_withType_getsExistingValue() {
        BaseIngredient ingredient = new BaseIngredient("TestIngredient", "A") {};
        ingredient.setProperty("foo", 7);
        int value = ingredient.getProperty(int.class, "foo");
        assertEquals(7, value);
    }

    @Test
    public void testGetProperty_withType_intDefaultsTo0() {
        BaseIngredient ingredient = new BaseIngredient("TestIngredient", "A") {};
        int value = ingredient.getProperty(int.class, "foo");
        assertEquals(0, value);
    }

    @Test
    public void testGetProperty_withType_floatDefaultsTo0() {
        BaseIngredient ingredient = new BaseIngredient("TestIngredient", "A") {};
        float value = ingredient.getProperty(float.class, "foo");
        assertEquals(0.0f, value, 0.0);
    }

    @Test
    public void testGetProperty_withType_booleanDefaultsToFalse() {
        BaseIngredient ingredient = new BaseIngredient("TestIngredient", "A") {};
        boolean value = ingredient.getProperty(boolean.class, "foo");
        assertEquals(false, value);
    }

    @Test
    public void testGetProperty_withType_stringDefaultsToNull() {
        BaseIngredient ingredient = new BaseIngredient("TestIngredient", "A") {};
        String value = ingredient.getProperty(String.class, "foo");
        assertEquals(null, value);
    }

    @Test
    public void testGetProperty_withType_enumDefaultsToFirstDeclaredValue() {
        BaseIngredient ingredient = new BaseIngredient("TestIngredient", "A") {};
        TestEnum value = ingredient.getProperty(TestEnum.class, "foo");
        assertEquals(TestEnum.A, value);
    }

    @Test
    public void testGetProperty_withType_arrayDefaultsToEmptyArray() {
        BaseIngredient ingredient = new BaseIngredient("TestIngredient", "A") {};
        String[] value = ingredient.getProperty(String[].class, "foo");
        assertArrayEquals(new String[0], value);
    }

    @Test
    public void testGetProperty_withType_objectDefaultsToNull() {
        BaseIngredient ingredient = new BaseIngredient("TestIngredient", "A") {};
        SomeClass value = ingredient.getProperty(SomeClass.class, "foo");
        assertEquals(null, value);
    }

    public enum TestEnum {
        A, B, C
    }

    public class SomeClass {
    }
}
