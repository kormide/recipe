package ca.derekcormier.recipe;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.function.BiConsumer;

public class OvenTest {
    private Oven oven;

    @Before
    public void before() {
        oven = new Oven();
    }

    @Test
    public void testBake_doesNotCallDispatcherForEmptyRecipe() {
        BiConsumer<String,String> spy = Mockito.spy(BiConsumer.class);
        oven.addDispatcher(spy);

        oven.bake(Recipe.prepare());
        verify(spy, never()).accept(anyString(), anyString());
    }

    @Test
    public void testBake_doesNotCallDispatcherForNestedEmptyRecipes() {
        BiConsumer<String,String> spy = Mockito.spy(BiConsumer.class);
        oven.addDispatcher(spy);

        oven.bake(Recipe.prepare(Recipe.prepare()));
        verify(spy, never()).accept(anyString(), anyString());
    }

    @Test
    public void testBake_callsDispatcherForSingleIngredient() {
        BiConsumer<String,String> spy = Mockito.spy(BiConsumer.class);
        oven.addDispatcher(spy);

        Ingredient ingredient = new Ingredient("TestIngredient", "TestDomain") {};
        oven.bake(Recipe.prepare(ingredient));
        verify(spy).accept("TestDomain", ingredient.toJson());
    }

    @Test
    public void testBake_noDispatchersDoesNotThrow() {
        Ingredient ingredient = new Ingredient("TestIngredient", "TestDomain") {};
        oven.bake(Recipe.prepare(ingredient));
    }

    @Test
    public void testBake_callsDispatcherForMultipleIngredients() {
        BiConsumer<String,String> spy = Mockito.spy(BiConsumer.class);
        oven.addDispatcher(spy);

        Ingredient ingredient1 = new Ingredient("TestIngredient1", "DomainA") {};
        Ingredient ingredient2 = new Ingredient("TestIngredient2", "DomainB") {};

        oven.bake(Recipe.prepare(ingredient1, ingredient2));
        verify(spy).accept("DomainA", ingredient1.toJson());
        verify(spy).accept("DomainB", ingredient2.toJson());
        verify(spy, times(2)).accept(anyString(), anyString());
    }

    @Test
    public void testBake_callsDispatcherForIngredientsInNestedRecipe() {
        BiConsumer<String,String> spy = Mockito.spy(BiConsumer.class);
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

        verify(spy).accept("DomainA", ingredient1.toJson());
        verify(spy).accept("DomainB", ingredient2.toJson());
        verify(spy).accept("DomainB", ingredient3.toJson());

        verify(spy, times(3)).accept(anyString(), anyString());
    }

    @Test
    public void testBake_callsMultipleDispatchers() {
        BiConsumer<String,String> spy1 = Mockito.spy(BiConsumer.class);
        BiConsumer<String,String> spy2 = Mockito.spy(BiConsumer.class);

        oven.addDispatcher(spy1);
        oven.addDispatcher(spy2);


        Ingredient ingredient = new Ingredient("TestIngredient", "DomainA") {};

        oven.bake(Recipe.prepare(ingredient));

        verify(spy1).accept("DomainA", ingredient.toJson());
        verify(spy2).accept("DomainA", ingredient.toJson());
    }
}
