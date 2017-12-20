package ca.derekcormier.recipe;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

public class OvenTest {
    private Oven oven;

    @Before
    public void before() {
        oven = new Oven();
    }

    @Test
    public void testBake_doesNotCallDispatcherForEmptyRecipe() {
        Dispatcher spy = setupDispatcherSpy();

        oven.bake(Recipe.prepare());
        verify(spy, never()).dispatch(anyString(), anyString());
    }

    @Test
    public void testBake_doesNotCallDispatcherForNestedEmptyRecipes() {
        Dispatcher spy = setupDispatcherSpy();

        oven.bake(Recipe.prepare(Recipe.prepare()));
        verify(spy, never()).dispatch(anyString(), anyString());
    }

    @Test
    public void testBake_callsDispatcherForSingleIngredient() {
        Dispatcher spy = setupDispatcherSpy();

        Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
        oven.bake(Recipe.prepare(ingredient));
        verify(spy).dispatch(anyString(), anyString());
    }

    @Test
    public void testBake_noDispatchersDoesNotThrow() {
        Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
        oven.bake(Recipe.prepare(ingredient));
    }

    @Test
    public void testBake_callsDispatcherPerDomain() {
        Dispatcher spy = setupDispatcherSpy();

        Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
        Ingredient ingredient2 = new Ingredient("TestIngredient2", "B") {};

        oven.bake(Recipe.prepare(ingredient1, ingredient2));
        verify(spy).dispatch(eq("A"), anyString());
        verify(spy).dispatch(eq("B"), anyString());
        verify(spy, times(2)).dispatch(anyString(), anyString());
    }

    @Test
    public void testBake_callsDispatcherPerDomain_nestedRecipe() {
        Dispatcher spy = setupDispatcherSpy();

        Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
        Ingredient ingredient2 = new Ingredient("TestIngredient2", "B") {};
        Ingredient ingredient3 = new Ingredient("TestIngredient3", "C") {};

        oven.bake(Recipe.prepare(
            ingredient1,
            Recipe.prepare(
                ingredient2,
                ingredient3
            )
        ));

        verify(spy).dispatch(eq("A"), anyString());
        verify(spy).dispatch(eq("B"), anyString());
        verify(spy).dispatch(eq("C"), anyString());
        verify(spy, times(3)).dispatch(anyString(), anyString());
    }

    @Test
    public void testBake_callsDispatcherForInterleavedDomains() {
        Dispatcher spy = setupDispatcherSpy();

        Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
        Ingredient ingredient2 = new Ingredient("TestIngredient2", "B") {};
        Ingredient ingredient3 = new Ingredient("TestIngredient3", "A") {};

        oven.bake(Recipe.prepare(
            ingredient1,
            ingredient2,
            ingredient3
        ));

        InOrder orderVerifier = Mockito.inOrder(spy);
        orderVerifier.verify(spy).dispatch(eq("A"), anyString());
        orderVerifier.verify(spy).dispatch(eq("B"), anyString());
        orderVerifier.verify(spy).dispatch(eq("A"), anyString());
    }

    @Test
    public void testBake_callsDispatcherForContextIngredient() {
        Dispatcher spy = setupDispatcherSpy();
        KeyedIngredient keyedIngredient = new KeyedIngredient("KeyedIngredient", "A") {};

        oven.bake(Recipe.context(keyedIngredient));

        verify(spy).dispatch(eq("A"), anyString());
    }

    @Test
    public void testBake_callsDispatcherForContextIngredientAndChildWithDifferentDomain() {
        Dispatcher spy = setupDispatcherSpy();
        KeyedIngredient keyedIngredient = new KeyedIngredient("KeyedIngredient", "A") {};
        Ingredient ingredient = new Ingredient("TestIngredient", "B") {};


        oven.bake(Recipe.context(keyedIngredient,
            ingredient
        ));

        verify(spy).dispatch(eq("A"), anyString());
        verify(spy).dispatch(eq("B"), anyString());
    }

    @Test
    public void testBake_propagatesCakeToSubsequentDispatches() {
        Dispatcher spy = setupDispatcherSpy("{\"foo\":\"bar\"}");

        Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
        Ingredient ingredient2 = new Ingredient("TestIngredient2", "B") {};

        oven.bake(Recipe.prepare(ingredient1, ingredient2));
        verify(spy).dispatch("A", payloadJsonWithCake("{}", "{\"TestIngredient1\":{}}"));
        verify(spy).dispatch("B", payloadJsonWithCake("{\"foo\":\"bar\"}", "{\"TestIngredient2\":{}}"));
    }

    @Test
    public void testBake_serializesPayload_emptyIngredient() {
        Dispatcher spy = setupDispatcherSpy();

        Ingredient ingredient = new Ingredient("EmptyIngredient", "A") {};

        oven.bake(Recipe.prepare(ingredient));
        verify(spy).dispatch("A", payloadJson("{\"EmptyIngredient\":{}}"));
    }

    @Test
    public void testBake_serializesPayload_ingredientWithRequired() {
        Dispatcher spy = setupDispatcherSpy();

        Ingredient ingredient = new Ingredient("IngredientWithRequired", "A") {};
        ingredient.setRequired("required", 5);

        oven.bake(Recipe.prepare(ingredient));
        verify(spy).dispatch("A", payloadJson("{\"IngredientWithRequired\":{\"required\":5}}"));
    }

    @Test
    public void testBake_serializesPayload_ingredientWithOptional() {
        Dispatcher spy = setupDispatcherSpy();

        Ingredient ingredient = new Ingredient("IngredientWithOptional", "A") {};
        ingredient.setOptional("optional", false, true);

        oven.bake(Recipe.prepare(ingredient));
        verify(spy).dispatch("A", payloadJson("{\"IngredientWithOptional\":{\"optional\":true}}"));
    }

    @Test
    public void testBake_serializesPayload_ingredientWithOptional_setTwiceRetainsLastValue() {
        Dispatcher spy = setupDispatcherSpy();

        Ingredient ingredient = new Ingredient("IngredientWithOptional", "A") {};
        ingredient.setOptional("optional", false, true);
        ingredient.setOptional("optional", false, false);

        oven.bake(Recipe.prepare(ingredient));
        verify(spy).dispatch("A", payloadJson("{\"IngredientWithOptional\":{\"optional\":false}}"));
    }

    @Test
    public void testBake_serializesPayload_ingredientWithRepeatableOptional_recordsMultipleCalls() {
        Dispatcher spy = setupDispatcherSpy();

        Ingredient ingredient = new Ingredient("IngredientWithRepeatableOptional", "A") {};
        ingredient.setOptional("optional", true, true);
        ingredient.setOptional("optional", true, false);

        oven.bake(Recipe.prepare(ingredient));
        verify(spy).dispatch("A", payloadJson("{\"IngredientWithRepeatableOptional\":{\"optional\":[true,false]}}"));
    }

    @Test
    public void testBake_serializesPayload_ingredientWithCompoundOptional() {
        Dispatcher spy = setupDispatcherSpy();

        Ingredient ingredient = new Ingredient("IngredientWithCompoundOptional", "A") {};
        ingredient.setCompoundOptional("optional", false, "a", 1, "b", "foo");

        oven.bake(Recipe.prepare(ingredient));
        verify(spy).dispatch("A", payloadJson("{\"IngredientWithCompoundOptional\":{\"optional\":{\"a\":1,\"b\":\"foo\"}}}"));
    }

    @Test
    public void testBake_serializesPayload_ingredientWithRepeatableCompoundOptional() {
        Dispatcher spy = setupDispatcherSpy();

        Ingredient ingredient = new Ingredient("IngredientWithRepeatableCompoundOptional", "A") {};
        ingredient.setCompoundOptional("optional", true, "a", 1, "b", "foo");
        ingredient.setCompoundOptional("optional", true, "a", -1, "b", "bar");

        oven.bake(Recipe.prepare(ingredient));
        verify(spy).dispatch("A", payloadJson("{\"IngredientWithRepeatableCompoundOptional\":{\"optional\":[{\"a\":1,\"b\":\"foo\"},{\"a\":-1,\"b\":\"bar\"}]}}"));
    }

    @Test
    public void testBake_serializesPayload_recipeWithContext() {
        Dispatcher spy = setupDispatcherSpy();

        Ingredient ingredient = new Ingredient("EmptyIngredient", "A") {};

        oven.bake(Recipe.context("foo", ingredient));
        verify(spy).dispatch("A", "{\"recipe\":{\"Recipe\":{\"context\":\"foo\",\"ingredients\":[{\"EmptyIngredient\":{}}]}},\"cake\":{}}");
    }

    @Test
    public void testBake_serializesPayload_recipeWithIngredientContext() {
        Dispatcher spy = setupDispatcherSpy();

        KeyedIngredient keyedIngredient = new KeyedIngredient("EmptyKeyedIngredient", "A") {};
        Ingredient ingredient = new Ingredient("EmptyIngredient", "A") {};

        oven.bake(Recipe.context(keyedIngredient, ingredient));
        verify(spy).dispatch("A", "{\"recipe\":{\"Recipe\":{\"contextIngredient\":{\"EmptyKeyedIngredient\":{}},\"ingredients\":[{\"EmptyIngredient\":{}}]}},\"cake\":{}}");
    }

    @Test
    public void testBake_serializesPayload_emptyRecipeWithIngredientContext() {
        Dispatcher spy = setupDispatcherSpy();

        KeyedIngredient keyedIngredient = new KeyedIngredient("EmptyKeyedIngredient", "A") {};

        oven.bake(Recipe.context(keyedIngredient));
        verify(spy).dispatch("A", "{\"recipe\":{\"Recipe\":{\"contextIngredient\":{\"EmptyKeyedIngredient\":{}},\"ingredients\":[]}},\"cake\":{}}");
    }

    @Test
    public void testBake_serializesPayload_nestedEmptyRecipeWithIngredientContextInOtherDomain() {
        Dispatcher spy = setupDispatcherSpy();

        Ingredient ingredient = new Ingredient("EmptyIngredient", "A") {};
        KeyedIngredient keyedIngredient = new KeyedIngredient("EmptyKeyedIngredient", "B") {};

        oven.bake(Recipe.prepare(
            ingredient,
            Recipe.context(keyedIngredient)
        ));

        verify(spy).dispatch("A", "{\"recipe\":{\"Recipe\":{\"ingredients\":[{\"EmptyIngredient\":{}}]}},\"cake\":{}}");
        verify(spy).dispatch("B", "{\"recipe\":{\"Recipe\":{\"ingredients\":[{\"Recipe\":{\"contextIngredient\":{\"EmptyKeyedIngredient\":{}},\"ingredients\":[]}}]}},\"cake\":{}}");
    }

    private String payloadJson(String... ingredientJson) {
        return "{\"recipe\":{\"Recipe\":{\"ingredients\":[" + StringUtils.join(ingredientJson, ",") + "]}},\"cake\":{}}";
    }

    private String payloadJsonWithCake(String cake, String... ingredientJson) {
        return "{\"recipe\":{\"Recipe\":{\"ingredients\":[" + StringUtils.join(ingredientJson, ",") + "]}},\"cake\":" + cake + "}";
    }

    private Dispatcher setupDispatcherSpy() {
        return setupDispatcherSpy("{}");
    }

    private Dispatcher setupDispatcherSpy(String returnedCakeJson) {
        Dispatcher spy = Mockito.spy(Dispatcher.class);
        when(spy.dispatch(anyString(), anyString())).thenReturn(returnedCakeJson);
        oven.addDispatcher(spy);
        return spy;
    }
}
