package ca.derekcormier.recipe;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

public class BackendOvenTest {
    private BackendOven oven;

    @Before
    public void before() {
        oven = new BackendOven();
    }

    @Test
    public void testBake_invokesHookForIngredient() {
        TestIngredientHook hook = spy(TestIngredientHook.class);
        oven.registerHook(hook);
        oven.bake("{\"ingredient\":{\"TestIngredient\":{}},\"cake\":{}}");

        verify(hook).bake(any(), any());
    }

    @Test(expected = RuntimeException.class)
    public void testBake_throwsOnMissingIngredientHook() {
        oven.bake("{\"ingredient\":{\"TestIngredient\":{}},\"cake\":{}}");
    }

    @Test
    public void testBake_invokesHookForIngredientInRecipe() {
        TestIngredientHook hook = spy(TestIngredientHook.class);
        oven.registerHook(hook);
        oven.bake("{\"ingredient\":{\"Recipe\":{\"ingredients\":[{\"TestIngredient\":{}}]}},\"cake\":{}}");

        verify(hook).bake(any(), any());
    }

    @Test
    public void testBake_invokesHookForIngredientInNestedRecipe() {
        TestIngredientHook hook = spy(TestIngredientHook.class);
        oven.registerHook(hook);
        oven.bake("{\"ingredient\":{\"Recipe\":{\"ingredients\":[{\"Recipe\":{\"ingredients\":[{\"TestIngredient\":{}}]}}]}},\"cake\":{}}");

        verify(hook).bake(any(), any());
    }

    @Test
    public void testBake_invokesHookMultipleTimesForRepeatedIngredient() {
        TestIngredientHook hook = spy(TestIngredientHook.class);
        oven.registerHook(hook);
        oven.bake("{\"ingredient\":{\"Recipe\":{\"ingredients\":[{\"TestIngredient\":{}},{\"TestIngredient\":{}}]}},\"cake\":{}}");

        verify(hook, times(2)).bake(any(), any());
    }

    @Test
    public void testBake_invokesHooksForMultipleIngredientsInRecipe() {
        TestIngredientHook hook1 = spy(TestIngredientHook.class);
        TestIngredientHook2 hook2 = spy(TestIngredientHook2.class);

        oven.registerHook(hook1);
        oven.registerHook(hook2);
        oven.bake("{\"ingredient\":{\"Recipe\":{\"ingredients\":[{\"TestIngredient\":{}},{\"TestIngredient2\":{}}]}},\"cake\":{}}");

        verify(hook1).bake(any(), any());
        verify(hook2).bake(any(), any());
    }

    public static class TestIngredientHook extends BaseIngredientHook<TestIngredientData> {
        public TestIngredientData ingredient;
        public TestIngredientHook() {
            super("TestIngredient", TestIngredientData.class);
        }

        @Override
        public void bake(TestIngredientData ingredient, Cake cake) {
            this.ingredient = ingredient;
        }
    }

    public static class TestIngredientData extends IngredientSnapshot {
        public TestIngredientData() {
            super("TestIngredient");
        }
    }

    public static class TestIngredientHook2 extends BaseIngredientHook<TestIngredientData2> {
        public TestIngredientHook2() {
            super("TestIngredient2", TestIngredientData2.class);
        }

        @Override
        public void bake(TestIngredientData2 ingredient, Cake cake) {
        }
    }

    public static class TestIngredientData2 extends IngredientSnapshot {
        public TestIngredientData2() {
            super("TestIngredient2");
        }
    }
}
