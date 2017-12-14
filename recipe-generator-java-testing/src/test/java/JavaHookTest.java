import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import ca.derekcormier.recipe.BaseIngredientHook;
import ca.derekcormier.recipe.BackendOven;
import ca.derekcormier.recipe.Cake;

public class JavaHookTest {
    @Test
    public void testGeneration_generatesIngredientHook_emptyIngredient() {
        new AbstractEmptyIngredientHook() {
            @Override
            public void bake(EmptyIngredientData data, Cake cake) {}
        };
    }

    @Test
    public void testGeneration_generatesIngredientHook_multipleParamsIngredient() {
        new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {}
        };
    }

    @Test
    public void testGeneration_generatedHookHasCorrectSuperclass() {
        assertEquals(BaseIngredientHook.class, AbstractAllParamsIngredientHook.class.getSuperclass());
    }

    @Test
    public void testGeneration_emptyIngredientData() {
        new EmptyIngredient();
    }

    @Test
    public void testGeneration_ingredientWithRequiredData() throws NoSuchMethodException {
        new IngredientWithRequiredData();

        IngredientWithRequiredData.class.getMethod("getRequired");
        assertEquals(String.class, IngredientWithRequiredData.class.getMethod("getRequired").getReturnType());
    }

    @Test
    public void testGeneration_ingredientWithRequiredAndOptionalData() throws NoSuchMethodException {
        new IngredientWithRequiredAndOptionalData();

        IngredientWithRequiredAndOptionalData.class.getMethod("getRequired");
        IngredientWithRequiredAndOptionalData.class.getMethod("getOptional");
        IngredientWithRequiredAndOptionalData.class.getMethod("hasOptional");
        assertEquals(String.class, IngredientWithRequiredAndOptionalData.class.getMethod("getRequired").getReturnType());
        assertEquals(boolean.class, IngredientWithRequiredAndOptionalData.class.getMethod("getOptional").getReturnType());
        assertEquals(boolean.class, IngredientWithRequiredAndOptionalData.class.getMethod("hasOptional").getReturnType());
    }

    @Test
    public void testGeneration_ingredientWithRepeatableOptionalData() throws NoSuchMethodException {
        new IngredientWithRepeatableOptionalData();

        IngredientWithRepeatableOptionalData.class.getMethod("getOptional");
        assertEquals(boolean[].class, IngredientWithRepeatableOptionalData.class.getMethod("getOptional").getReturnType());
    }

    @Test
    public void testGeneration_ingredientWithRepeatableVarargOptionalData() throws NoSuchMethodException {
        new IngredientWithRepeatableVarargOptionalData();

        IngredientWithRepeatableVarargOptionalData.class.getMethod("getOptional");
        assertEquals(int[][].class, IngredientWithRepeatableVarargOptionalData.class.getMethod("getOptional").getReturnType());
    }

    @Test
    public void testGeneration_ingredientWithCompoundOptionalData() throws NoSuchMethodException, NoSuchFieldException {
        new IngredientWithCompoundOptionalData();

        IngredientWithCompoundOptionalData.class.getMethod("getCompoundOptional");
        assertEquals(IngredientWithCompoundOptionalData.CompoundOptionalParams.class, IngredientWithCompoundOptionalData.class.getMethod("getCompoundOptional").getReturnType());
        IngredientWithCompoundOptionalData.CompoundOptionalParams.class.getField("param1");
        IngredientWithCompoundOptionalData.CompoundOptionalParams.class.getField("param2");
        assertEquals(int.class, IngredientWithCompoundOptionalData.CompoundOptionalParams.class.getField("param1").getType());
        assertEquals(boolean.class, IngredientWithCompoundOptionalData.CompoundOptionalParams.class.getField("param2").getType());
    }

    @Test
    public void testGeneration_ingredientWithRepeatableCompoundOptionalData() throws NoSuchMethodException, NoSuchFieldException {
        new IngredientWithRepeatableCompoundOptionalData();

        IngredientWithRepeatableCompoundOptionalData.class.getMethod("getCompoundOptional");
        assertEquals(IngredientWithRepeatableCompoundOptionalData.CompoundOptionalParams[].class, IngredientWithRepeatableCompoundOptionalData.class.getMethod("getCompoundOptional").getReturnType());

        IngredientWithRepeatableCompoundOptionalData.CompoundOptionalParams.class.getField("param1");
        IngredientWithRepeatableCompoundOptionalData.CompoundOptionalParams.class.getField("param2");
        assertEquals(int.class, IngredientWithRepeatableCompoundOptionalData.CompoundOptionalParams.class.getField("param1").getType());
        assertEquals(boolean.class, IngredientWithRepeatableCompoundOptionalData.CompoundOptionalParams.class.getField("param2").getType());
    }

    @Test
    public void testGeneration_ingredientDataWithIntParam() throws NoSuchMethodException {
        new AllParamsIngredientData();
        AllParamsIngredientData.class.getMethod("getIntArg");
        assertEquals(int.class, AllParamsIngredientData.class.getMethod("getIntArg").getReturnType());
    }

    @Test
    public void testGeneration_ingredientDataWithStringParam() throws NoSuchMethodException {
        new AllParamsIngredientData();
        AllParamsIngredientData.class.getMethod("getStringArg");
        assertEquals(String.class, AllParamsIngredientData.class.getMethod("getStringArg").getReturnType());
    }

    @Test
    public void testGeneration_ingredientDataWithBooleanParam() throws NoSuchMethodException {
        new AllParamsIngredientData();
        AllParamsIngredientData.class.getMethod("getBooleanArg");
        assertEquals(boolean.class, AllParamsIngredientData.class.getMethod("getBooleanArg").getReturnType());
    }

    @Test
    public void testGeneration_ingredientDataWithFlagParam() throws NoSuchMethodException {
        new AllParamsIngredientData();
        AllParamsIngredientData.class.getMethod("getFlagArg");
        assertEquals(boolean.class, AllParamsIngredientData.class.getMethod("getFlagArg").getReturnType());
    }

    @Test
    public void testGeneration_ingredientDataWithEnumParam() throws NoSuchMethodException {
        new AllParamsIngredientData();
        AllParamsIngredientData.class.getMethod("getEnumArg");
        assertEquals(TestEnum.class, AllParamsIngredientData.class.getMethod("getEnumArg").getReturnType());
    }

    @Test
    public void testGeneration_ingredientWithPrimitiveArrayParam() throws NoSuchMethodException {
        new AllParamsIngredientData();
        AllParamsIngredientData.class.getMethod("getStringArrayArg");
        assertEquals(String[].class, AllParamsIngredientData.class.getMethod("getStringArrayArg").getReturnType());
    }

    @Test
    public void testGeneration_ingredientWithEnumArrayParam() throws NoSuchMethodException {
        new AllParamsIngredientData();
        AllParamsIngredientData.class.getMethod("getEnumArrayArg");
        assertEquals(TestEnum[].class, AllParamsIngredientData.class.getMethod("getEnumArrayArg").getReturnType());
    }

    @Test
    public void testGeneration_ingredientWithVarargParam() throws NoSuchMethodException {
        new AllParamsIngredientData();
        AllParamsIngredientData.class.getMethod("getVarargArg");
        assertEquals(String[].class, AllParamsIngredientData.class.getMethod("getVarargArg").getReturnType());
    }

    @Test
    public void testGeneration_ingredientWithVarargArrayParam() throws NoSuchMethodException {
        new AllParamsIngredientData();
        AllParamsIngredientData.class.getMethod("getVarargArrayArg");
        assertEquals(int[][].class, AllParamsIngredientData.class.getMethod("getVarargArrayArg").getReturnType());
    }

    @Test
    public void testGeneration_backendOvenInvokesHook() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"Recipe\":{\"ingredients\":[{\"AllParamsIngredient\":{\"intArg\":5}}]}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredienWithRequired() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractIngredientWithRequiredHook() {
            @Override
            public void bake(IngredientWithRequiredData data, Cake cake) {
                assertEquals("foobar", data.getRequired());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"IngredientWithRequired\":{\"required\":\"foobar\"}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithRequiredAndOptional() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractIngredientWithRequiredAndOptionalHook() {
            @Override
            public void bake(IngredientWithRequiredAndOptionalData data, Cake cake) {
                assertEquals("foobar", data.getRequired());
                assertEquals(true, data.getOptional());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"IngredientWithRequiredAndOptional\":{\"required\":\"foobar\",\"optional\":true}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithOptional_valuePresent() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractIngredientWithOptionalHook() {
            @Override
            public void bake(IngredientWithOptionalData data, Cake cake) {
                assertTrue(data.hasOptional());
                assertEquals(true, data.getOptional());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"IngredientWithOptional\":{\"optional\":true}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredienWithOptional_valueMissing() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractIngredientWithOptionalHook() {
            @Override
            public void bake(IngredientWithOptionalData data, Cake cake) {
                assertFalse(data.hasOptional());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"IngredientWithOptional\":{}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithRepeatableOptional_singleValuePresent() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractIngredientWithRepeatableOptionalHook() {
            @Override
            public void bake(IngredientWithRepeatableOptionalData data, Cake cake) {
                assertTrue(data.hasOptional());
                assertArrayEquals(new boolean[]{true}, data.getOptional());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"IngredientWithRepeatableOptional\":{\"optional\":[true]}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithRepeatableOptional_multipleValuesPresent() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractIngredientWithRepeatableOptionalHook() {
            @Override
            public void bake(IngredientWithRepeatableOptionalData data, Cake cake) {
                assertTrue(data.hasOptional());
                assertArrayEquals(new boolean[]{true, false, true}, data.getOptional());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"IngredientWithRepeatableOptional\":{\"optional\":[true, false, true]}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithRepeatableVarargOptional() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractIngredientWithRepeatableVarargOptionalHook() {
            @Override
            public void bake(IngredientWithRepeatableVarargOptionalData data, Cake cake) {
                assertTrue(data.hasOptional());
                assertArrayEquals(new int[][]{new int[]{1, 2}, new int[]{3, 4}}, data.getOptional());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"IngredientWithRepeatableVarargOptional\":{\"optional\":[[1,2],[3,4]]}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithRepeatableOptional_valueMissing() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractIngredientWithRepeatableOptionalHook() {
            @Override
            public void bake(IngredientWithRepeatableOptionalData data, Cake cake) {
                assertFalse(data.hasOptional());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"IngredientWithRepeatableOptional\":{}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithCompoundOptional_valuePresent() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractIngredientWithCompoundOptionalHook() {
            @Override
            public void bake(IngredientWithCompoundOptionalData data, Cake cake) {
                assertTrue(data.hasCompoundOptional());
                assertEquals(5, data.getCompoundOptional().param1);
                assertEquals(false, data.getCompoundOptional().param2);
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"IngredientWithCompoundOptional\":{\"compoundOptional\":{\"param1\":5,\"param2\":false}}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithCompoundOptional_valueMissing() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractIngredientWithCompoundOptionalHook() {
            @Override
            public void bake(IngredientWithCompoundOptionalData data, Cake cake) {
                assertFalse(data.hasCompoundOptional());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"IngredientWithCompoundOptional\":{}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithRepeatableCompoundOptional_singleValuePresent() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractIngredientWithRepeatableCompoundOptionalHook() {
            @Override
            public void bake(IngredientWithRepeatableCompoundOptionalData data, Cake cake) {
                assertTrue(data.hasCompoundOptional());
                assertEquals(5, data.getCompoundOptional()[0].param1);
                assertEquals(false, data.getCompoundOptional()[0].param2);
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"IngredientWithRepeatableCompoundOptional\":{\"compoundOptional\":[{\"param1\":5,\"param2\":false}]}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithRepeatableCompoundOptional_multipleValuesPresent() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractIngredientWithRepeatableCompoundOptionalHook() {
            @Override
            public void bake(IngredientWithRepeatableCompoundOptionalData data, Cake cake) {
                assertTrue(data.hasCompoundOptional());
                assertEquals(5, data.getCompoundOptional()[0].param1);
                assertEquals(false, data.getCompoundOptional()[0].param2);
                assertEquals(-1, data.getCompoundOptional()[1].param1);
                assertEquals(true, data.getCompoundOptional()[1].param2);
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"IngredientWithRepeatableCompoundOptional\":{\"compoundOptional\":[{\"param1\":5,\"param2\":false},{\"param1\":-1,\"param2\":true}]}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithRepeatableCompoundOptional_valueMissing() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractIngredientWithRepeatableCompoundOptionalHook() {
            @Override
            public void bake(IngredientWithRepeatableCompoundOptionalData data, Cake cake) {
                assertFalse(data.hasCompoundOptional());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"IngredientWithRepeatableCompoundOptional\":{}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithStringParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals("foobar", data.getStringArg());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"AllParamsIngredient\":{\"stringArg\":\"foobar\"}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithStringParam_null() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(null, data.getStringArg());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"AllParamsIngredient\":{\"stringArg\":null}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithIntParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(100, data.getIntArg());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"AllParamsIngredient\":{\"intArg\":100}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithBooleanParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(true, data.getBooleanArg());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"AllParamsIngredient\":{\"booleanArg\":true}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithFlagParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(false, data.getFlagArg());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"AllParamsIngredient\":{\"flagArg\":false}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithEnumParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(TestEnum.B, data.getEnumArg());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"AllParamsIngredient\":{\"enumArg\":\"B\"}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithPrimitiveArrayParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(new String[]{"foo", "bar"}, data.getStringArrayArg());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"AllParamsIngredient\":{\"stringArrayArg\":[\"foo\",\"bar\"]}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithEnumArrayParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(new TestEnum[]{TestEnum.B, TestEnum.C}, data.getEnumArrayArg());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"AllParamsIngredient\":{\"enumArrayArg\":[\"B\",\"C\"]}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithVarargParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(new String[]{"foo", "bar"}, data.getVarargArg());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"AllParamsIngredient\":{\"varargArg\":[\"foo\",\"bar\"]}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testGeneration_deserializesIngredientWithVarargArrayParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(new int[][]{new int[]{1, 2}, new int[]{3,4}}, data.getVarargArrayArg());
                spy.run();
            }
        });

        oven.bake("{\"ingredient\":{\"AllParamsIngredient\":{\"varargArrayArg\":[[1,2],[3,4]]}},\"cake\":{}}");
        verify(spy).run();
    }
}
