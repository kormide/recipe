import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.function.BiConsumer;

import ca.derekcormier.recipe.Ingredient;
import ca.derekcormier.recipe.Oven;
import ca.derekcormier.recipe.Recipe;

public class JavaIngredientTest {
    private Oven oven;
    BiConsumer<String,String> dispatcherSpy;
    ArgumentCaptor<String> jsonCaptor;

    @Before
    public void before() {
        oven = new Oven();
    }

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
    public void testGeneration_ingredientsHaveCorrectDomain() {
        BiConsumer<String,String> spy = spy(BiConsumer.class);
        oven.addDispatcher(spy);
        Ingredient ingredient = new IngredientWithRequired("foo");
        oven.bake(Recipe.prepare(ingredient));

        verify(spy).accept(eq("TestDomain"), anyString());
    }

    @Test
    public void testGeneration_compoundOptionalReturnsInstanceOfSameType() {
        assertTrue(new IngredientWithCompoundOptional().withCompoundOptional(0, false) instanceof IngredientWithCompoundOptional);
    }

    @Test
    public void testBake_emptyIngredient() {
        setupDispatcherSpy();
        oven.bake(Recipe.prepare(new EmptyIngredient()));

        assertDispatchedJson("{\"EmptyIngredient\":{}}");
    }


    @Test
    public void testBake_ingredientWithRequired() {
        setupDispatcherSpy();
        oven.bake(Recipe.prepare(new IngredientWithRequired("foobar")));

        assertDispatchedJson("{\"IngredientWithRequired\":{\"required\":\"foobar\"}}");
    }

    @Test
    public void testBake_ingredientWithDefaultRequired() {
        setupDispatcherSpy();
        oven.bake(Recipe.prepare(new IngredientWithDefaultRequired(false)));

        assertDispatchedJson("{\"IngredientWithDefaultRequired\":{\"param1\":\"foobar\",\"param2\":false,\"param3\":\"A\"}}");
    }

    @Test
    public void testBake_ingredientWithOptional() {
        setupDispatcherSpy();
        oven.bake(Recipe.prepare(new IngredientWithOptional().withOptional(true)));

        assertDispatchedJson("{\"IngredientWithOptional\":{\"optional\":true}}");
    }

    @Test
    public void testBake_ingredientWithRepeatableOptional() {
        setupDispatcherSpy();
        oven.bake(Recipe.prepare(new IngredientWithRepeatableOptional()
            .withOptional(true)
            .withOptional(false)
        ));

        assertDispatchedJson("{\"IngredientWithRepeatableOptional\":{\"optional\":[true,false]}}");
    }

    @Test
    public void testBake_ingredientWithCompoundOptional() {
        setupDispatcherSpy();
        oven.bake(Recipe.prepare(new IngredientWithCompoundOptional().withCompoundOptional(5, false)));

        assertDispatchedJson("{\"IngredientWithCompoundOptional\":{\"compoundOptional\":{\"param1\":5,\"param2\":false}}}");
    }

    @Test
    public void testBake_ingredientWithRepeatableCompoundOptional() {
        setupDispatcherSpy();
        oven.bake(Recipe.prepare(new IngredientWithRepeatableCompoundOptional()
            .withCompoundOptional(5, false)
            .withCompoundOptional(-2, true)
        ));

        assertDispatchedJson("{\"IngredientWithRepeatableCompoundOptional\":{\"compoundOptional\":{\"param1\":[5,-2],\"param2\":[false,true]}}}");
    }

    @Test
    public void testBake_ingredientWithCompoundOptionalWithOneParam() {
        setupDispatcherSpy();
        oven.bake(Recipe.prepare(new IngredientWithCompoundOptionalWithOneParam().withCompoundOptional(3)));

        assertDispatchedJson("{\"IngredientWithCompoundOptionalWithOneParam\":{\"compoundOptional\":{\"param1\":3}}}");
    }

    @Test
    public void testBake_ingredientWithRequiredAndOptional() {
        setupDispatcherSpy();
        oven.bake(Recipe.prepare(new IngredientWithRequiredAndOptional("foobar").withOptional(true)));

        assertDispatchedJson("{\"IngredientWithRequiredAndOptional\":{\"required\":\"foobar\",\"optional\":true}}");
    }

    @Test
    public void testBake_ingredientWithAllParamTypes() {
        setupDispatcherSpy();
        oven.bake(Recipe.prepare(
            new AllParamsIngredient()
                .withBooleanArg(true)
                .withEnumArg(TestEnum.B)
                .withFlagArg()
                .withStringArg("foobar")
                .withIntArg(-10)
        ));

        assertDispatchedJson("{\"AllParamsIngredient\":{\"booleanArg\":true,\"enumArg\":\"B\",\"flagArg\":true,\"stringArg\":\"foobar\",\"intArg\":-10}}");
    }

    private ObjectMapper objectMapper = new ObjectMapper();
    public void assertJsonEquals(String expected, String actual) {
        try {
            assertEquals(
                objectMapper.readValue(expected, new TypeReference<Map<String, Object>>() {}),
                (Map<String, Object>)objectMapper.readValue(actual, new TypeReference<Map<String, Object>>() {})
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupDispatcherSpy() {
        dispatcherSpy = spy(BiConsumer.class);
        jsonCaptor = ArgumentCaptor.forClass(String.class);
        oven.addDispatcher(dispatcherSpy);
    }

    private void assertDispatchedJson(String expected) {
        verify(dispatcherSpy).accept(anyString(), jsonCaptor.capture());
        assertJsonEquals(expected, jsonCaptor.getValue());
    }
}
