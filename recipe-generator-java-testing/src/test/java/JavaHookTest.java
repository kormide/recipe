import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import ca.derekcormier.recipe.BaseIngredientHook;
import ca.derekcormier.recipe.BackendOven;
import ca.derekcormier.recipe.Cake;
import testdomain.hooks.AbstractAllParamsIngredientHook;
import testdomain.hooks.AbstractEmptyIngredientHook;
import testdomain.hooks.AbstractIngredientWithCompoundOptionalHook;
import testdomain.hooks.AbstractIngredientWithOptionalHook;
import testdomain.hooks.AbstractIngredientWithRepeatableCompoundOptionalHook;
import testdomain.hooks.AbstractIngredientWithRepeatableOptionalHook;
import testdomain.hooks.AbstractIngredientWithRepeatableVarargOptionalHook;
import testdomain.hooks.AbstractIngredientWithRequiredAndOptionalHook;
import testdomain.hooks.AbstractIngredientWithRequiredHook;
import testdomain.hooks.AbstractKeyedTestIngredientHook;
import testdomain.hooks.AllParamsIngredientData;
import testdomain.hooks.EmptyIngredientData;
import testdomain.hooks.IngredientWithCompoundOptionalData;
import testdomain.hooks.IngredientWithOptionalData;
import testdomain.hooks.IngredientWithRepeatableCompoundOptionalData;
import testdomain.hooks.IngredientWithRepeatableOptionalData;
import testdomain.hooks.IngredientWithRepeatableVarargOptionalData;
import testdomain.hooks.IngredientWithRequiredAndOptionalData;
import testdomain.hooks.IngredientWithRequiredData;
import testdomain.hooks.KeyedTestIngredientData;
import testdomain.hooks.TestEnum;

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
        new EmptyIngredientData();
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
    public void testGeneration_ingredientDataWithFloatParam() throws NoSuchMethodException {
        new AllParamsIngredientData();
        AllParamsIngredientData.class.getMethod("getFloatArg");
        assertEquals(float.class, AllParamsIngredientData.class.getMethod("getFloatArg").getReturnType());
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
    public void testBake_deserialization_emptyIngredient() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractEmptyIngredientHook() {
            @Override
            public void bake(EmptyIngredientData data, Cake cake) {
                spy.run();
            }
        });

        oven.bake(payloadJson("{\"Recipe\":{\"ingredients\":[{\"EmptyIngredient\":{}}]}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithRequired() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractIngredientWithRequiredHook() {
            @Override
            public void bake(IngredientWithRequiredData data, Cake cake) {
                assertEquals("foobar", data.getRequired());
                spy.run();
            }
        });

        oven.bake(payloadJson("{\"IngredientWithRequired\":{\"required\":\"foobar\"}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithRequiredAndOptional() {
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

        oven.bake(payloadJson("{\"IngredientWithRequiredAndOptional\":{\"required\":\"foobar\",\"optional\":true}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithOptionalValuePresent() {
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

        oven.bake(payloadJson("{\"IngredientWithOptional\":{\"optional\":true}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithOptionalValueMissing() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractIngredientWithOptionalHook() {
            @Override
            public void bake(IngredientWithOptionalData data, Cake cake) {
                assertFalse(data.hasOptional());
                spy.run();
            }
        });

        oven.bake(payloadJson("{\"IngredientWithOptional\":{}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithRepeatableOptional_singleValuePresent() {
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

        oven.bake(payloadJson("{\"IngredientWithRepeatableOptional\":{\"optional\":[true]}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithRepeatableOptional_multipleValuesPresent() {
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

        oven.bake(payloadJson("{\"IngredientWithRepeatableOptional\":{\"optional\":[true, false, true]}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithRepeatableVarargOptional() {
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

        oven.bake(payloadJson("{\"IngredientWithRepeatableVarargOptional\":{\"optional\":[[1,2],[3,4]]}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithRepeatableOptional_valueMissing() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractIngredientWithRepeatableOptionalHook() {
            @Override
            public void bake(IngredientWithRepeatableOptionalData data, Cake cake) {
                assertFalse(data.hasOptional());
                spy.run();
            }
        });

        oven.bake(payloadJson("{\"IngredientWithRepeatableOptional\":{}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithCompoundOptional_valuePresent() {
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

        oven.bake(payloadJson("{\"IngredientWithCompoundOptional\":{\"compoundOptional\":{\"param1\":5,\"param2\":false}}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithCompoundOptional_valueMissing() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractIngredientWithCompoundOptionalHook() {
            @Override
            public void bake(IngredientWithCompoundOptionalData data, Cake cake) {
                assertFalse(data.hasCompoundOptional());
                spy.run();
            }
        });

        oven.bake(payloadJson("{\"IngredientWithCompoundOptional\":{}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithRepeatableCompoundOptional_singleValuePresent() {
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

        oven.bake(payloadJson("{\"IngredientWithRepeatableCompoundOptional\":{\"compoundOptional\":[{\"param1\":5,\"param2\":false}]}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithRepeatableCompoundOptional_multipleValuesPresent() {
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

        oven.bake(payloadJson("{\"IngredientWithRepeatableCompoundOptional\":{\"compoundOptional\":[{\"param1\":5,\"param2\":false},{\"param1\":-1,\"param2\":true}]}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithRepeatableCompoundOptional_valueMissing() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractIngredientWithRepeatableCompoundOptionalHook() {
            @Override
            public void bake(IngredientWithRepeatableCompoundOptionalData data, Cake cake) {
                assertFalse(data.hasCompoundOptional());
                spy.run();
            }
        });

        oven.bake(payloadJson("{\"IngredientWithRepeatableCompoundOptional\":{}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithStringParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals("foobar", data.getStringArg());
                spy.run();
            }
        });

        oven.bake(payloadJson("{\"AllParamsIngredient\":{\"stringArg\":\"foobar\"}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithStringParam_null() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(null, data.getStringArg());
                spy.run();
            }
        });

        oven.bake(payloadJson("{\"AllParamsIngredient\":{\"stringArg\":null}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithIntParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(100, data.getIntArg());
                spy.run();
            }
        });

        oven.bake(payloadJson("{\"AllParamsIngredient\":{\"intArg\":100}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithFloatParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(-1.543f, data.getFloatArg(), 0);
                spy.run();
            }
        });

        oven.bake(payloadJson("{\"AllParamsIngredient\":{\"floatArg\":-1.543}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithBooleanParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(true, data.getBooleanArg());
                spy.run();
            }
        });

        oven.bake(payloadJson("{\"AllParamsIngredient\":{\"booleanArg\":true}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithFlagParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(false, data.getFlagArg());
                spy.run();
            }
        });

        oven.bake(payloadJson("{\"AllParamsIngredient\":{\"flagArg\":false}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithEnumParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(TestEnum.B, data.getEnumArg());
                spy.run();
            }
        });

        oven.bake(payloadJson("{\"AllParamsIngredient\":{\"enumArg\":\"B\"}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithPrimitiveArrayParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(new String[]{"foo", "bar"}, data.getStringArrayArg());
                spy.run();
            }
        });

        oven.bake(payloadJson("{\"AllParamsIngredient\":{\"stringArrayArg\":[\"foo\",\"bar\"]}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithEnumArrayParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(new TestEnum[]{TestEnum.B, TestEnum.C}, data.getEnumArrayArg());
                spy.run();
            }
        });

        oven.bake(payloadJson("{\"AllParamsIngredient\":{\"enumArrayArg\":[\"B\",\"C\"]}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithVarargParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(new String[]{"foo", "bar"}, data.getVarargArg());
                spy.run();
            }
        });

        oven.bake(payloadJson("{\"AllParamsIngredient\":{\"varargArg\":[\"foo\",\"bar\"]}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserialization_ingredientWithVarargArrayParam() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data, Cake cake) {
                assertEquals(new int[][]{new int[]{1, 2}, new int[]{3,4}}, data.getVarargArrayArg());
                spy.run();
            }
        });

        oven.bake(payloadJson("{\"AllParamsIngredient\":{\"varargArrayArg\":[[1,2],[3,4]]}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_deserializesCake() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractEmptyIngredientHook() {
            @Override
            public void bake(EmptyIngredientData data, Cake cake) {
                String value = cake.get("someKey");
                assertEquals("someValue", value);
                spy.run();
            }
        });

        oven.bake(payloadJsonWithCake("{\"someKey\":\"someValue\"}", "{\"EmptyIngredient\":{}}"));
        verify(spy).run();
    }

    @Test
    public void testBake_bakesIngredientInContext() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractEmptyIngredientHook() {
            @Override
            public void bake(EmptyIngredientData data, Cake cake) {
                cake.publish("bar", "value");
                assertEquals(Cake.key("foo", "bar"), cake.getPublishedKeyForValue("value", true));
                spy.run();
            }
        });

        oven.bake("{\"recipe\":{\"Recipe\":{\"ingredients\":[{\"EmptyIngredient\":{}}],\"context\":\"foo\"}},\"cake\":{}}");
        verify(spy).run();
    }

    @Test
    public void testBake_bakesIngredientInContextOfIngredient() {
        Runnable spy = spy(Runnable.class);
        BackendOven oven = new BackendOven();
        oven.registerHook(new AbstractKeyedTestIngredientHook() {
            @Override
            public void bake(KeyedTestIngredientData data, Cake cake) {}
        });
        oven.registerHook(new AbstractEmptyIngredientHook() {
            @Override
            public void bake(EmptyIngredientData data, Cake cake) {
                cake.publish("bar", "value");
                assertEquals(Cake.key("foo", "bar"), cake.getPublishedKeyForValue("value", true));
                spy.run();
            }
        });

        oven.bake("{\"recipe\":{\"Recipe\":{\"ingredients\":[{\"EmptyIngredient\":{}}],\"contextIngredient\":{\"KeyedTestIngredient\":{\"key\":\"foo\"}}}},\"cake\":{}}");
        verify(spy).run();
    }

    private String payloadJson(String... ingredientJson) {
        return "{\"recipe\":{\"Recipe\":{\"ingredients\":[" + StringUtils.join(ingredientJson, ",") + "]}}}";
    }

    private String payloadJsonWithCake(String cake, String... ingredientJson) {
        return "{\"recipe\":{\"Recipe\":{\"ingredients\":[" + StringUtils.join(ingredientJson, ",") + "]}},\"cake\":" + cake + "}";
    }
}
