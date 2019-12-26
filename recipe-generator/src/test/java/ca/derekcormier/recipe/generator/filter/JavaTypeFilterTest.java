package ca.derekcormier.recipe.generator.filter;

import static org.junit.Assert.assertEquals;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.Enum;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import liqp.filters.Filter;
import org.junit.Test;

public class JavaTypeFilterTest {
  @Test
  public void testApply_convertsInteger() {
    Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
    Filter filter = new JavaTypeFilter(cookbook);

    assertEquals("int", filter.apply("int"));
  }

  @Test
  public void testApply_convertsFloat() {
    Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
    Filter filter = new JavaTypeFilter(cookbook);

    assertEquals("float", filter.apply("float"));
  }

  @Test
  public void testApply_convertsBoolean() {
    Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
    Filter filter = new JavaTypeFilter(cookbook);

    assertEquals("boolean", filter.apply("boolean"));
  }

  @Test
  public void testApply_convertsFlag() {
    Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
    Filter filter = new JavaTypeFilter(cookbook);

    assertEquals("boolean", filter.apply("flag"));
  }

  @Test
  public void testApply_convertsString() {
    Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
    Filter filter = new JavaTypeFilter(cookbook);

    assertEquals("String", filter.apply("string"));
  }

  @Test
  public void testApply_convertsEnum() {
    Cookbook cookbook =
        new Cookbook(
            new ArrayList<>(), Lists.newArrayList(new Enum("MyEnum", Lists.newArrayList("A"))));
    Filter filter = new JavaTypeFilter(cookbook);

    assertEquals("MyEnum", filter.apply("MyEnum"));
  }

  @Test
  public void testApply_convertsArrayOfIntegers() {
    Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
    Filter filter = new JavaTypeFilter(cookbook);

    assertEquals("int[]", filter.apply("int[]"));
  }

  @Test
  public void testApply_convertsArrayOfStrings() {
    Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
    Filter filter = new JavaTypeFilter(cookbook);

    assertEquals("String[]", filter.apply("string[]"));
  }

  @Test
  public void testApply_convertsVarargOfStrings() {
    Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
    Filter filter = new JavaTypeFilter(cookbook);

    assertEquals("String...", filter.apply("string..."));
  }

  @Test
  public void testApply_convertsVarargOfStrings_asArrayType() {
    Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
    Filter filter = new JavaTypeFilter(cookbook);

    assertEquals("String[]", filter.apply("string...", true));
  }

  @Test
  public void testApply_convertsVarargOfArraysOfStrings() {
    Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
    Filter filter = new JavaTypeFilter(cookbook);

    assertEquals("String[]...", filter.apply("string[]..."));
  }

  @Test
  public void testApply_convertsVarargOfArraysOfStrings_asArrayType() {
    Cookbook cookbook = new Cookbook(new ArrayList<>(), new ArrayList<>());
    Filter filter = new JavaTypeFilter(cookbook);

    assertEquals("String[][]", filter.apply("string[]...", true));
  }
}
