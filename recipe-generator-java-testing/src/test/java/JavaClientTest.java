import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

import ca.derekcormier.recipe.Recipe;

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
    public void testGeneration_ingredientWithDefaultRequired() {
        new IngredientWithDefaultRequired("foobar");
        new IngredientWithDefaultRequired(false);
        new IngredientWithDefaultRequired(TestEnum.B);
    }

    @Test
    public void testGeneration_ingredientWithOptional() {
        new IngredientWithOptional()
            .withOptional(true);
    }

    @Test
    public void testGeneration_ingredientWithRepeatableOptional() {
        new IngredientWithRepeatableOptional()
            .withOptional(true)
            .withOptional(false);
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
    public void testGeneration_compoundOptionalWithOneParam() {
        new IngredientWithCompoundOptionalWithOneParam()
            .withCompoundOptional(3);
    }

    @Test
    public void testGeneration_repeatableCompoundOptional() {
        new IngredientWithRepeatableCompoundOptional()
            .withCompoundOptional(1, false)
            .withCompoundOptional(2, true);
    }

    @Test
    public void testGeneration_compoundOptionalReturnsInstanceOfSameType() {
        assertTrue(new IngredientWithCompoundOptional().withCompoundOptional(0, false) instanceof IngredientWithCompoundOptional);
    }

    @Test
    public void testJson_emptyIngredient() {
        Recipe recipe = Recipe.prepare(new EmptyIngredient());

        assertJsonEquals("{\"Recipe\":{\"ingredients\":[{\"EmptyIngredient\":{}}]}}", recipe.toJson());
    }

    @Test
    public void testJson_ingredientWithRequired() {
        Recipe recipe = Recipe.prepare(new IngredientWithRequired("foobar"));

        assertJsonEquals("{\"Recipe\":{\"ingredients\":[{\"IngredientWithRequired\":{\"required\":\"foobar\"}}]}}", recipe.toJson());
    }

    @Test
    public void testJson_ingredientWithDefaultRequired() {
        Recipe recipe = Recipe.prepare(new IngredientWithDefaultRequired(false));

        assertJsonEquals("{\"Recipe\":{\"ingredients\":[{\"IngredientWithDefaultRequired\":{\"param1\":\"foobar\",\"param2\":false,\"param3\":\"A\"}}]}}", recipe.toJson());
    }

    @Test
    public void testJson_ingredientWithOptional() {
        Recipe recipe = Recipe.prepare(new IngredientWithOptional().withOptional(true));

        assertJsonEquals("{\"Recipe\":{\"ingredients\":[{\"IngredientWithOptional\":{\"optional\":true}}]}}", recipe.toJson());
    }

    @Test
    public void testJson_ingredientWithRepeatableOptional() {
        Recipe recipe = Recipe.prepare(new IngredientWithRepeatableOptional()
            .withOptional(true)
            .withOptional(false)
        );

        assertJsonEquals("{\"Recipe\":{\"ingredients\":[{\"IngredientWithRepeatableOptional\":{\"optional\":[true,false]}}]}}", recipe.toJson());
    }

    @Test
    public void testJson_ingredientWithCompoundOptional() {
        Recipe recipe = Recipe.prepare(new IngredientWithCompoundOptional().withCompoundOptional(5, false));

        assertJsonEquals("{\"Recipe\":{\"ingredients\":[{\"IngredientWithCompoundOptional\":{\"compoundOptional\":{\"param1\":5,\"param2\":false}}}]}}", recipe.toJson());
    }

    @Test
    public void testJson_ingredientWithRepeatableCompoundOptional() {
        Recipe recipe = Recipe.prepare(new IngredientWithRepeatableCompoundOptional()
            .withCompoundOptional(5, false)
            .withCompoundOptional(-2, true)
        );

        assertJsonEquals("{\"Recipe\":{\"ingredients\":[{\"IngredientWithRepeatableCompoundOptional\":{\"compoundOptional\":{\"param1\":[5,-2],\"param2\":[false,true]}}}]}}", recipe.toJson());
    }

    @Test
    public void testJson_ingredientWithCompoundOptionalWithOneParam() {
        Recipe recipe = Recipe.prepare(new IngredientWithCompoundOptionalWithOneParam().withCompoundOptional(3));

        assertJsonEquals("{\"Recipe\":{\"ingredients\":[{\"IngredientWithCompoundOptionalWithOneParam\":{\"compoundOptional\":{\"param1\":3}}}]}}", recipe.toJson());
    }

    @Test
    public void testJson_ingredientWithRequiredAndOptional() {
        Recipe recipe = Recipe.prepare(new IngredientWithRequiredAndOptional("foobar").withOptional(true));

        assertJsonEquals("{\"Recipe\":{\"ingredients\":[{\"IngredientWithRequiredAndOptional\":{\"required\":\"foobar\",\"optional\":true}}]}}", recipe.toJson());
    }

    @Test
    public void testJson_ingredientWithAllParamTypes() {
        Recipe recipe = Recipe.prepare(
            new AllParamsIngredient()
                .withBooleanArg(true)
                .withEnumArg(TestEnum.B)
                .withFlagArg()
                .withStringArg("foobar")
                .withIntArg(-10)
        );

        assertJsonEquals("{\"Recipe\":{\"ingredients\":[{\"AllParamsIngredient\":{\"booleanArg\":true,\"enumArg\":\"B\",\"flagArg\":true,\"stringArg\":\"foobar\",\"intArg\":-10}}]}}", recipe.toJson());
    }

    public void assertJsonEquals(String expected, String actual) {
        try {
            assertEquals(
                new ObjectMapper().readValue(expected, new TypeReference<Map<String, Object>>() {}),
                (Map<String, Object>)new ObjectMapper().readValue(actual, new TypeReference<Map<String, Object>>() {})
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
