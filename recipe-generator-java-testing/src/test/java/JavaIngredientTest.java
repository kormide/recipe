import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

import ca.derekcormier.recipe.Cake;
import ca.derekcormier.recipe.Dispatcher;
import ca.derekcormier.recipe.Oven;
import ca.derekcormier.recipe.Recipe;
import testdomain.ingredients.AllParamsIngredient;
import testdomain.ingredients.EmptyIngredient;
import testdomain.ingredients.IngredientWithCompoundOptional;
import testdomain.ingredients.IngredientWithCompoundOptionalWithOneParam;
import testdomain.ingredients.IngredientWithDefaultRequired;
import testdomain.ingredients.IngredientWithDefaultRequiredNoInitializers;
import testdomain.ingredients.IngredientWithKeyConstant;
import testdomain.ingredients.IngredientWithOptional;
import testdomain.ingredients.IngredientWithRepeatableCompoundOptional;
import testdomain.ingredients.IngredientWithRepeatableOptional;
import testdomain.ingredients.IngredientWithRepeatableVarargOptional;
import testdomain.ingredients.IngredientWithRequired;
import testdomain.ingredients.IngredientWithRequiredAndOptional;
import testdomain.ingredients.IngredientWithStringDefaultContainingQuotes;
import testdomain.ingredients.KeyedTestIngredient;
import testdomain.ingredients.TestEnum;

public class JavaIngredientTest {
    private Oven oven;
    Dispatcher dispatcherSpy;
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
    public void testGeneration_ingredientWithDefaultRequiredNoInitializers() {
        new IngredientWithDefaultRequiredNoInitializers();
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
    public void testGeneration_ingredientWithRepeatableVarargOptional() {
        new IngredientWithRepeatableVarargOptional()
            .withOptional(1, 2)
            .withOptional(3, 4);
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
    public void testGeneration_floatType() {
        new AllParamsIngredient()
            .withFloatArg(1.123f);
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
    public void testGeneration_primitiveArrayType() {
        new AllParamsIngredient()
            .withStringArrayArg(new String[]{"foo", "bar"});
    }

    @Test
    public void testGeneration_enumArrayType() {
        new AllParamsIngredient()
            .withEnumArrayArg(new TestEnum[]{TestEnum.A, TestEnum.C});
    }

    @Test
    public void testGeneration_varargType() {
        new AllParamsIngredient()
            .withVarargArg("foo", "bar");
    }

    @Test
    public void testGeneration_varargArrayType() {
        new AllParamsIngredient()
            .withVarargArrayArg(new int[]{1, 2}, new int[]{3, 4});
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
    public void testGeneration_ingredientWithStringDefaultContainingQuotes() {
        new IngredientWithStringDefaultContainingQuotes();
    }

    @Test
    public void testGeneration_ingredientsHaveCorrectDomain() {
        setupDispatcherSpy("TestDomain");
        oven.bake(Recipe.prepare(new IngredientWithRequired("foo")));

        verify(dispatcherSpy).dispatch(anyString());
    }

    @Test
    public void testGeneration_compoundOptionalReturnsInstanceOfSameType() {
        assertTrue(new IngredientWithCompoundOptional().withCompoundOptional(0, false) instanceof IngredientWithCompoundOptional);
    }

    @Test
    public void testGeneration_ingredientWithPostfix() {
        new PostfixIngredientFoo();
    }

    @Test
    public void testGeneration_ingredientWithPostfixDoesNotIncludePostfixInType() {
        PostfixIngredientFoo ingredient = new PostfixIngredientFoo();
        assertEquals("PostfixIngredient", ingredient.getIngredientType());
    }

    @Test
    public void testGeneration_generatesKeyConstants() {
        assertEquals("KEY_A", IngredientWithKeyConstant.KEY_A);
    }

    @Test
    public void testBake_serialization_emptyIngredient() {
        setupDispatcherSpy("TestDomain");
        oven.bake(Recipe.prepare(new EmptyIngredient()));

        assertDispatchedJson(payloadJson("{\"EmptyIngredient\":{}}"));
    }

    @Test
    public void testBake_serialization_ingredientWithRequired() {
        setupDispatcherSpy("TestDomain");
        oven.bake(Recipe.prepare(new IngredientWithRequired("foobar")));

        assertDispatchedJson(payloadJson("{\"IngredientWithRequired\":{\"required\":\"foobar\"}}"));
    }

    @Test
    public void testBake_serialization_ingredientWithDefaultRequired() {
        setupDispatcherSpy("TestDomain");
        oven.bake(Recipe.prepare(new IngredientWithDefaultRequired(false)));

        assertDispatchedJson(payloadJson("{\"IngredientWithDefaultRequired\":{\"param1\":\"foobar\",\"param2\":false,\"param3\":\"A\"}}"));
    }

    @Test
    public void testBake_serialization_ingredientWithDefaultRequiredNoInitializers() {
        setupDispatcherSpy("TestDomain");
        oven.bake(Recipe.prepare(new IngredientWithDefaultRequiredNoInitializers()));

        assertDispatchedJson(payloadJson("{\"IngredientWithDefaultRequiredNoInitializers\":{\"required\":5}}"));
    }

    @Test
    public void testBake_serialization_ingredientWithOptional() {
        setupDispatcherSpy("TestDomain");
        oven.bake(Recipe.prepare(new IngredientWithOptional().withOptional(true)));

        assertDispatchedJson(payloadJson("{\"IngredientWithOptional\":{\"optional\":true}}"));
    }

    @Test
    public void testBake_serialization_ingredientWithRepeatableOptional() {
        setupDispatcherSpy("TestDomain");
        oven.bake(Recipe.prepare(new IngredientWithRepeatableOptional()
            .withOptional(true)
            .withOptional(false)
        ));

        assertDispatchedJson(payloadJson("{\"IngredientWithRepeatableOptional\":{\"optional\":[true,false]}}"));
    }

    @Test
    public void testBake_serialization_ingredientWithRepeatableVarargOptional() {
        setupDispatcherSpy("TestDomain");
        oven.bake(Recipe.prepare(new IngredientWithRepeatableVarargOptional()
            .withOptional(1, 2)
            .withOptional(3, 4)
        ));

        assertDispatchedJson(payloadJson("{\"IngredientWithRepeatableVarargOptional\":{\"optional\":[[1,2],[3,4]]}}"));
    }

    @Test
    public void testBake_serialization_ingredientWithCompoundOptional() {
        setupDispatcherSpy("TestDomain");
        oven.bake(Recipe.prepare(new IngredientWithCompoundOptional().withCompoundOptional(5, false)));

        assertDispatchedJson(payloadJson("{\"IngredientWithCompoundOptional\":{\"compoundOptional\":{\"param1\":5,\"param2\":false}}}"));
    }

    @Test
    public void testBake_serialization_ingredientWithRepeatableCompoundOptional() {
        setupDispatcherSpy("TestDomain");
        oven.bake(Recipe.prepare(new IngredientWithRepeatableCompoundOptional()
            .withCompoundOptional(5, false)
            .withCompoundOptional(-2, true)
        ));

        assertDispatchedJson(payloadJson("{\"IngredientWithRepeatableCompoundOptional\":{\"compoundOptional\":[{\"param1\":5,\"param2\":false},{\"param1\":-2,\"param2\":true}]}}"));
    }

    @Test
    public void testBake_serialization_ingredientWithCompoundOptionalWithOneParam() {
        setupDispatcherSpy("TestDomain");
        oven.bake(Recipe.prepare(new IngredientWithCompoundOptionalWithOneParam().withCompoundOptional(3)));

        assertDispatchedJson(payloadJson("{\"IngredientWithCompoundOptionalWithOneParam\":{\"compoundOptional\":{\"param1\":3}}}"));
    }

    @Test
    public void testBake_serialization_ingredientWithRequiredAndOptional() {
        setupDispatcherSpy("TestDomain");
        oven.bake(Recipe.prepare(new IngredientWithRequiredAndOptional("foobar").withOptional(true)));

        assertDispatchedJson(payloadJson("{\"IngredientWithRequiredAndOptional\":{\"required\":\"foobar\",\"optional\":true}}"));
    }

    @Test
    public void testBake_serialization_ingredientWithAllParamTypes() {
        setupDispatcherSpy("TestDomain");
        oven.bake(Recipe.prepare(
            new AllParamsIngredient()
                .withBooleanArg(true)
                .withEnumArg(TestEnum.B)
                .withFlagArg()
                .withStringArg("foobar")
                .withIntArg(-10)
                .withFloatArg(1.123f)
                .withEnumArrayArg(new TestEnum[]{TestEnum.A, TestEnum.B})
                .withVarargArg("foo", "bar")
                .withVarargArrayArg(new int[]{1, 2}, new int[]{3, 4})
        ));

        assertDispatchedJson(payloadJson(
            "{\"AllParamsIngredient\":{\"booleanArg\":true,\"enumArg\":\"B\",\"flagArg\":true,\"stringArg\":\"foobar\",\"intArg\":-10,\"floatArg\":1.123,\"enumArrayArg\":[\"A\",\"B\"],\"varargArg\":[\"foo\",\"bar\"],\"varargArrayArg\":[[1,2],[3,4]]}}"
        ));
    }

    @Test
    public void testBake_serialization_ingredientWithStringDefaultContainingQuotes() {
        setupDispatcherSpy("TestDomain");
        oven.bake(Recipe.prepare(
            new IngredientWithStringDefaultContainingQuotes()
        ));

        assertDispatchedJson(payloadJson(
            "{\"IngredientWithStringDefaultContainingQuotes\":{\"required\":\"\\\"foo\"}}"
        ));
    }

    @Test
    public void testBake_recipeWithContextButNoIngredientsDoesNotDispatch() {
        setupDispatcherSpy("TestDomain");

        oven.bake(Recipe.context("foo"));

        verify(dispatcherSpy, never()).dispatch(anyString());
    }

    @Test
    public void testBake_serialization_recipeWithContextAndChildIngredient() {
        setupDispatcherSpy("TestDomain");

        oven.bake(Recipe.context("foo",
            new EmptyIngredient()
        ));

        assertDispatchedJson("{\"recipe\":{\"Recipe\":{\"ingredients\":[{\"EmptyIngredient\":{}}],\"context\":\"foo\"}},\"cake\":{}}");
    }

    @Test
    public void testBake_serialization_recipeWithUnkeyedContextIngredient() {
        setupDispatcherSpy("TestDomain");

        oven.bake(Recipe.context(new KeyedTestIngredient()));

        assertDispatchedJson("{\"recipe\":{\"Recipe\":{\"contextIngredient\":{\"KeyedTestIngredient\":{}},\"ingredients\":[]}},\"cake\":{}}");
    }

    @Test
    public void testBake_serialization_recipeWithKeyedContextIngredient() {
        setupDispatcherSpy("TestDomain");

        oven.bake(Recipe.context(new KeyedTestIngredient().keyed("foo")));

        assertDispatchedJson("{\"recipe\":{\"Recipe\":{\"contextIngredient\":{\"KeyedTestIngredient\":{\"key\":\"foo\"}},\"ingredients\":[]}},\"cake\":{}}");
    }

    @Test
    public void testBake_serialization_recipeContextIngredientAndChild() {
        setupDispatcherSpy("TestDomain");

        oven.bake(Recipe.context(new KeyedTestIngredient().keyed("foo"),
            new EmptyIngredient()
        ));

        assertDispatchedJson("{\"recipe\":{\"Recipe\":{\"contextIngredient\":{\"KeyedTestIngredient\":{\"key\":\"foo\"}},\"ingredients\":[{\"EmptyIngredient\":{}}]}},\"cake\":{}}");
    }

    @Test
    public void testBake_deserializesCake() {
        dispatcherSpy = spy(Dispatcher.class);
        when(dispatcherSpy.dispatch(anyString())).thenReturn("{\"someKey\":\"someValue\"}");
        oven.addDispatcher("TestDomain", dispatcherSpy);

        Cake cake = oven.bake(Recipe.prepare(
            new EmptyIngredient()
        ));

        assertEquals("someValue", cake.get("someKey"));
    }

    @Test
    public void testBake_propagatesCakeUpdatesToSubsequentDispatches() {
        dispatcherSpy = spy(Dispatcher.class);
        when(dispatcherSpy.dispatch(anyString())).thenReturn("{\"someKey\":\"someValue\"}");
        Dispatcher dispatcherSpyB = spy(Dispatcher.class);
        when(dispatcherSpyB.dispatch(anyString())).thenReturn("{\"someKey\":\"someValue\"}");

        oven.addDispatcher("TestDomain", dispatcherSpy);
        oven.addDispatcher("B", dispatcherSpyB);

        Cake cake = oven.bake(Recipe.prepare(
            new EmptyIngredient(),
            new DomainBIngredient()
        ));

        verify(dispatcherSpy).dispatch(eq(payloadJson("{\"EmptyIngredient\":{}}")));
        verify(dispatcherSpyB).dispatch(eq(payloadJsonWithCake("{\"someKey\":\"someValue\"}", "{\"DomainBIngredient\":{}}")));

        assertEquals("someValue", cake.get("someKey"));
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

    private void setupDispatcherSpy(String domain) {
        dispatcherSpy = spy(Dispatcher.class);
        when(dispatcherSpy.dispatch(anyString())).thenReturn("{}");
        jsonCaptor = ArgumentCaptor.forClass(String.class);
        oven.addDispatcher(domain, dispatcherSpy);
    }

    private void assertDispatchedJson(String expected) {
        verify(dispatcherSpy).dispatch(jsonCaptor.capture());
        assertJsonEquals(expected, jsonCaptor.getValue());
    }

    private String payloadJson(String... ingredientJson) {
        return "{\"recipe\":{\"Recipe\":{\"ingredients\":[" + StringUtils.join(ingredientJson, ",") + "]}},\"cake\":{}}";
    }

    private String payloadJsonWithCake(String cake, String... ingredientJson) {
        return "{\"recipe\":{\"Recipe\":{\"ingredients\":[" + StringUtils.join(ingredientJson, ",") + "]}},\"cake\":" + cake + "}";
    }
}
