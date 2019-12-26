package ca.derekcormier.recipe;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IngredientTest {
  @Test
  public void testSetProperty_setsProperty() {
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    ingredient.setProperty("foo", "bar");
    assertEquals("bar", ingredient.getProperty("foo"));
  }

  @Test
  public void testHasProperty_propertyExists() {
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    ingredient.setProperty("foo", "bar");
    assertTrue(ingredient.hasProperty("foo"));
  }

  @Test
  public void testHasProperty_propertyDoesNotExist() {
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    assertFalse(ingredient.hasProperty("foo"));
  }

  @Test
  public void testGetProperty_getsExistingValue() {
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    ingredient.setProperty("foo", TestEnum.B);
    TestEnum value = ingredient.getProperty("foo");
    assertEquals(TestEnum.B, value);
  }

  @Test
  public void testGetProperty_returnsNullOnNonExistentKey() {
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    assertNull(ingredient.getProperty("foo"));
  }

  @Test
  public void testGetProperty_withType_getsExistingValue() {
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    ingredient.setProperty("foo", 7);
    int value = ingredient.getProperty(int.class, "foo");
    assertEquals(7, value);
  }

  @Test
  public void testGetProperty_withType_intDefaultsTo0() {
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    int value = ingredient.getProperty(int.class, "foo");
    assertEquals(0, value);
  }

  @Test
  public void testGetProperty_withType_floatDefaultsTo0() {
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    float value = ingredient.getProperty(float.class, "foo");
    assertEquals(0.0f, value, 0.0);
  }

  @Test
  public void testGetProperty_withType_booleanDefaultsToFalse() {
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    boolean value = ingredient.getProperty(boolean.class, "foo");
    assertEquals(false, value);
  }

  @Test
  public void testGetProperty_withType_stringDefaultsToNull() {
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    String value = ingredient.getProperty(String.class, "foo");
    assertEquals(null, value);
  }

  @Test
  public void testGetProperty_withType_enumDefaultsToFirstDeclaredValue() {
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    TestEnum value = ingredient.getProperty(TestEnum.class, "foo");
    assertEquals(TestEnum.A, value);
  }

  @Test
  public void testGetProperty_withType_arrayDefaultsToEmptyArray() {
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    String[] value = ingredient.getProperty(String[].class, "foo");
    assertArrayEquals(new String[0], value);
  }

  @Test
  public void testGetProperty_withType_objectDefaultsToNull() {
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    SomeClass value = ingredient.getProperty(SomeClass.class, "foo");
    assertEquals(null, value);
  }

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

  public enum TestEnum {
    A,
    B,
    C
  }

  public class SomeClass {}
}
