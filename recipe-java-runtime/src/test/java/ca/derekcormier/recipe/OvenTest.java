package ca.derekcormier.recipe;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.StringUtils;
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

  @Test(expected = RuntimeException.class)
  public void testAddDispatcher_alreadyExistsForDomain() {
    oven.addDispatcher("FooDomain", payload -> "{}");
    oven.addDispatcher("FooDomain", payload -> "{}");
  }

  @Test
  public void testAddDispatcher_callsForIngredientWithSameDomain() {
    Ingredient ingredient = new Ingredient("FooIngredient", "FooDomain") {};

    Dispatcher dispatcher = Mockito.spy(Dispatcher.class);
    when(dispatcher.dispatch(anyString())).thenReturn("{}}");
    oven.addDispatcher("FooDomain", dispatcher);

    oven.bake(Recipe.prepare(ingredient));

    verify(dispatcher).dispatch(anyString());
  }

  @Test
  public void testSetDefaultDispatcher_notCalledIfDomainDispatcherExists() {
    Ingredient ingredient = new Ingredient("FooIngredient", "FooDomain") {};

    Dispatcher defaultDispatcher = Mockito.spy(Dispatcher.class);
    when(defaultDispatcher.dispatch(anyString())).thenReturn("{}}");
    oven.setDefaultDispatcher(defaultDispatcher);
    oven.addDispatcher("FooDomain", payload -> "{}");

    oven.bake(Recipe.prepare(ingredient));

    verify(defaultDispatcher, never()).dispatch(anyString());
  }

  @Test
  public void testSetDefaultDispatcher_calledIfNoDomainDispatchers() {
    Ingredient ingredient = new Ingredient("FooIngredient", "FooDomain") {};

    Dispatcher defaultDispatcher = Mockito.spy(Dispatcher.class);
    when(defaultDispatcher.dispatch(anyString())).thenReturn("{}}");
    oven.setDefaultDispatcher(defaultDispatcher);

    oven.bake(Recipe.prepare(ingredient));

    verify(defaultDispatcher).dispatch(anyString());
  }

  @Test
  public void testSetDefaultDispatcher_calledIfNoMatchingDomainDispatchers() {
    Ingredient ingredient = new Ingredient("FooIngredient", "FooDomain") {};

    Dispatcher defaultDispatcher = Mockito.spy(Dispatcher.class);
    when(defaultDispatcher.dispatch(anyString())).thenReturn("{}}");
    oven.setDefaultDispatcher(defaultDispatcher);
    oven.addDispatcher("BarDomain", payload -> "{}");

    oven.bake(Recipe.prepare(ingredient));

    verify(defaultDispatcher).dispatch(anyString());
  }

  @Test
  public void testBake_doesNotCallDispatcherForEmptyRecipe() {
    Dispatcher spy = setupDispatcherSpy("A");

    oven.bake(Recipe.prepare());
    verify(spy, never()).dispatch(anyString());
  }

  @Test
  public void testBake_doesNotCallDispatcherForNestedEmptyRecipes() {
    Dispatcher spy = setupDispatcherSpy("A");

    oven.bake(Recipe.prepare(Recipe.prepare()));
    verify(spy, never()).dispatch(anyString());
  }

  @Test(expected = RuntimeException.class)
  public void testBake_throwsOnNoDispatcherForIngredient() {
    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    oven.bake(Recipe.prepare(ingredient));
  }

  @Test(expected = RuntimeException.class)
  public void testBake_throwsOnAddTwoDispatchersForSameDomain() {
    oven.addDispatcher("A", (payload) -> "{}");
    oven.addDispatcher("A", (payload) -> "{}");
  }

  @Test
  public void testBake_callsDispatcherForSingleIngredient() {
    Dispatcher spy = setupDispatcherSpy("A");

    Ingredient ingredient = new Ingredient("TestIngredient", "A") {};
    oven.bake(Recipe.prepare(ingredient));
    verify(spy).dispatch(anyString());
  }

  @Test
  public void testBake_callsDispatcherPerDomain() {
    Dispatcher spyA = setupDispatcherSpy("A");
    Dispatcher spyB = setupDispatcherSpy("B");

    Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
    Ingredient ingredient2 = new Ingredient("TestIngredient2", "B") {};

    oven.bake(Recipe.prepare(ingredient1, ingredient2));
    verify(spyA, times(1)).dispatch(anyString());
    verify(spyB, times(1)).dispatch(anyString());
  }

  @Test
  public void testBake_callsDispatcherPerDomain_nestedRecipe() {
    Dispatcher spyA = setupDispatcherSpy("A");
    Dispatcher spyB = setupDispatcherSpy("B");
    Dispatcher spyC = setupDispatcherSpy("C");

    Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
    Ingredient ingredient2 = new Ingredient("TestIngredient2", "B") {};
    Ingredient ingredient3 = new Ingredient("TestIngredient3", "C") {};

    oven.bake(Recipe.prepare(ingredient1, Recipe.prepare(ingredient2, ingredient3)));

    verify(spyA, times(1)).dispatch(anyString());
    verify(spyB, times(1)).dispatch(anyString());
    verify(spyC, times(1)).dispatch(anyString());
  }

  @Test
  public void testBake_callsDispatcherForInterleavedDomains() {
    Dispatcher spyA = setupDispatcherSpy("A");
    Dispatcher spyB = setupDispatcherSpy("B");

    Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
    Ingredient ingredient2 = new Ingredient("TestIngredient2", "B") {};
    Ingredient ingredient3 = new Ingredient("TestIngredient3", "A") {};

    oven.bake(Recipe.prepare(ingredient1, ingredient2, ingredient3));

    InOrder orderVerifier = Mockito.inOrder(spyA, spyB, spyA);
    orderVerifier.verify(spyA).dispatch(anyString());
    orderVerifier.verify(spyB).dispatch(anyString());
    orderVerifier.verify(spyA).dispatch(anyString());
  }

  @Test
  public void testBake_callsDispatcherForContextIngredient() {
    Dispatcher spy = setupDispatcherSpy("A");
    KeyedIngredient keyedIngredient = new KeyedIngredient("KeyedIngredient", "A") {};

    oven.bake(Recipe.context(keyedIngredient));

    verify(spy).dispatch(anyString());
  }

  @Test
  public void testBake_callsDispatcherForContextIngredientAndChildWithDifferentDomain() {
    Dispatcher spyA = setupDispatcherSpy("A");
    Dispatcher spyB = setupDispatcherSpy("B");

    KeyedIngredient keyedIngredient = new KeyedIngredient("KeyedIngredient", "A") {};
    Ingredient ingredient = new Ingredient("TestIngredient", "B") {};

    oven.bake(Recipe.context(keyedIngredient, ingredient));

    InOrder orderVerifier = Mockito.inOrder(spyA, spyB);
    orderVerifier.verify(spyA).dispatch(anyString());
    orderVerifier.verify(spyB).dispatch(anyString());
  }

  @Test
  public void testBake_propagatesCakeToSubsequentDispatches() {
    Dispatcher spyA = setupDispatcherSpy("A", "{\"foo\":\"bar\"}");
    Dispatcher spyB = setupDispatcherSpy("B");

    Ingredient ingredient1 = new Ingredient("TestIngredient1", "A") {};
    Ingredient ingredient2 = new Ingredient("TestIngredient2", "B") {};

    oven.bake(Recipe.prepare(ingredient1, ingredient2));
    verify(spyA).dispatch(payloadJsonWithCake("{}", "{\"TestIngredient1\":{}}"));
    verify(spyB).dispatch(payloadJsonWithCake("{\"foo\":\"bar\"}", "{\"TestIngredient2\":{}}"));
  }

  @Test
  public void testBake_serializesPayload_emptyIngredient() {
    Dispatcher spy = setupDispatcherSpy("A");

    Ingredient ingredient = new Ingredient("EmptyIngredient", "A") {};

    oven.bake(Recipe.prepare(ingredient));
    verify(spy).dispatch(payloadJson("{\"EmptyIngredient\":{}}"));
  }

  @Test
  public void testBake_serializesPayload_ingredientWithRequired() {
    Dispatcher spy = setupDispatcherSpy("A");

    Ingredient ingredient = new Ingredient("IngredientWithRequired", "A") {};
    ingredient.setRequired("required", 5);

    oven.bake(Recipe.prepare(ingredient));
    verify(spy).dispatch(payloadJson("{\"IngredientWithRequired\":{\"required\":5}}"));
  }

  @Test
  public void testBake_serializesPayload_ingredientWithOptional() {
    Dispatcher spy = setupDispatcherSpy("A");

    Ingredient ingredient = new Ingredient("IngredientWithOptional", "A") {};
    ingredient.setOptional("optional", false, true);

    oven.bake(Recipe.prepare(ingredient));
    verify(spy).dispatch(payloadJson("{\"IngredientWithOptional\":{\"optional\":true}}"));
  }

  @Test
  public void testBake_serializesPayload_ingredientWithOptional_setTwiceRetainsLastValue() {
    Dispatcher spy = setupDispatcherSpy("A");

    Ingredient ingredient = new Ingredient("IngredientWithOptional", "A") {};
    ingredient.setOptional("optional", false, true);
    ingredient.setOptional("optional", false, false);

    oven.bake(Recipe.prepare(ingredient));
    verify(spy).dispatch(payloadJson("{\"IngredientWithOptional\":{\"optional\":false}}"));
  }

  @Test
  public void testBake_serializesPayload_ingredientWithRepeatableOptional_recordsMultipleCalls() {
    Dispatcher spy = setupDispatcherSpy("A");

    Ingredient ingredient = new Ingredient("IngredientWithRepeatableOptional", "A") {};
    ingredient.setOptional("optional", true, true);
    ingredient.setOptional("optional", true, false);

    oven.bake(Recipe.prepare(ingredient));
    verify(spy)
        .dispatch(
            payloadJson("{\"IngredientWithRepeatableOptional\":{\"optional\":[true,false]}}"));
  }

  @Test
  public void testBake_serializesPayload_ingredientWithCompoundOptional() {
    Dispatcher spy = setupDispatcherSpy("A");

    Ingredient ingredient = new Ingredient("IngredientWithCompoundOptional", "A") {};
    ingredient.setCompoundOptional("optional", false, "a", 1, "b", "foo");

    oven.bake(Recipe.prepare(ingredient));
    verify(spy)
        .dispatch(
            payloadJson(
                "{\"IngredientWithCompoundOptional\":{\"optional\":{\"a\":1,\"b\":\"foo\"}}}"));
  }

  @Test
  public void testBake_serializesPayload_ingredientWithRepeatableCompoundOptional() {
    Dispatcher spy = setupDispatcherSpy("A");

    Ingredient ingredient = new Ingredient("IngredientWithRepeatableCompoundOptional", "A") {};
    ingredient.setCompoundOptional("optional", true, "a", 1, "b", "foo");
    ingredient.setCompoundOptional("optional", true, "a", -1, "b", "bar");

    oven.bake(Recipe.prepare(ingredient));
    verify(spy)
        .dispatch(
            payloadJson(
                "{\"IngredientWithRepeatableCompoundOptional\":{\"optional\":[{\"a\":1,\"b\":\"foo\"},{\"a\":-1,\"b\":\"bar\"}]}}"));
  }

  @Test
  public void testBake_serializesPayload_recipeWithContext() {
    Dispatcher spy = setupDispatcherSpy("A");

    Ingredient ingredient = new Ingredient("EmptyIngredient", "A") {};

    oven.bake(Recipe.context("foo", ingredient));
    verify(spy)
        .dispatch(
            "{\"recipe\":{\"Recipe\":{\"ingredients\":[{\"EmptyIngredient\":{}}],\"context\":\"foo\"}},\"cake\":{}}");
  }

  @Test
  public void testBake_serializesPayload_recipeWithIngredientContext() {
    Dispatcher spy = setupDispatcherSpy("A");

    KeyedIngredient keyedIngredient = new KeyedIngredient("EmptyKeyedIngredient", "A") {};
    keyedIngredient.setKey("key");
    Ingredient ingredient = new Ingredient("EmptyIngredient", "A") {};

    oven.bake(Recipe.context(keyedIngredient, ingredient));
    verify(spy)
        .dispatch(
            "{\"recipe\":{\"Recipe\":{\"ingredients\":[{\"EmptyKeyedIngredient\":{\"key\":\"key\"}},{\"Recipe\":{\"ingredients\":[{\"EmptyIngredient\":{}}],\"context\":\"key\"}}]}},\"cake\":{}}");
  }

  @Test
  public void testBake_serializesPayload_emptyRecipeWithIngredientContext() {
    Dispatcher spy = setupDispatcherSpy("A");

    KeyedIngredient keyedIngredient = new KeyedIngredient("EmptyKeyedIngredient", "A") {};

    oven.bake(Recipe.context(keyedIngredient));
    verify(spy)
        .dispatch(
            "{\"recipe\":{\"Recipe\":{\"ingredients\":[{\"EmptyKeyedIngredient\":{}}]}},\"cake\":{}}");
  }

  @Test
  public void testBake_serializesPayload_nestedEmptyRecipeWithIngredientContextInOtherDomain() {
    Dispatcher spyA = setupDispatcherSpy("A");
    Dispatcher spyB = setupDispatcherSpy("B");

    Ingredient ingredient = new Ingredient("EmptyIngredient", "A") {};
    KeyedIngredient keyedIngredient = new KeyedIngredient("EmptyKeyedIngredient", "B") {};

    oven.bake(Recipe.prepare(ingredient, Recipe.context(keyedIngredient)));

    verify(spyA)
        .dispatch(
            "{\"recipe\":{\"Recipe\":{\"ingredients\":[{\"EmptyIngredient\":{}}]}},\"cake\":{}}");
    verify(spyB)
        .dispatch(
            "{\"recipe\":{\"Recipe\":{\"ingredients\":[{\"EmptyKeyedIngredient\":{}}]}},\"cake\":{}}");
  }

  private String payloadJson(String... ingredientJson) {
    return "{\"recipe\":{\"Recipe\":{\"ingredients\":["
        + StringUtils.join(ingredientJson, ",")
        + "]}},\"cake\":{}}";
  }

  private String payloadJsonWithCake(String cake, String... ingredientJson) {
    return "{\"recipe\":{\"Recipe\":{\"ingredients\":["
        + StringUtils.join(ingredientJson, ",")
        + "]}},\"cake\":"
        + cake
        + "}";
  }

  private Dispatcher setupDispatcherSpy(String domain) {
    return setupDispatcherSpy(domain, "{}");
  }

  private Dispatcher setupDispatcherSpy(String domain, String returnedCakeJson) {
    Dispatcher spy = Mockito.spy(Dispatcher.class);
    when(spy.dispatch(anyString())).thenReturn(returnedCakeJson);
    oven.addDispatcher(domain, spy);
    return spy;
  }
}
