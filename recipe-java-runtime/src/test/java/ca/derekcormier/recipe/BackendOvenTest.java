package ca.derekcormier.recipe;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class BackendOvenTest {
    private BackendOven oven;

    @Before
    public void before() {
        oven = new BackendOven();
    }

    @Test(expected = RuntimeException.class)
    public void testBake_throwsOnMissingIngredientHook() {
        oven.bake("{\"recipe\":{\"Recipe\":{\"ingredients\":[{\"TestIngredient\":{}}]}}}");
    }

    @Test
    public void testBake_bakesSingleIngredient() {
        EmptyIngredientHook hook = spy(EmptyIngredientHook.class);
        oven.registerHook(hook);
        oven.bake("{\"recipe\":{\"Recipe\":{\"ingredients\":[{\"EmptyIngredient\":{}}]}}}");

        verify(hook).bake(any(), any());
    }

    @Test
    public void testBake_bakesIngredientInNestedRecipe() {
        EmptyIngredientHook hook = spy(EmptyIngredientHook.class);
        oven.registerHook(hook);
        oven.bake("{\"recipe\":{\"Recipe\":{\"ingredients\":[{\"EmptyIngredient\":{}}]}}}");

        verify(hook).bake(any(), any());
    }

    @Test
    public void testBake_bakesRepeatedIngredient() {
        EmptyIngredientHook hook = spy(EmptyIngredientHook.class);
        oven.registerHook(hook);
        oven.bake("{\"recipe\":{\"Recipe\":{\"ingredients\":[{\"EmptyIngredient\":{}},{\"EmptyIngredient\":{}}]}}}");

        verify(hook, times(2)).bake(any(), any());
    }

    @Test
    public void testBake_basedMultipleIngredients() {
        EmptyIngredientHook hook1 = spy(EmptyIngredientHook.class);
        IngredientWithRequiredHook hook2 = spy(IngredientWithRequiredHook.class);

        oven.registerHook(hook1);
        oven.registerHook(hook2);
        oven.bake("{\"recipe\":{\"Recipe\":{\"ingredients\":[{\"EmptyIngredient\":{}},{\"IngredientWithRequired\":{}}]}}}");

        verify(hook1).bake(any(), any());
        verify(hook2).bake(any(), any());
    }


    @Test
    public void testBake_bakesIngredientInRecipeWithContext() {
        EmptyIngredientHook hook = spy(EmptyIngredientHook.class);

        oven.registerHook(hook);

        Mockito.doAnswer(invocation -> {
            Cake cake = invocation.getArgument(1);
            assertEquals("foo", cake.getNamespace());
            return null;
        }).when(hook).bake(any(), any());

        oven.bake("{\"recipe\":{\"Recipe\":{\"context\":\"foo\",\"ingredients\":[{\"EmptyIngredient\":{}}]}},\"cake\":{}}");

        verify(hook).bake(any(), any());
    }

    public static class EmptyIngredientHook extends BaseIngredientHook<EmptyIngredientData> {
        public EmptyIngredientHook() {
            super("EmptyIngredient", EmptyIngredientData.class);
        }

        @Override
        public void bake(EmptyIngredientData ingredient, Cake cake) {
        }
    }

    public static class EmptyIngredientData extends Ingredient {
        public EmptyIngredientData() {
            super("EmptyIngredient");
        }
    }

    public static class IngredientWithRequiredHook extends BaseIngredientHook<IngredientWithRequiredData> {
        public IngredientWithRequiredHook() {
            super("IngredientWithRequired", IngredientWithRequiredData.class);
        }

        @Override
        public void bake(IngredientWithRequiredData ingredient, Cake cake) {
        }
    }

    public static class IngredientWithRequiredData extends Ingredient {
        public IngredientWithRequiredData() {
            super("IngredientWithRequired");
        }
    }
}
