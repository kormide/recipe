package ca.derekcormier.recipe.cookbook;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CookbookUtilsTest {
    @Test
    public void testIsPrimitiveType_string() {
        assertTrue(CookbookUtils.isPrimitiveType("string"));
    }

    @Test
    public void testIsPrimitiveType_boolean() {
        assertTrue(CookbookUtils.isPrimitiveType("boolean"));
    }

    @Test
    public void testIsPrimitiveType_int() {
        assertTrue(CookbookUtils.isPrimitiveType("int"));
    }

    @Test
    public void testIsPrimitiveType_unknownType() {
        assertFalse(CookbookUtils.isPrimitiveType("foobar"));
    }

    @Test
    public void testIsKnownType_primitive() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        assertTrue(CookbookUtils.isKnownType(cookbook, "boolean"));
    }

    @Test
    public void testIsKnownType_flag() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        assertTrue(CookbookUtils.isKnownType(cookbook, "flag"));
    }

    @Test
    public void testIsKnownType_enum() {
        List<Enum> enums = new ArrayList<>();
        List<String> enumValues = new ArrayList<>();
        enumValues.add("A");
        enumValues.add("B");
        enums.add(new Enum("MyEnum", enumValues));

        Cookbook cookbook = new Cookbook(new ArrayList<>(), enums);
        assertTrue(CookbookUtils.isKnownType(cookbook, "MyEnum"));
    }

    @Test
    public void testIsKnownType_unknownType() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        assertFalse(CookbookUtils.isKnownType(cookbook, "foobar"));
    }

    @Test
    public void testIsFlagType_flag() {
        assertTrue(CookbookUtils.isFlagType("flag"));
    }

    @Test
    public void testIsFlagType_notFlag() {
        assertFalse(CookbookUtils.isFlagType("foobar"));
    }
}
