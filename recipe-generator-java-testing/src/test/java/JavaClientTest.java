import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class JavaClientTest {
    @Test
    public void testGeneration_emptyIngredient() {
        new EmptyIngredient();
    }

    @Test
    public void testGeneration_ingredientWithRequired() {
        new IngredientWithRequired("foo");
    }

    @Test
    public void testGeneration_ingredientWithOptional() {
        new IngredientWithOptional()
            .withOptional(true);
    }

    @Test
    public void testGeneration_ingredientOptionalReturnsSameClass() {
        assertTrue(new IngredientWithOptional().withOptional(true) instanceof IngredientWithOptional);
    }

    @Test
    public void testGeneration_ingredientWithRequiredAndOptional() {
        new IngredientWithRequiredAndOptional("foo")
            .withOptional(false);
    }

    @Test
    public void testGeneration_stringType() {
        new AllParamsIngredient()
            .withStringArg("foo");
    }

    @Test
    public void testGeneration_booleanType() {
        new AllParamsIngredient()
            .withBooleanArg(true);
    }

    @Test
    public void testGeneration_intType() {
        new AllParamsIngredient()
            .withIntArg(-5);
    }

    @Test
    public void testGeneration_flagType() {
        new AllParamsIngredient()
            .withFlagArg();
    }

    @Test
    public void testGeneration_enumType() {
        new AllParamsIngredient()
            .withEnumArg(TestEnum.B);
    }

    @Test
    public void testGeneration_compoundOptional() {
        new IngredientWithCompoundOptional()
            .withCompoundOptional(5, true);
    }

    @Test
    public void testGeneration_compoundOptionalReturnsInstanceOfSameType() {
        assertTrue(new IngredientWithCompoundOptional().withCompoundOptional(0, false) instanceof IngredientWithCompoundOptional);
    }
}
