package ca.derekcormier.recipe.generator.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.Lists;

import java.util.ArrayList;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.Enum;
import liqp.filters.Filter;

public class TsTypeFilterTest {
    @Test
    public void testApply_convertsInteger() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        Filter filter = new TsTypeFilter(cookbook);

        assertEquals("number", filter.apply("int"));
    }

    @Test
    public void testApply_convertsFloat() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        Filter filter = new TsTypeFilter(cookbook);

        assertEquals("number", filter.apply("float"));
    }

    @Test
    public void testApply_convertsBoolean() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        Filter filter = new TsTypeFilter(cookbook);

        assertEquals("boolean", filter.apply("boolean"));
    }

    @Test
    public void testApply_convertsFlag() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        Filter filter = new TsTypeFilter(cookbook);

        assertEquals("boolean", filter.apply("flag"));
    }

    @Test
    public void testApply_convertsString() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        Filter filter = new TsTypeFilter(cookbook);

        assertEquals("string | null", filter.apply("string"));
    }

    @Test
    public void testApply_convertsEnum() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), Lists.newArrayList(new Enum("MyEnum", Lists.newArrayList("A"))));
        Filter filter = new TsTypeFilter(cookbook);

        assertEquals("MyEnum", filter.apply("MyEnum"));
    }

    @Test
    public void testApply_convertsArrayOfIntegers() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        Filter filter = new TsTypeFilter(cookbook);

        assertEquals("number[]", filter.apply("int[]"));
    }

    @Test
    public void testApply_convertsArrayOfStrings() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        Filter filter = new TsTypeFilter(cookbook);

        assertEquals("(string | null)[]", filter.apply("string[]"));
    }

    @Test
    public void testApply_convertsVarargOfStrings() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        Filter filter = new TsTypeFilter(cookbook);

        assertEquals("(string | null)[]", filter.apply("string..."));
    }

    @Test
    public void testApply_convertsVarargOfInts() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        Filter filter = new TsTypeFilter(cookbook);

        assertEquals("number[]", filter.apply("int..."));
    }

    @Test
    public void testApply_convertsVarargOfStrings_asArrayType() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        Filter filter = new TsTypeFilter(cookbook);

        assertEquals("(string | null)[]", filter.apply("string...", true));
    }

    @Test
    public void testApply_convertsVarargOfArraysOfStrings() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        Filter filter = new TsTypeFilter(cookbook);

        assertEquals("(string | null)[][]", filter.apply("string[]..."));
    }

    @Test
    public void testApply_convertsVarargOfArraysOfStrings_asArrayType() {
        Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
        Filter filter = new TsTypeFilter(cookbook);

        assertEquals("(string | null)[][]", filter.apply("string[]...", true));
    }
}
