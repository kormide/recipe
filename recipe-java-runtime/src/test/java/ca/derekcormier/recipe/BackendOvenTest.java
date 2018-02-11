package ca.derekcormier.recipe;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.commons.lang.StringUtils;
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
        oven.bake(payloadJson("{\"TestIngredient\":{}}"));
    }

    @Test
    public void testBake_bakesSingleIngredient() {
        EmptyIngredientHook hook = spy(EmptyIngredientHook.class);
        oven.registerHook(hook);
        oven.bake(payloadJson("{\"EmptyIngredient\":{}}"));

        verify(hook).bake(any(), any());
    }

    @Test
    public void testBake_bakesIngredientInNestedRecipe() {
        EmptyIngredientHook hook = spy(EmptyIngredientHook.class);
        oven.registerHook(hook);
        oven.bake(payloadJson("{\"Recipe\":{\"ingredients\":[{\"EmptyIngredient\":{}}]}}"));

        verify(hook).bake(any(), any());
    }

    @Test
    public void testBake_bakesRepeatedIngredient() {
        EmptyIngredientHook hook = spy(EmptyIngredientHook.class);
        oven.registerHook(hook);
        oven.bake(payloadJson("{\"EmptyIngredient\":{}}", "{\"EmptyIngredient\":{}}"));

        verify(hook, times(2)).bake(any(), any());
    }

    @Test
    public void testBake_basedMultipleIngredients() {
        EmptyIngredientHook hook1 = spy(EmptyIngredientHook.class);
        IngredientWithRequiredHook hook2 = spy(IngredientWithRequiredHook.class);

        oven.registerHook(hook1);
        oven.registerHook(hook2);
        oven.bake(payloadJson("{\"EmptyIngredient\":{}}", "{\"IngredientWithRequired\":{}}"));

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

    @Test
    public void testBake_bakesContextIngredient() {
        KeyedIngredientHook hook = spy(KeyedIngredientHook.class);

        oven.registerHook(hook);
        oven.bake("{\"recipe\":{\"Recipe\":{\"contextIngredient\":{\"KeyedIngredient\":{}},\"ingredients\":[]}},\"cake\":{}}");

        verify(hook).bake(any(), any());
    }

    @Test
    public void testBake_bakesContextIngredientAndChildIngredients() {
        KeyedIngredientHook hook = spy(KeyedIngredientHook.class);
        EmptyIngredientHook hook2 = spy(EmptyIngredientHook.class);

        oven.registerHook(hook);
        oven.registerHook(hook2);
        oven.bake("{\"recipe\":{\"Recipe\":{\"contextIngredient\":{\"KeyedIngredient\":{}},\"ingredients\":[{\"EmptyIngredient\":{}}]}},\"cake\":{}}");

        verify(hook).bake(any(), any());
        verify(hook2).bake(any(), any());
    }

    @Test
    public void testBake_bakesChildrenOfContextIngredientInContext_keySetInDto() {
        KeyedIngredientHook hook = spy(KeyedIngredientHook.class);
        EmptyIngredientHook hook2 = spy(EmptyIngredientHook.class);

        oven.registerHook(hook);
        oven.registerHook(hook2);

        Mockito.doAnswer(invocation -> {
            Cake cake = invocation.getArgument(1);
            assertEquals("foo", cake.getNamespace());
            return null;
        }).when(hook2).bake(any(), any());

        oven.bake("{\"recipe\":{\"Recipe\":{\"contextIngredient\":{\"KeyedIngredient\":{\"key\":\"foo\"}},\"ingredients\":[{\"EmptyIngredient\":{}}]}},\"cake\":{}}");

        verify(hook).bake(any(), any());
        verify(hook2).bake(any(), any());
    }

    @Test
    public void testBake_bakesChildrenOfContextIngredientInContext_keySetInBake() {
        KeyedIngredientHook hook = spy(KeyedIngredientHook.class);
        EmptyIngredientHook hook2 = spy(EmptyIngredientHook.class);

        oven.registerHook(hook);
        oven.registerHook(hook2);

        Mockito.doAnswer(invocation -> {
            KeyedIngredientData data = invocation.getArgument(0);
            data.setKey("foo");
            return null;
        }).when(hook).bake(any(), any());

        Mockito.doAnswer(invocation -> {
            Cake cake = invocation.getArgument(1);
            assertEquals("foo", cake.getNamespace());
            return null;
        }).when(hook2).bake(any(), any());

        oven.bake("{\"recipe\":{\"Recipe\":{\"contextIngredient\":{\"KeyedIngredient\":{}},\"ingredients\":[{\"EmptyIngredient\":{}}]}},\"cake\":{}}");

        verify(hook).bake(any(), any());
        verify(hook2).bake(any(), any());
    }

    @Test
    public void testBake_bakesChildrenOfContextIngredientInContext_noKeyResultsInNoContext() {
        KeyedIngredientHook hook = spy(KeyedIngredientHook.class);
        EmptyIngredientHook hook2 = spy(EmptyIngredientHook.class);

        oven.registerHook(hook);
        oven.registerHook(hook2);

        Mockito.doAnswer(invocation -> {
            Cake cake = invocation.getArgument(1);
            assertEquals("", cake.getNamespace());
            return null;
        }).when(hook2).bake(any(), any());

        oven.bake("{\"recipe\":{\"Recipe\":{\"contextIngredient\":{\"KeyedIngredient\":{}},\"ingredients\":[{\"EmptyIngredient\":{}}]}},\"cake\":{}}");

        verify(hook).bake(any(), any());
        verify(hook2).bake(any(), any());
    }

    @Test
    public void testBake_bakesChildrenOfContextIngredientInContext_keyChangedToNullInBakeResultsInNoContext() {
        KeyedIngredientHook hook = spy(KeyedIngredientHook.class);
        EmptyIngredientHook hook2 = spy(EmptyIngredientHook.class);

        oven.registerHook(hook);
        oven.registerHook(hook2);

        Mockito.doAnswer(invocation -> {
            KeyedIngredientData data = invocation.getArgument(0);
            data.setKey(null);
            return null;
        }).when(hook).bake(any(), any());

        Mockito.doAnswer(invocation -> {
            Cake cake = invocation.getArgument(1);
            assertEquals("", cake.getNamespace());
            return null;
        }).when(hook2).bake(any(), any());

        oven.bake("{\"recipe\":{\"Recipe\":{\"contextIngredient\":{\"KeyedIngredient\":{\"key\":\"foo\"}},\"ingredients\":[{\"EmptyIngredient\":{}}]}},\"cake\":{}}");

        verify(hook).bake(any(), any());
        verify(hook2).bake(any(), any());
    }

    @Test
    public void testAddSerializer_cakeDeserializesValues() {
        EmptyIngredientHook hook = spy(EmptyIngredientHook.class);
        oven.registerHook(hook);
        oven.addCakeValueSerializer(new CakeValueSerializer<String,String>(String.class) {
            @Override
            public String serialize(String value) {
                return "_" + value + "_";
            }
            @Override
            public String deserialize(Class<? extends String> clazz, String value) {
                return value.substring(1, value.length() - 1);
            }
        });

        Mockito.doAnswer(invocation -> {
            Cake cake = invocation.getArgument(1);
            assertEquals("bar", cake.get(String.class, "foo"));
            return null;
        }).when(hook).bake(any(), any());

        oven.bake("{\"recipe\":{\"Recipe\":{\"ingredients\":[{\"EmptyIngredient\":{}}]}},\"cake\":{\"foo\":\"_bar_\"}}");
    }

    @Test
    public void testAddSerializer_cakeSerializesValues() {
        EmptyIngredientHook hook = spy(EmptyIngredientHook.class);
        oven.registerHook(hook);
        oven.addCakeValueSerializer(new CakeValueSerializer<String,String>(String.class) {
            @Override
            public String serialize(String value) {
                return "_" + value + "_";
            }
            @Override
            public String deserialize(Class<? extends String> clazz, String value) {
                return value.substring(1, value.length() - 1);
            }
        });

        Mockito.doAnswer(invocation -> {
            Cake cake = invocation.getArgument(1);
            cake.publish("foo", "bar");
            assertEquals("_bar_", cake.get("foo"));
            return null;
        }).when(hook).bake(any(), any());

        oven.bake("{\"recipe\":{\"Recipe\":{\"ingredients\":[{\"EmptyIngredient\":{}}]}},\"cake\":{}}");
    }

    public static class EmptyIngredientHook extends BaseIngredientHook<EmptyIngredientData> {
        public EmptyIngredientHook() {
            super("EmptyIngredient", EmptyIngredientData.class);
        }

        @Override
        public void bake(EmptyIngredientData ingredient, Cake cake) {
        }
    }

    public static class EmptyIngredientData extends IngredientSnapshot {
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

    public static class IngredientWithRequiredData extends IngredientSnapshot {
        public IngredientWithRequiredData() {
            super("IngredientWithRequired");
        }
    }

    public static class KeyedIngredientHook extends BaseIngredientHook<KeyedIngredientData> {
        public KeyedIngredientHook() {
            super("KeyedIngredient", KeyedIngredientData.class);
        }

        @Override
        public void bake(KeyedIngredientData ingredient, Cake cake) {
        }
    }

    public static class KeyedIngredientData extends KeyedIngredientSnapshot {
        public KeyedIngredientData() {
            super("KeyedIngredient");
        }
    }

    private String payloadJson(String... ingredientJson) {
        return "{\"recipe\":{\"Recipe\":{\"ingredients\":[" + StringUtils.join(ingredientJson, ",") + "]}}}";
    }
}
