package ca.derekcormier.recipe;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.Test;

public class RecipeTest {

  @Test
  public void testPrepare_flattensRecipeWithNoContext() {
    Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
    Ingredient ingredient2 = new Ingredient("TestIngredient2", "A") {};

    Recipe recipe = Recipe.prepare(ingredient1, Recipe.prepare(ingredient2));

    assertEquals(2, recipe.getIngredients().size());
    assertEquals(ingredient1, recipe.getIngredients().get(0));
    assertEquals(ingredient2, recipe.getIngredients().get(1));
  }

  @Test
  public void testPrepare_preservesStructureOfContextfulRecipes() {
    Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
    Ingredient ingredient2 = new Ingredient("TestIngredient2", "A") {};

    Recipe recipe = Recipe.prepare(ingredient1, Recipe.context("key1", ingredient2));

    assertEquals(2, recipe.getIngredients().size());
    assertEquals(ingredient1, recipe.getIngredients().get(0));
    assertEquals("key1", ((Recipe) recipe.getIngredients().get(1)).getContext());
    assertEquals(1, ((Recipe) recipe.getIngredients().get(1)).getIngredients().size());
    assertEquals(ingredient2, ((Recipe) recipe.getIngredients().get(1)).getIngredients().get(0));
  }

  @Test
  public void testPrepare_flattensRecipeWhenContextIsNull() {
    Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
    Ingredient ingredient2 = new Ingredient("TestIngredient2", "A") {};

    Recipe recipe = Recipe.prepare(ingredient1, Recipe.context((String) null, ingredient2));

    assertEquals(2, recipe.getIngredients().size());
    assertEquals(ingredient1, recipe.getIngredients().get(0));
    assertEquals(ingredient2, recipe.getIngredients().get(1));
  }

  @Test
  public void testContext_createsRecipeWithContext() {
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    Recipe recipe = Recipe.context("key", ingredient);

    assertEquals("key", recipe.getContext());
    assertEquals(1, recipe.getIngredients().size());
    assertEquals(ingredient, recipe.getIngredients().get(0));
  }

  @Test
  public void testContext_flattensInnerRecipeWithNoContext() {
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    Recipe recipe = Recipe.context("key", Recipe.prepare(ingredient));

    assertEquals("key", recipe.getContext());
    assertEquals(1, recipe.getIngredients().size());
    assertEquals(ingredient, recipe.getIngredients().get(0));
  }

  @Test
  public void testContext_preservesStructureOfInnerContextfulRecipe() {
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    Recipe recipe = Recipe.context("key", Recipe.context("key2", ingredient));

    assertEquals("key", recipe.getContext());
    assertEquals(1, recipe.getIngredients().size());
    assertEquals("key2", ((Recipe) recipe.getIngredients().get(0)).getContext());
    assertEquals(1, ((Recipe) recipe.getIngredients().get(0)).getIngredients().size());
    assertEquals(ingredient, ((Recipe) recipe.getIngredients().get(0)).getIngredients().get(0));
  }

  @Test
  public void
      testContext_contextIngredient_createsRecipeWithContextIngredientAndSubRecipeWithContext() {
    KeyedIngredient contextIngredient = new KeyedIngredient("TestIngredient", "A") {};
    contextIngredient.setKey("key");
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};

    Recipe recipe = Recipe.context(contextIngredient, ingredient);

    assertEquals(null, recipe.getContext());
    assertEquals(2, recipe.getIngredients().size());
    assertEquals(contextIngredient, recipe.getIngredients().get(0));
    assertEquals("key", ((Recipe) recipe.getIngredients().get(1)).getContext());
    assertEquals(1, ((Recipe) recipe.getIngredients().get(1)).getIngredients().size());
    assertEquals(ingredient, ((Recipe) recipe.getIngredients().get(1)).getIngredients().get(0));
  }

  @Test
  public void testContext_contextIngredient_nullKeyFlattensInnerRecipe() {
    KeyedIngredient contextIngredient = new KeyedIngredient("TestIngredient", "A") {};
    contextIngredient.setKey(null);
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};

    Recipe recipe = Recipe.context(contextIngredient, ingredient);

    assertEquals(null, recipe.getContext());
    assertEquals(2, recipe.getIngredients().size());
    assertEquals(contextIngredient, recipe.getIngredients().get(0));
    assertEquals(ingredient, recipe.getIngredients().get(1));
  }

  @Test
  public void testContext_contextIngredient_flattensInnerRecipeWithNoContext() {
    KeyedIngredient contextIngredient = new KeyedIngredient("TestIngredient", "A") {};
    contextIngredient.setKey("key");
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};

    Recipe recipe = Recipe.context(contextIngredient, Recipe.prepare(ingredient));

    assertEquals(null, recipe.getContext());
    assertEquals(2, recipe.getIngredients().size());
    assertEquals(contextIngredient, recipe.getIngredients().get(0));
    assertEquals(1, ((Recipe) recipe.getIngredients().get(1)).getIngredients().size());
    assertEquals(ingredient, ((Recipe) recipe.getIngredients().get(1)).getIngredients().get(0));
  }

  @Test
  public void testContext_contextIngredient_preservesStructureOfInnerContextfulRecipe() {
    KeyedIngredient contextIngredient = new KeyedIngredient("TestIngredient", "A") {};
    contextIngredient.setKey("key");
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};

    Recipe recipe = Recipe.context(contextIngredient, Recipe.context("key2", ingredient));

    assertEquals(null, recipe.getContext());
    assertEquals(2, recipe.getIngredients().size());
    assertEquals(contextIngredient, recipe.getIngredients().get(0));
    assertEquals("key", ((Recipe) recipe.getIngredients().get(1)).getContext());
    assertEquals(1, ((Recipe) recipe.getIngredients().get(1)).getIngredients().size());
    assertEquals(
        "key2",
        ((Recipe) ((Recipe) recipe.getIngredients().get(1)).getIngredients().get(0)).getContext());
    assertEquals(
        1,
        ((Recipe) ((Recipe) recipe.getIngredients().get(1)).getIngredients().get(0))
            .getIngredients()
            .size());
    assertEquals(
        ingredient,
        ((Recipe) ((Recipe) recipe.getIngredients().get(1)).getIngredients().get(0))
            .getIngredients()
            .get(0));
  }

  @Test
  public void testSegment_emptyRecipeReturnsZeroSegments() {
    Recipe recipe = Recipe.prepare();
    List<Recipe.Segment> segments = recipe.segment();

    assertEquals(0, segments.size());
  }

  @Test
  public void testSegment_createsOneSegmentForSingleIngredient() {
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    Recipe recipe = Recipe.prepare(ingredient);

    List<Recipe.Segment> segments = recipe.segment();
    assertEquals(1, segments.size());
    assertEquals("A", segments.get(0).domain);
    assertEquals(1, segments.get(0).recipe.getIngredients().size());
  }

  @Test
  public void testSegment_createsOneSegmentForTwoIngredientsInSameDomain() {
    Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
    Ingredient ingredient2 = new Ingredient("TestIngredient2", "A") {};

    Recipe recipe = Recipe.prepare(ingredient1, ingredient2);

    List<Recipe.Segment> segments = recipe.segment();
    assertEquals(1, segments.size());
    assertEquals("A", segments.get(0).domain);
    assertEquals(2, segments.get(0).recipe.getIngredients().size());
    assertEquals(
        "TestIngredient1", segments.get(0).recipe.getIngredients().get(0).getIngredientType());
    assertEquals(
        "TestIngredient2", segments.get(0).recipe.getIngredients().get(1).getIngredientType());
  }

  @Test
  public void testSegment_createsTwoSegmentsForTwoIngredientsInDifferentDomain() {
    Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
    Ingredient ingredient2 = new Ingredient("TestIngredient2", "B") {};

    Recipe recipe = Recipe.prepare(ingredient1, ingredient2);

    List<Recipe.Segment> segments = recipe.segment();
    assertEquals(2, segments.size());
    assertEquals(1, segments.get(0).recipe.getIngredients().size());
    assertEquals("A", segments.get(0).domain);
    assertEquals(1, segments.get(1).recipe.getIngredients().size());
    assertEquals("B", segments.get(1).domain);
    assertEquals(
        "TestIngredient1", segments.get(0).recipe.getIngredients().get(0).getIngredientType());
    assertEquals(
        "TestIngredient2", segments.get(1).recipe.getIngredients().get(0).getIngredientType());
  }

  @Test
  public void testSegment_preservesRecipeContexts() {
    Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
    Ingredient ingredient2 = new Ingredient("TestIngredient2", "A") {};
    Ingredient ingredient3 = new Ingredient("TestIngredient3", "A") {};

    Recipe recipe =
        Recipe.prepare(ingredient1, ingredient2, Recipe.context("context", ingredient3));

    List<Recipe.Segment> segments = recipe.segment();
    assertEquals(1, segments.size());
    assertEquals("A", segments.get(0).domain);
    assertEquals(3, segments.get(0).recipe.getIngredients().size());
    assertEquals(
        "TestIngredient1", segments.get(0).recipe.getIngredients().get(0).getIngredientType());
    assertEquals(
        "TestIngredient2", segments.get(0).recipe.getIngredients().get(1).getIngredientType());
    assertEquals("Recipe", segments.get(0).recipe.getIngredients().get(2).getIngredientType());
    assertEquals("context", ((Recipe) segments.get(0).recipe.getIngredients().get(2)).getContext());
    assertEquals(
        "TestIngredient3",
        ((Recipe) segments.get(0).recipe.getIngredients().get(2))
            .getIngredients()
            .get(0)
            .getIngredientType());
  }

  @Test
  public void testSegment_twoDomainsInNonContiguousOrderCreatesMoreThanTwoSegments() {
    Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
    Ingredient ingredient2 = new Ingredient("TestIngredient2", "B") {};
    Ingredient ingredient3 = new Ingredient("TestIngredient3", "A") {};

    Recipe recipe = Recipe.prepare(ingredient1, ingredient2, ingredient3);

    List<Recipe.Segment> segments = recipe.segment();
    assertEquals(3, segments.size());
    assertEquals("A", segments.get(0).domain);
    assertEquals(1, segments.get(0).recipe.getIngredients().size());
    assertEquals("B", segments.get(1).domain);
    assertEquals(1, segments.get(1).recipe.getIngredients().size());
    assertEquals("A", segments.get(2).domain);
    assertEquals(1, segments.get(2).recipe.getIngredients().size());
  }

  @Test
  public void testSegment_createsSegmentForContextfulRecipeWithNoIngredients() {
    KeyedIngredient keyedIngredient = new KeyedIngredient("KeyedIngredient", "A") {};

    Recipe recipe = Recipe.context(keyedIngredient);

    List<Recipe.Segment> segments = recipe.segment();
    assertEquals(1, segments.size());
    assertEquals("A", segments.get(0).domain);
    assertEquals(1, segments.get(0).recipe.getIngredients().size());
    assertEquals(keyedIngredient, segments.get(0).recipe.getIngredients().get(0));
  }

  @Test
  public void testSegment_contextIngredientAndSubIngredientInDifferentDomain() {
    KeyedIngredient keyedIngredient = new KeyedIngredient("KeyedIngredient", "A") {};
    keyedIngredient.setKey("key");
    Ingredient ingredient = new Ingredient("TestIngredient", "B") {};

    Recipe recipe = Recipe.context(keyedIngredient, ingredient);

    List<Recipe.Segment> segments = recipe.segment();
    assertEquals(2, segments.size());
    assertEquals("A", segments.get(0).domain);
    assertEquals(1, segments.get(0).recipe.getIngredients().size());
    assertEquals(
        "KeyedIngredient", segments.get(0).recipe.getIngredients().get(0).getIngredientType());
    assertEquals("B", segments.get(1).domain);
    assertEquals(1, segments.get(1).recipe.getIngredients().size());
    assertEquals("Recipe", segments.get(1).recipe.getIngredients().get(0).getIngredientType());
    assertEquals(
        1, ((Recipe) segments.get(1).recipe.getIngredients().get(0)).getIngredients().size());
    assertEquals(
        "TestIngredient",
        ((Recipe) segments.get(1).recipe.getIngredients().get(0))
            .getIngredients()
            .get(0)
            .getIngredientType());
  }

  @Test
  public void testSegment_multipleNestedContexts() {
    Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
    Ingredient ingredient2 = new Ingredient("TestIngredient2", "A") {};

    Recipe recipe = Recipe.context("key1", ingredient1, Recipe.context("key2", ingredient2));

    List<Recipe.Segment> segments = recipe.segment();
    assertEquals(1, segments.size());
    assertEquals("A", segments.get(0).domain);
    assertEquals("key1", segments.get(0).recipe.getContext());
    assertEquals(2, segments.get(0).recipe.getIngredients().size());
    assertEquals(
        "TestIngredient1", segments.get(0).recipe.getIngredients().get(0).getIngredientType());
    assertEquals("Recipe", segments.get(0).recipe.getIngredients().get(1).getIngredientType());
    assertEquals("key2", ((Recipe) segments.get(0).recipe.getIngredients().get(1)).getContext());
    assertEquals(
        1, ((Recipe) segments.get(0).recipe.getIngredients().get(1)).getIngredients().size());
    assertEquals(
        "TestIngredient2",
        ((Recipe) segments.get(0).recipe.getIngredients().get(1))
            .getIngredients()
            .get(0)
            .getIngredientType());
  }

  @Test
  public void testSegment_nestedContextIngredientRecipeWithDifferentDomain() {
    Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
    Ingredient ingredient2 = new Ingredient("TestIngredient2", "A") {};
    KeyedIngredient keyedIngredient = new KeyedIngredient("KeyedIngredient", "B") {};
    keyedIngredient.setKey("key2");

    Recipe recipe =
        Recipe.context("key1", ingredient1, Recipe.context(keyedIngredient, ingredient2));

    List<Recipe.Segment> segments = recipe.segment();

    assertEquals(3, segments.size());
    assertEquals("A", segments.get(0).domain);
    assertEquals("key1", segments.get(0).recipe.getContext());
    assertEquals(1, segments.get(0).recipe.getIngredients().size());
    assertEquals(
        "TestIngredient1", segments.get(0).recipe.getIngredients().get(0).getIngredientType());

    assertEquals("B", segments.get(1).domain);
    assertEquals("key1", segments.get(1).recipe.getContext());
    assertEquals(1, segments.get(1).recipe.getIngredients().size());
    assertEquals(
        "KeyedIngredient", segments.get(1).recipe.getIngredients().get(0).getIngredientType());

    assertEquals("A", segments.get(2).domain);
    assertEquals("key1", segments.get(2).recipe.getContext());
    assertEquals(1, segments.get(2).recipe.getIngredients().size());
    assertEquals("Recipe", segments.get(2).recipe.getIngredients().get(0).getIngredientType());
    assertEquals("key2", ((Recipe) segments.get(2).recipe.getIngredients().get(0)).getContext());
    assertEquals(
        1, ((Recipe) segments.get(2).recipe.getIngredients().get(0)).getIngredients().size());
    assertEquals(
        "TestIngredient2",
        ((Recipe) segments.get(2).recipe.getIngredients().get(0))
            .getIngredients()
            .get(0)
            .getIngredientType());
  }
}
