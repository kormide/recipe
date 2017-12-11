import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.derekcormier.recipe.AbstractIngredientHook;

public class JavaHookTest {
    @Test
    public void testGeneration_generatesIngredientHook_emptyIngredient() {
        new AbstractEmptyIngredientHook() {
            @Override
            public void bake(EmptyIngredientData data) {}
        };
    }

    @Test
    public void testGeneration_generatesIngredientHook_multipleParamsIngredient() {
        new AbstractAllParamsIngredientHook() {
            @Override
            public void bake(AllParamsIngredientData data) {}
        };
    }

    @Test
    public void testGeneration_generatedHookHasCorrectSuperclass() {
        assertEquals(AbstractIngredientHook.class, AbstractAllParamsIngredientHook.class.getSuperclass());
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
}
