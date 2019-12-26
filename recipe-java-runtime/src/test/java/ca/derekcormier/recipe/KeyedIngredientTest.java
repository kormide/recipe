package ca.derekcormier.recipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class KeyedIngredientTest {
  @Test
  public void testKeyed_setsKey() {
    KeyedIngredient ingredient = new KeyedIngredient("KeyedIngredient", "A") {};

    assertFalse(ingredient.hasKey());

    ingredient.keyed("foo");

    assertEquals("foo", ingredient.getKey());
    assertTrue(ingredient.hasKey());
  }
}
