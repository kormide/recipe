package ca.derekcormier.recipe.cookbook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class CookbookLoaderTest {
    private CookbookLoader loader;

    @Before
    public void before() {
        loader = new CookbookLoader();
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnEmpty() {
        String ingredients = "";
        loader.load(toStream(ingredients));
    }

    @Test
    public void testLoad_domainDefaultsToEmptyString() {
        String ingredients = "ingredients: []";
        Cookbook cookbook = loader.load(toStream(ingredients));
        assertEquals("", cookbook.getDomain());
    }

    @Test
    public void testLoad_withDomain() {
        String ingredients = "domain: 'Domain'";
        Cookbook cookbook = loader.load(toStream(ingredients));
        assertEquals("Domain", cookbook.getDomain());
    }

    @Test
    public void testLoad_noIngredients() {
        String ingredients = "ingredients: []";
        Cookbook cookbook = loader.load(toStream(ingredients));
        assertEquals(0, cookbook.getIngredients().size());
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnIngredientWithoutName() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - initializers: []"
        );

        loader.load(toStream(ingredients));
    }

    @Test
    public void testLoad_ingredientUnspecifiedFieldsGetDefaultValues() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'"
        );

        Cookbook cookbook = loader.load(toStream(ingredients));
        assertFalse(cookbook.getIngredients().get(0).isKeyed());
        assertEquals(0, cookbook.getIngredients().get(0).getRequired().size());
        assertEquals(0, cookbook.getIngredients().get(0).getInitializers().size());
        assertEquals(0, cookbook.getIngredients().get(0).getOptionals().size());
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnDuplicateIngredientNames() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "  - name: 'fooIngredient'"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnRequiredWithoutName() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    required:",
            "      - type: 'string'"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnRequiredWithoutType() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    required:",
            "      - name: 'fooParam'"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnRequiredWithoutUnknownType() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    required:",
            "      - name: 'fooParam'",
            "        type: 'something'"
        );

        loader.load(toStream(ingredients));
    }

    @Test
    public void testLoad_ingredientWithNoInitializers() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    initializers: []"
        );

        Cookbook cookbook = loader.load(toStream(ingredients));
        assertEquals(0, cookbook.getIngredients().get(0).getInitializers().size());
    }

    @Test
    public void testLoad_initializerWithNoParams() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    initializers:",
            "      - params: []"
        );

        Cookbook cookbook = loader.load(toStream(ingredients));
        assertEquals(1, cookbook.getIngredients().get(0).getInitializers().size());
        assertEquals(0, cookbook.getIngredients().get(0).getInitializers().get(0).getParams().size());
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnInitializerWithUndeclaredRequiredParam() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    required:",
            "      - name: 'requiredParam'",
            "        type: 'string'",
            "    initializers:",
            "      - params:",
            "        - 'undeclared'"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnInitializersWithSameTypeSignatures() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    required:",
            "      - name: 'param1'",
            "        type: 'string'",
            "    required:",
            "      - name: 'param2'",
            "        type: 'string'",
            "    initializers:",
            "      - params:",
            "        - 'param1'",
            "      - params:",
            "        - 'param2'"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnInitializersWithSameTypeSignatures_twoEmptyInitializers() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    required:",
            "      - name: 'param1'",
            "        type: 'string'",
            "    required:",
            "      - name: 'param2'",
            "        type: 'string'",
            "    initializers:",
            "      - params: []",
            "      - params: []"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnRequiredParamWithTypeFlag() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    required:",
            "      - name: 'param'",
            "        type: 'flag'",
            "    initializers:",
            "      - params:",
            "        - 'param'"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsWhenRequiredHasNoDefaultAndNotInAnyInitializer() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    required:",
            "      - name: 'param'",
            "        type: 'string'"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsWhenRequiredHasNoDefaultAndNotInOneInitializer() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    required:",
            "      - name: 'param1'",
            "        type: 'string'",
            "      - name: 'param2'",
            "        type: 'boolean'",
            "    initializers:",
            "      - params: ['param1', 'param2']",
            "      - params: ['param2']"
        );

        loader.load(toStream(ingredients));
    }

    @Test
    public void testLoad_requiredWithDefaultValueNotInInitializerDoesNotThrow() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    required:",
            "      - name: 'param'",
            "        type: 'boolean'",
            "        default: true"
        );

        loader.load(toStream(ingredients));
    }

    @Test
    public void testLoad_requiredInInitializerWithNoDefaultValueDoesNotThrow() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    required:",
            "      - name: 'param'",
            "        type: 'boolean'",
            "    initializers:",
            "      - params: ['param']"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnOptionalWithoutName() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    optionals:",
            "      - type: 'string'"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnOptionalWithoutTypeOrParams() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    optionals:",
            "      - name: 'optional'"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnOptionalWithEmptyParams() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    optionals:",
            "      - name: 'optional'",
            "        params: []"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnOptionalWithTypeAndParams() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    optionals:",
            "      - name: 'optional'",
            "        type: 'boolean'",
            "        params:",
            "          - name: 'param1",
            "            type: 'boolean'",
            "          - name: 'param2",
            "            type: 'string'"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnOptionalWithUnknownType() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    optionals:",
            "      - name: 'optional'",
            "        type: 'something'"
        );

        loader.load(toStream(ingredients));
    }

    @Test
    public void testLoad_optionalRepeatableDefaultsToFalse() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    optionals:",
            "      - name: 'optional'",
            "        type: 'string'"
        );

        Cookbook cookbook = loader.load(toStream(ingredients));
        assertFalse(cookbook.getIngredients().get(0).getOptionals().get(0).isRepeatable());
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnCompoundOptionalParamWithoutName() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    optionals:",
            "      - name: 'optional'",
            "        params:",
            "          - name: 'param1'",
            "            type: 'string'",
            "          - type: 'string'"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnCompoundOptionalParamWithoutType() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    optionals:",
            "      - name: 'optional'",
            "        params:",
            "          - name: 'param1'",
            "            type: 'string'",
            "          - name: 'param2'"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnCompoundOptionalParamWithUnknownType() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    optionals:",
            "      - name: 'optional'",
            "        params:",
            "          - name: 'param1'",
            "            type: 'string'",
            "          - name: 'param2'",
            "            type: 'something'"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnCompoundOptionalWithZeroParams() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    optionals:",
            "      - name: 'optional'",
            "        params: []"
        );

        loader.load(toStream(ingredients));
    }

    @Test
    public void testLoad_compoundOptionalWithOneParam() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    optionals:",
            "      - name: 'optional'",
            "        params:",
            "          - name: 'param'",
            "            type: 'string'"
        );

        Cookbook cookbook = loader.load(toStream(ingredients));

        assertEquals("optional", cookbook.getIngredients().get(0).getOptionals().get(0).getName());
        assertEquals("param", cookbook.getIngredients().get(0).getOptionals().get(0).getParams().get(0).getName());
        assertEquals("string", cookbook.getIngredients().get(0).getOptionals().get(0).getParams().get(0).getType());
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnCompoundOptionalWithParamOfTypeFlag() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    optionals:",
            "      - name: 'optional'",
            "        params:",
            "          - name: 'param1'",
            "            type: 'string'",
            "          - name: 'param2'",
            "            type: 'flag'"
        );

        loader.load(toStream(ingredients));
    }

    @Test
    public void testLoad_optionalWithTypeFlag() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    keyed: true",
            "    optionals:",
            "      - name: 'optionalField'",
            "        type: 'flag'"
        );

        Cookbook cookbook = loader.load(toStream(ingredients));
        assertEquals("optionalField", cookbook.getIngredients().get(0).getOptionals().get(0).getName());
        assertEquals("flag", cookbook.getIngredients().get(0).getOptionals().get(0).getType());
    }

    @Test
    public void testLoad_sampleIngredient() {
        String ingredients = String.join("\n",
            "domain: 'Domain'",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    keyed: true",
            "    required:",
            "      - name: 'requiredField'",
            "        type: 'string'",
            "        default: 'foobar'",
            "    initializers:",
            "      - params: []",
            "      - params:",
            "        - 'requiredField'",
            "    optionals:",
            "      - name: 'optionalField'",
            "        type: 'boolean'",
            "      - name: 'repeatableOptionalField'",
            "        type: 'string'",
            "        repeatable: true",
            "      - name: 'compoundOptionalField'",
            "        params:",
            "          - name: 'compoundOptionalParam1'",
            "            type: 'string'",
            "          - name: 'compoundOptionalParam2'",
            "            type: 'boolean'",
            "      - name: 'repeatableCompoundOptionalField'",
            "        params:",
            "          - name: 'compoundOptionalParam1'",
            "            type: 'string'",
            "          - name: 'compoundOptionalParam2'",
            "            type: 'string'",
            "        repeatable: true"
        );

        Cookbook cookbook = loader.load(toStream(ingredients));

        assertEquals("Domain", cookbook.getDomain());
        assertEquals(1, cookbook.getIngredients().size());
        assertEquals("fooIngredient", cookbook.getIngredients().get(0).getName());
        assertTrue(cookbook.getIngredients().get(0).isKeyed());
        assertEquals(1, cookbook.getIngredients().get(0).getRequired().size());
        assertEquals("requiredField", cookbook.getIngredients().get(0).getRequired().get(0).getName());
        assertEquals("foobar", cookbook.getIngredients().get(0).getRequired().get(0).getDefault());
        assertEquals("string", cookbook.getIngredients().get(0).getRequired().get(0).getType());
        assertEquals(2, cookbook.getIngredients().get(0).getInitializers().size());
        assertEquals(0, cookbook.getIngredients().get(0).getInitializers().get(0).getParams().size());
        assertEquals(1, cookbook.getIngredients().get(0).getInitializers().get(1).getParams().size());
        assertEquals("requiredField", cookbook.getIngredients().get(0).getInitializers().get(1).getParams().get(0));
        assertEquals(4, cookbook.getIngredients().get(0).getOptionals().size());
        assertEquals("optionalField", cookbook.getIngredients().get(0).getOptionals().get(0).getName());
        assertEquals("boolean", cookbook.getIngredients().get(0).getOptionals().get(0).getType());
        assertFalse(cookbook.getIngredients().get(0).getOptionals().get(0).isRepeatable());
        assertEquals("repeatableOptionalField", cookbook.getIngredients().get(0).getOptionals().get(1).getName());
        assertEquals("string", cookbook.getIngredients().get(0).getOptionals().get(1).getType());
        assertTrue(cookbook.getIngredients().get(0).getOptionals().get(1).isRepeatable());
        assertEquals("compoundOptionalField", cookbook.getIngredients().get(0).getOptionals().get(2).getName());
        assertEquals(2, cookbook.getIngredients().get(0).getOptionals().get(2).getParams().size());
        assertEquals("compoundOptionalParam1", cookbook.getIngredients().get(0).getOptionals().get(2).getParams().get(0).getName());
        assertEquals("string", cookbook.getIngredients().get(0).getOptionals().get(2).getParams().get(0).getType());
        assertEquals("compoundOptionalParam2", cookbook.getIngredients().get(0).getOptionals().get(2).getParams().get(1).getName());
        assertEquals("boolean", cookbook.getIngredients().get(0).getOptionals().get(2).getParams().get(1).getType());
        assertFalse(cookbook.getIngredients().get(0).getOptionals().get(2).isRepeatable());
        assertEquals("repeatableCompoundOptionalField", cookbook.getIngredients().get(0).getOptionals().get(3).getName());
        assertEquals(2, cookbook.getIngredients().get(0).getOptionals().get(3).getParams().size());
        assertEquals("compoundOptionalParam1", cookbook.getIngredients().get(0).getOptionals().get(3).getParams().get(0).getName());
        assertEquals("string", cookbook.getIngredients().get(0).getOptionals().get(3).getParams().get(0).getType());
        assertEquals("compoundOptionalParam2", cookbook.getIngredients().get(0).getOptionals().get(3).getParams().get(1).getName());
        assertEquals("string", cookbook.getIngredients().get(0).getOptionals().get(3).getParams().get(1).getType());
        assertTrue(cookbook.getIngredients().get(0).getOptionals().get(3).isRepeatable());
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_requiredAndOptionalHaveSameName() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    required:",
            "      - name: 'foo'",
            "        type: 'string'",
            "    optionals:",
            "      - name: 'foo'",
            "        type: 'string'"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_twoOptionalAndCompoundOptionalHaveSameName() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    optional:",
            "      - name: 'foo'",
            "        type: 'boolean'",
            "      - name: 'foo'",
            "        params:",
            "          - name: 'param1'",
            "            type: 'string'"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnEnumWithoutName() {
        String input = String.join("\n",
            "enums:",
            "    values:",
            "      - 'A'",
            "      - 'B'",
            "      - 'C'"
        );

        loader.load(toStream(input));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnEnumWithoutValues() {
        String input = String.join("\n",
            "enums:",
            "    name: 'fooEnum'"
        );

        loader.load(toStream(input));
    }

    @Test
    public void testLoad_emptyEnums() {
        String input = String.join("\n",
            "enums: []"
        );

        Cookbook cookbook = loader.load(toStream(input));
        assertEquals(0, cookbook.getEnums().size());
    }

    @Test
    public void testLoad_singleEnum() {
        String input = String.join("\n",
            "enums:",
            "  - name: 'fooEnum'",
            "    values:",
            "      - 'A'",
            "      - 'B'",
            "      - 'C'"
        );

        Cookbook cookbook = loader.load(toStream(input));
        assertEquals(1, cookbook.getEnums().size());
        assertEquals("fooEnum", cookbook.getEnums().get(0).getName());
        assertEquals(3, cookbook.getEnums().get(0).getValues().size());
        assertEquals("A", cookbook.getEnums().get(0).getValues().get(0));
        assertEquals("B", cookbook.getEnums().get(0).getValues().get(1));
        assertEquals("C", cookbook.getEnums().get(0).getValues().get(2));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnEmptyEnumValues() {
        String input = String.join("\n",
            "enums:",
            "  - name: 'fooEnum'",
            "    values: []"
        );

        loader.load(toStream(input));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnDuplicateEnumNames() {
        String input = String.join("\n",
            "enums:",
            "  - name: 'fooEnum'",
            "    values:",
            "      - 'A'",
            "  - name: 'fooEnum'",
            "    values:",
            "      - 'B'"
        );

        loader.load(toStream(input));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnEnumWithDuplicateValues() {
        String input = String.join("\n",
            "enums:",
            "  - name: 'fooEnum'",
            "    values:",
            "      - 'A'",
            "      - 'A'"
        );

        loader.load(toStream(input));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnEnumNameThatStartsWithDigit() {
        String yaml = String.join("\n",
            "enums:",
            "  - name: '1myEnum'",
            "    values:",
            "      - 'A'"
        );

        loader.load(toStream(yaml));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnEnumNameWithSpecialCharacter() {
        String yaml = String.join("\n",
            "enums:",
            "  - name: 'My@Enum'",
            "    values:",
            "      - 'A'"
        );

        loader.load(toStream(yaml));
    }

    @Test
    public void testLoad_ingredientWithPrimitiveArrayType_doesNotThrow() {
        String yaml = String.join("\n",
            "ingredients:",
            "  - name: 'ingredient'",
            "    optionals:",
            "      - name: 'arg'",
            "        type: 'string[]'"
        );

        loader.load(toStream(yaml));
    }

    @Test
    public void testLoad_ingredientWithPrimitiveVararg_doesNotThrow() {
        String yaml = String.join("\n",
            "ingredients:",
            "  - name: 'ingredient'",
            "    optionals:",
            "      - name: 'arg'",
            "        type: 'int...'"
        );

        loader.load(toStream(yaml));
    }

    @Test
    public void testLoad_ingredientWithPrimitiveArrayVararg_doesNotThrow() {
        String yaml = String.join("\n",
            "ingredients:",
            "  - name: 'ingredient'",
            "    optionals:",
            "      - name: 'arg'",
            "        type: 'int[]...'"
        );

        loader.load(toStream(yaml));
    }

    @Test
    public void testLoad_ingredientWithEnumArray_doesNotThrow() {
        String yaml = String.join("\n",
            "ingredients:",
            "  - name: 'ingredient'",
            "    optionals:",
            "      - name: 'arg'",
            "        type: 'MyEnum[]'",
            "enums:",
            "  - name: 'MyEnum'",
            "    values:",
            "      - 'A'"
        );

        loader.load(toStream(yaml));
    }

    @Test
    public void testLoad_ingredientWithEnumVararg_doesNotThrow() {
        String yaml = String.join("\n",
            "ingredients:",
            "  - name: 'ingredient'",
            "    optionals:",
            "      - name: 'arg'",
            "        type: 'MyEnum...'",
            "enums:",
            "  - name: 'MyEnum'",
            "    values:",
            "      - 'A'"
        );

        loader.load(toStream(yaml));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnIngredientRequiredVarargParamNotLastParamInInitializer() {
        String yaml = String.join("\n",
            "ingredients:",
            "  - name: 'ingredient'",
            "    required:",
            "      - name: 'param1'",
            "        type: 'string...'",
            "      - name: 'param2'",
            "        type: 'int'",
            "    initializers:",
            "      - params: ['param1', 'param2']"
        );

        loader.load(toStream(yaml));
    }

    @Test
    public void testLoad_doesNotThrowOnIngredientRequiredVarargParamLastParamInInitializer() {
        String yaml = String.join("\n",
            "ingredients:",
            "  - name: 'ingredient'",
            "    required:",
            "      - name: 'param1'",
            "        type: 'string...'",
            "      - name: 'param2'",
            "        type: 'int'",
            "    initializers:",
            "      - params: ['param2', 'param1']"
        );

        loader.load(toStream(yaml));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnIngredientCompoundOptionalVarargParamNotLastParam() {
        String yaml = String.join("\n",
            "ingredients:",
            "  - name: 'ingredient'",
            "    optionals:",
            "      - name: 'optional'",
            "        params:",
            "          - name: 'param1'",
            "            type: 'int...'",
            "          - name: 'param2'",
            "            type: 'boolean'"
        );

        loader.load(toStream(yaml));
    }

    @Test
    public void testLoad_doesNotThrowOnIngredientCompoundOptionalVarargParamIsLastParam() {
        String yaml = String.join("\n",
            "ingredients:",
            "  - name: 'ingredient'",
            "    optionals:",
            "      - name: 'optional'",
            "        params:",
            "          - name: 'param1'",
            "            type: 'boolean'",
            "          - name: 'param2'",
            "            type: 'int...'"
        );

        loader.load(toStream(yaml));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnIngredientVarargFlagType() {
        String yaml = String.join("\n",
            "ingredients:",
            "  - name: 'ingredient'",
            "    optionals:",
            "      - name: 'optional'",
            "        type: 'flag...'"
        );

        loader.load(toStream(yaml));
    }

    private InputStream toStream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }
}
