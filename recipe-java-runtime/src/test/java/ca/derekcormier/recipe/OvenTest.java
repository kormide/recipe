package ca.derekcormier.recipe;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        verify(spy).dispatch("TestDomain", "{\"ingredient\":{\"TestIngredient\":{}},\"cake\":{}}");
    }

    @Test
    public void testBake_noDispatchersDoesNotThrow() {
        Ingredient ingredient = new Ingredient("TestIngredient", "TestDomain") {};
        oven.bake(Recipe.prepare(ingredient));
    }

    @Test
    public void testBake_callsDispatcherForMultipleIngredients() {
        Dispatcher spy = Mockito.spy(Dispatcher.class);
        when(spy.dispatch(anyString(), anyString())).thenReturn("{}");
        oven.addDispatcher(spy);

        Ingredient ingredient1 = new Ingredient("TestIngredient1", "DomainA") {};
        Ingredient ingredient2 = new Ingredient("TestIngredient2", "DomainB") {};

        oven.bake(Recipe.prepare(ingredient1, ingredient2));
        verify(spy).dispatch("DomainA", "{\"ingredient\":{\"TestIngredient1\":{}},\"cake\":{}}");
        verify(spy).dispatch("DomainB", "{\"ingredient\":{\"TestIngredient2\":{}},\"cake\":{}}");
        verify(spy, times(2)).dispatch(anyString(), anyString());
    }

    @Test
    public void testBake_callsDispatcherForIngredientsInNestedRecipe() {
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

        verify(spy).dispatch("DomainA", "{\"ingredient\":{\"TestIngredient1\":{}},\"cake\":{}}");
        verify(spy).dispatch("DomainB", "{\"ingredient\":{\"TestIngredient2\":{}},\"cake\":{}}");
        verify(spy).dispatch("DomainB", "{\"ingredient\":{\"TestIngredient3\":{}},\"cake\":{}}");
        verify(spy, times(3)).dispatch(anyString(), anyString());
    }

    @Test
    public void testBake_callsMultipleDispatchersForSameIngredient() {
        Dispatcher spy1 = Mockito.spy(Dispatcher.class);
        Dispatcher spy2 = Mockito.spy(Dispatcher.class);
        when(spy1.dispatch(anyString(), anyString())).thenReturn("{}");
        when(spy2.dispatch(anyString(), anyString())).thenReturn("{}");

        oven.addDispatcher(spy1);
        oven.addDispatcher(spy2);

        Ingredient ingredient = new Ingredient("TestIngredient", "DomainA") {};

        oven.bake(Recipe.prepare(ingredient));

        verify(spy1).dispatch("DomainA", "{\"ingredient\":{\"TestIngredient\":{}},\"cake\":{}}");
        verify(spy2).dispatch("DomainA", "{\"ingredient\":{\"TestIngredient\":{}},\"cake\":{}}");
    }
}
