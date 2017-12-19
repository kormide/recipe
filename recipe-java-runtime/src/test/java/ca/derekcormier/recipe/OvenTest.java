package ca.derekcormier.recipe;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class OvenTest {
    private Oven oven;

    @Before
    public void before() {
        oven = new Oven();
    }

    @Test
    public void testBake_doesNotCallDispatcherForEmptyRecipe() {
        Dispatcher spy = Mockito.spy(Dispatcher.class);
        oven.addDispatcher(spy);

        oven.bake(Recipe.prepare());
        verify(spy, never()).dispatch(anyString(), anyString());
    }

    @Test
    public void testBake_doesNotCallDispatcherForNestedEmptyRecipes() {
        Dispatcher spy = Mockito.spy(Dispatcher.class);
        oven.addDispatcher(spy);

        oven.bake(Recipe.prepare(Recipe.prepare()));
        verify(spy, never()).dispatch(anyString(), anyString());
    }

    @Test
    public void testBake_callsDispatcherForSingleIngredient() {
        Dispatcher spy = Mockito.spy(Dispatcher.class);
        when(spy.dispatch(anyString(), anyString())).thenReturn("{}");
        oven.addDispatcher(spy);

        Ingredient ingredient = new Ingredient("TestIngredient", "TestDomain") {};
        oven.bake(Recipe.prepare(ingredient));
        verify(spy).dispatch("TestDomain", payloadJson("{\"TestIngredient\":{}}"));
    }

    @Test
    public void testBake_noDispatchersDoesNotThrow() {
        Ingredient ingredient = new Ingredient("TestIngredient", "TestDomain") {};
        oven.bake(Recipe.prepare(ingredient));
    }

    @Test
    public void testBake_callsDispatcherPerDomain() {
        Dispatcher spy = Mockito.spy(Dispatcher.class);
        when(spy.dispatch(anyString(), anyString())).thenReturn("{}");
        oven.addDispatcher(spy);

        Ingredient ingredient1 = new Ingredient("TestIngredient1", "DomainA") {};
        Ingredient ingredient2 = new Ingredient("TestIngredient2", "DomainB") {};

        oven.bake(Recipe.prepare(ingredient1, ingredient2));
        verify(spy).dispatch("DomainA", payloadJson("{\"TestIngredient1\":{}}"));
        verify(spy).dispatch("DomainB", payloadJson("{\"TestIngredient2\":{}}"));
        verify(spy, times(2)).dispatch(anyString(), anyString());
    }

    @Test
    public void testBake_callsDispatcherPerDomain_nestedRecipe() {
        Dispatcher spy = Mockito.spy(Dispatcher.class);
        when(spy.dispatch(anyString(), anyString())).thenReturn("{}");
        oven.addDispatcher(spy);

        Ingredient ingredient1 = new Ingredient("TestIngredient1", "DomainA") {};
        Ingredient ingredient2 = new Ingredient("TestIngredient2", "DomainB") {};
        Ingredient ingredient3 = new Ingredient("TestIngredient3", "DomainB") {};

        oven.bake(Recipe.prepare(
            ingredient1,
            Recipe.prepare(
                ingredient2,
                ingredient3
            )
        ));

        verify(spy).dispatch("DomainA", payloadJson("{\"TestIngredient1\":{}}"));
        verify(spy).dispatch("DomainB", payloadJson("{\"Recipe\":{\"ingredients\":[{\"TestIngredient2\":{}},{\"TestIngredient3\":{}}]}}"));
        verify(spy, times(2)).dispatch(anyString(), anyString());
    }

    @Test
    public void testBake_callsDispatcherForInterleavedDomains() {
        Dispatcher spy = Mockito.spy(Dispatcher.class);
        when(spy.dispatch(anyString(), anyString())).thenReturn("{}");
        oven.addDispatcher(spy);

        Ingredient ingredient1 = new Ingredient("TestIngredient1", "DomainA") {};
        Ingredient ingredient2 = new Ingredient("TestIngredient2", "DomainB") {};
        Ingredient ingredient3 = new Ingredient("TestIngredient3", "DomainA") {};

        oven.bake(Recipe.prepare(
            ingredient1,
            ingredient2,
            ingredient3
        ));

        verify(spy).dispatch("DomainA", payloadJson("{\"TestIngredient1\":{}}"));
        verify(spy).dispatch("DomainB", payloadJson("{\"TestIngredient2\":{}}"));
        verify(spy).dispatch("DomainA", payloadJson("{\"TestIngredient3\":{}}"));

        verify(spy, times(3)).dispatch(anyString(), anyString());
    }

    private String payloadJson(String... ingredientJson) {
        return "{\"recipe\":{\"Recipe\":{\"ingredients\":[" + StringUtils.join(ingredientJson, ",") + "]}},\"cake\":{}}";
    }

    private String payloadJsonWithCake(String cake, String... ingredientJson) {
        return "{\"recipe\":{\"Recipe\":{\"ingredients\":[" + StringUtils.join(ingredientJson, ",") + "]}},\"cake\":" + cake + "}";
    }
}
