package ca.derekcormier.recipe.cookbook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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
        assertEquals(0, cookbook.getIngredients().get(0).getCompoundOptionals().size());
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
    public void testLoad_requiredUnspecifiedFieldsGetDefaultValues() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    required:",
            "      - name: 'foo'",
            "        type: 'string'"
        );

        Cookbook cookbook = loader.load(toStream(ingredients));
        assertNull(cookbook.getIngredients().get(0).getRequired().get(0).getDefaultValue());
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
    public void testLoad_throwsOnOptionalWithoutType() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    optionals:",
            "      - name: 'fooIngredient'"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnOptionalWithUnknownType() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    optionals:",
            "      - name: 'fooParam'",
            "        type: 'something'"
        );

        loader.load(toStream(ingredients));
    }

    @Test
    public void testLoad_optionalUnspecifiedFieldsGetDefaultValues() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    optionals:",
            "      - name: 'fooIngredient'",
            "        type: 'string'"
        );

        Cookbook cookbook = loader.load(toStream(ingredients));
        assertFalse(cookbook.getIngredients().get(0).getOptionals().get(0).isRepeatable());
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnCompoundOptionalWithoutName() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    compoundOptionals:",
            "      - params: []"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnCompoundOptionalWithoutParams() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    compoundOptionals:",
            "      - name: 'fooOptional'"
            );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnCompoundOptionalParamWithoutName() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    compoundOptionals:",
            "      - name: 'fooOptional'",
            "        params:",
            "          - name: 'fooParam'",
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
            "    compoundOptionals:",
            "      - name: 'fooOptional'",
            "        params:",
            "          - name: 'fooParam'",
            "            type: 'string'",
            "          - name: 'fooParam2'"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnCompoundOptionalParamWithUnknownType() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    compoundOptionals:",
            "      - name: 'fooOptional'",
            "        params:",
            "          - name: 'fooParam'",
            "            type: 'string'",
            "          - name: 'fooParam2'",
            "            type: 'something'"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnCompoundOptionalWithZeroParams() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    compoundOptionals:",
            "      - name: 'fooOptional'",
            "        params: []"
        );

        loader.load(toStream(ingredients));
    }

    @Test(expected = RuntimeException.class)
    public void testLoad_throwsOnCompoundOptionalWithOneParam() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    compoundOptionals:",
            "      - name: 'fooOptional'",
            "        params:",
            "          - name: 'param'",
            "            type: 'string'"
        );

        loader.load(toStream(ingredients));
    }

    @Test
    public void testLoad_sampleIngredient() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    keyed: true",
            "    required:",
            "      - name: 'requiredField'",
            "        type: 'string'",
            "        defaultValue: ''",
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
            "    compoundOptionals:",
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

        assertEquals(1, cookbook.getIngredients().size());
        assertEquals("fooIngredient", cookbook.getIngredients().get(0).getName());
        assertTrue(cookbook.getIngredients().get(0).isKeyed());
        assertEquals(1, cookbook.getIngredients().get(0).getRequired().size());
        assertEquals("requiredField", cookbook.getIngredients().get(0).getRequired().get(0).getName());
        assertEquals(Type.STRING, cookbook.getIngredients().get(0).getRequired().get(0).getType());
        assertEquals(2, cookbook.getIngredients().get(0).getInitializers().size());
        assertEquals(0, cookbook.getIngredients().get(0).getInitializers().get(0).getParams().size());
        assertEquals(1, cookbook.getIngredients().get(0).getInitializers().get(1).getParams().size());
        assertEquals("requiredField", cookbook.getIngredients().get(0).getInitializers().get(1).getParams().get(0));
        assertEquals(2, cookbook.getIngredients().get(0).getOptionals().size());
        assertEquals("optionalField", cookbook.getIngredients().get(0).getOptionals().get(0).getName());
        assertEquals(Type.BOOLEAN, cookbook.getIngredients().get(0).getOptionals().get(0).getType());
        assertFalse(cookbook.getIngredients().get(0).getOptionals().get(0).isRepeatable());
        assertEquals("repeatableOptionalField", cookbook.getIngredients().get(0).getOptionals().get(1).getName());
        assertEquals(Type.STRING, cookbook.getIngredients().get(0).getOptionals().get(1).getType());
        assertTrue(cookbook.getIngredients().get(0).getOptionals().get(1).isRepeatable());
        assertEquals(2, cookbook.getIngredients().get(0).getCompoundOptionals().size());
        assertEquals("compoundOptionalField", cookbook.getIngredients().get(0).getCompoundOptionals().get(0).getName());
        assertEquals(2, cookbook.getIngredients().get(0).getCompoundOptionals().get(0).getParams().size());
        assertEquals("compoundOptionalParam1", cookbook.getIngredients().get(0).getCompoundOptionals().get(0).getParams().get(0).getName());
        assertEquals(Type.STRING, cookbook.getIngredients().get(0).getCompoundOptionals().get(0).getParams().get(0).getType());
        assertEquals("compoundOptionalParam2", cookbook.getIngredients().get(0).getCompoundOptionals().get(0).getParams().get(1).getName());
        assertEquals(Type.BOOLEAN, cookbook.getIngredients().get(0).getCompoundOptionals().get(0).getParams().get(1).getType());
        assertFalse(cookbook.getIngredients().get(0).getCompoundOptionals().get(0).isRepeatable());
        assertEquals("repeatableCompoundOptionalField", cookbook.getIngredients().get(0).getCompoundOptionals().get(1).getName());
        assertEquals(2, cookbook.getIngredients().get(0).getCompoundOptionals().get(1).getParams().size());
        assertEquals("compoundOptionalParam1", cookbook.getIngredients().get(0).getCompoundOptionals().get(1).getParams().get(0).getName());
        assertEquals(Type.STRING, cookbook.getIngredients().get(0).getCompoundOptionals().get(1).getParams().get(0).getType());
        assertEquals("compoundOptionalParam2", cookbook.getIngredients().get(0).getCompoundOptionals().get(1).getParams().get(1).getName());
        assertEquals(Type.STRING, cookbook.getIngredients().get(0).getCompoundOptionals().get(1).getParams().get(1).getType());

        assertTrue(cookbook.getIngredients().get(0).getCompoundOptionals().get(1).isRepeatable());
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
    public void testLoad_optionalAndCompoundOptionalHaveSameName() {
        String ingredients = String.join("\n",
            "ingredients:",
            "  - name: 'fooIngredient'",
            "    required:",
            "      - name: 'foo'",
            "        type: 'string'",
            "    compoundOptionals:",
            "      - name: 'foo'",
            "        params:",
            "          - name: 'fooParam'",
            "            type: 'string'",
            "          - name: 'fooParam2'",
            "            type: 'boolean'"
        );

        loader.load(toStream(ingredients));
    }

    private InputStream toStream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }
}
