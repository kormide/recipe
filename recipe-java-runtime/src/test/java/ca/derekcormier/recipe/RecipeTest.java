package ca.derekcormier.recipe;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.List;

public class RecipeTest {

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
        assertEquals("TestIngredient1", segments.get(0).recipe.getIngredients().get(0).getType());
        assertEquals("TestIngredient2", segments.get(0).recipe.getIngredients().get(1).getType());
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
        assertEquals("TestIngredient1", segments.get(0).recipe.getIngredients().get(0).getType());
        assertEquals("TestIngredient2", segments.get(1).recipe.getIngredients().get(0).getType());
    }

    @Test
    public void testSegment_preservesNestedRecipeStructure() {
        Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
        Ingredient ingredient2 = new Ingredient("TestIngredient2", "A") {};
        Ingredient ingredient3 = new Ingredient("TestIngredient3", "A") {};

        Recipe recipe = Recipe.prepare(
            ingredient1,
            ingredient2,
            Recipe.prepare(
                ingredient3
            )
        );

        List<Recipe.Segment> segments = recipe.segment();
        assertEquals(1, segments.size());
        assertEquals("A", segments.get(0).domain);
        assertEquals(3, segments.get(0).recipe.getIngredients().size());
        assertEquals("TestIngredient1", segments.get(0).recipe.getIngredients().get(0).getType());
        assertEquals("TestIngredient2", segments.get(0).recipe.getIngredients().get(1).getType());
        assertEquals("Recipe", segments.get(0).recipe.getIngredients().get(2).getType());
        assertEquals("TestIngredient3", ((Recipe)segments.get(0).recipe.getIngredients().get(2)).getIngredients().get(0).getType());
    }

    @Test
    public void testSegment_preservesRecipeContexts() {
        Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
        Ingredient ingredient2 = new Ingredient("TestIngredient2", "A") {};
        Ingredient ingredient3 = new Ingredient("TestIngredient3", "A") {};

        Recipe recipe = Recipe.prepare(
            ingredient1,
            ingredient2,
            Recipe.context("context",
                ingredient3
            )
        );

        List<Recipe.Segment> segments = recipe.segment();
        assertEquals(1, segments.size());
        assertEquals("A", segments.get(0).domain);
        assertEquals(3, segments.get(0).recipe.getIngredients().size());
        assertEquals("TestIngredient1", segments.get(0).recipe.getIngredients().get(0).getType());
        assertEquals("TestIngredient2", segments.get(0).recipe.getIngredients().get(1).getType());
        assertEquals("Recipe", segments.get(0).recipe.getIngredients().get(2).getType());
        assertEquals("context", ((Recipe)segments.get(0).recipe.getIngredients().get(2)).getContext());
        assertEquals("TestIngredient3", ((Recipe)segments.get(0).recipe.getIngredients().get(2)).getIngredients().get(0).getType());
    }

    @Test
    public void testSegment_segmentsNestedRecipesWithIngredientsInDifferentDomains() {
        Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
        Ingredient ingredient2 = new Ingredient("TestIngredient2", "A") {};
        Ingredient ingredient3 = new Ingredient("TestIngredient3", "B") {};

        Recipe recipe = Recipe.prepare(
            ingredient1,
            ingredient2,
            Recipe.prepare(
                ingredient3
            )
        );

        List<Recipe.Segment> segments = recipe.segment();
        assertEquals(2, segments.size());
        assertEquals(2, segments.get(0).recipe.getIngredients().size());
        assertEquals("A", segments.get(0).domain);
        assertEquals("TestIngredient1", segments.get(0).recipe.getIngredients().get(0).getType());
        assertEquals("TestIngredient2", segments.get(0).recipe.getIngredients().get(1).getType());
        assertEquals(1, segments.get(1).recipe.getIngredients().size());
        assertEquals("B", segments.get(1).domain);
        assertEquals("Recipe", segments.get(1).recipe.getIngredients().get(0).getType());
        assertEquals(1, ((Recipe)segments.get(1).recipe.getIngredients().get(0)).getIngredients().size());
        assertEquals("TestIngredient3", ((Recipe)segments.get(1).recipe.getIngredients().get(0)).getIngredients().get(0).getType());
    }

    @Test
    public void testSegment_nestedRecipesWithDifferentDomains_preservesRecipeContexts() {
        Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
        Ingredient ingredient2 = new Ingredient("TestIngredient2", "B") {};

        Recipe recipe = Recipe.context("foo",
            ingredient1,
            Recipe.prepare(
                Recipe.context("bar",
                    ingredient2
                )
            )
        );

        List<Recipe.Segment> segments = recipe.segment();
        assertEquals(2, segments.size());
        assertEquals("foo", segments.get(0).recipe.getContext());
        assertEquals(1, segments.get(0).recipe.getIngredients().size());
        assertEquals("A", segments.get(0).domain);
        assertEquals("TestIngredient1", segments.get(0).recipe.getIngredients().get(0).getType());
        assertEquals("foo", segments.get(1).recipe.getContext());
        assertEquals(1, segments.get(1).recipe.getIngredients().size());
        assertEquals("B", segments.get(1).domain);
        assertEquals("Recipe", segments.get(1).recipe.getIngredients().get(0).getType());
        assertEquals(null, ((Recipe)segments.get(1).recipe.getIngredients().get(0)).getContext());
        assertEquals(1, ((Recipe)segments.get(1).recipe.getIngredients().get(0)).getIngredients().size());
        assertEquals("Recipe", ((Recipe)segments.get(1).recipe.getIngredients().get(0)).getIngredients().get(0).getType());
        assertEquals("bar", ((Recipe)((Recipe)segments.get(1).recipe.getIngredients().get(0)).getIngredients().get(0)).getContext());
        assertEquals(1, ((Recipe)((Recipe)segments.get(1).recipe.getIngredients().get(0)).getIngredients().get(0)).getIngredients().size());
        assertEquals("TestIngredient2", ((Recipe)((Recipe)segments.get(1).recipe.getIngredients().get(0)).getIngredients().get(0)).getIngredients().get(0).getType());
    }

    @Test
    public void testSegment_twoDomainsInNonContiguousOrderCreatesMoreThanTwoSegments() {
        Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
        Ingredient ingredient2 = new Ingredient("TestIngredient2", "B") {};
        Ingredient ingredient3 = new Ingredient("TestIngredient3", "A") {};

        Recipe recipe = Recipe.prepare(
            ingredient1,
            ingredient2,
            ingredient3
        );

        List<Recipe.Segment> segments = recipe.segment();
        assertEquals(3, segments.size());
        assertEquals("A", segments.get(0).domain);
        assertEquals(1, segments.get(0).recipe.getIngredients().size());
        assertEquals("B", segments.get(1).domain);
        assertEquals(1, segments.get(1).recipe.getIngredients().size());
        assertEquals("A", segments.get(2).domain);
        assertEquals(1, segments.get(2).recipe.getIngredients().size());
    }
}