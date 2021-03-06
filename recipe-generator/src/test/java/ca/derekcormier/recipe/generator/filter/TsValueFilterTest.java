package ca.derekcormier.recipe.generator.filter;

import static org.junit.Assert.assertEquals;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.Enum;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import liqp.filters.Filter;
import org.junit.Test;

public class TsValueFilterTest {
  @Test(expected = IllegalArgumentException.class)
  public void testApply_throwsOnMissingTypeParam() {
    Cookbook cookbook = new Cookbook("test", new ArrayList<>(), new ArrayList<>());
    Filter filter = new JsValueFilter(cookbook);

    filter.apply("foobar");
  }

  @Test
  public void testApply_convertsString() {
    Cookbook cookbook = new Cookbook("test", new ArrayList<>(), new ArrayList<>());
    Filter filter = new JsValueFilter(cookbook);

    assertEquals("\"foobar\"", filter.apply("foobar", "string"));
  }

  @Test
  public void testApply_convertsStringWithQuotes() {
    Cookbook cookbook = new Cookbook("test", new ArrayList<>(), new ArrayList<>());
    Filter filter = new JsValueFilter(cookbook);

    assertEquals("\"\\\"foobar\"", filter.apply("\"foobar", "string"));
  }

  @Test
  public void testApply_convertsNullString() {
    Cookbook cookbook = new Cookbook("test", new ArrayList<>(), new ArrayList<>());
    Filter filter = new JsValueFilter(cookbook);

    assertEquals("null", filter.apply(null, "string"));
  }

  @Test
  public void testApply_convertsInt() {
    Cookbook cookbook = new Cookbook("test", new ArrayList<>(), new ArrayList<>());
    Filter filter = new JsValueFilter(cookbook);

    assertEquals("5", filter.apply(new Integer(5), "int"));
    assertEquals("5", filter.apply(5, "int"));
  }

  @Test
  public void testApply_convertsNegativeInt() {
    Cookbook cookbook = new Cookbook("test", new ArrayList<>(), new ArrayList<>());
    Filter filter = new JsValueFilter(cookbook);

    assertEquals("-1", filter.apply(new Integer(-1), "int"));
    assertEquals("-1", filter.apply(-1, "int"));
  }

  @Test
  public void testApply_convertsFloat() {
    Cookbook cookbook = new Cookbook("test", new ArrayList<>(), new ArrayList<>());
    Filter filter = new JsValueFilter(cookbook);

    assertEquals("5.0", filter.apply(new Float(5.0), "float"));
    assertEquals("5.0", filter.apply(5.0f, "float"));
  }

  @Test
  public void testApply_convertsNegativeFloat() {
    Cookbook cookbook = new Cookbook("test", new ArrayList<>(), new ArrayList<>());
    Filter filter = new JsValueFilter(cookbook);

    assertEquals("-5.0", filter.apply(new Float(-5.0), "float"));
    assertEquals("-5.0", filter.apply(-5.0f, "float"));
  }

  @Test
  public void testApply_convertsBoolean() {
    Cookbook cookbook = new Cookbook("test", new ArrayList<>(), new ArrayList<>());
    Filter filter = new JsValueFilter(cookbook);

    assertEquals("true", filter.apply(new Boolean(true), "boolean"));
    assertEquals("false", filter.apply(false, "boolean"));
  }

  @Test
  public void testApply_convertsFlag() {
    Cookbook cookbook = new Cookbook("test", new ArrayList<>(), new ArrayList<>());
    Filter filter = new JsValueFilter(cookbook);

    assertEquals("true", filter.apply(new Boolean(true), "flag"));
    assertEquals("false", filter.apply(false, "flag"));
  }

  @Test
  public void testApply_convertsEnum() {
    Cookbook cookbook =
        new Cookbook(
            "test",
            new ArrayList<>(),
            Lists.newArrayList(new Enum("MyEnum", Lists.newArrayList("A"))));
    Filter filter = new JsValueFilter(cookbook);

    assertEquals("MyEnum.A", filter.apply("A", "MyEnum"));
  }

  @Test
  public void testApply_convertsArrayOfStrings() {
    Cookbook cookbook = new Cookbook("test", new ArrayList<>(), new ArrayList<>());
    Filter filter = new JsValueFilter(cookbook);

    assertEquals(
        "[\"a\", \"b\", \"c\"]", filter.apply(Lists.newArrayList("a", "b", "c"), "string[]"));
  }

  @Test
  public void testApply_convertsArrayOfInts() {
    Cookbook cookbook = new Cookbook("test", new ArrayList<>(), new ArrayList<>());
    Filter filter = new JsValueFilter(cookbook);

    assertEquals("[1, 2, 3]", filter.apply(Lists.newArrayList(1, 2, 3), "int[]"));
  }

  @Test
  public void testApply_convertsArrayOfEnums() {
    Cookbook cookbook =
        new Cookbook(
            "test",
            new ArrayList<>(),
            Lists.newArrayList(new Enum("MyEnum", Lists.newArrayList("A", "B", "C"))));
    Filter filter = new JsValueFilter(cookbook);

    assertEquals(
        "[MyEnum.A, MyEnum.B, MyEnum.C]",
        filter.apply(Lists.newArrayList("A", "B", "C"), "MyEnum[]"));
  }

  @Test
  public void testApply_convertsVarargOfStrings() {
    Cookbook cookbook = new Cookbook("test", new ArrayList<>(), new ArrayList<>());
    Filter filter = new JsValueFilter(cookbook);

    assertEquals(
        "[\"a\", \"b\", \"c\"]", filter.apply(Lists.newArrayList("a", "b", "c"), "string..."));
  }

  @Test
  public void testApply_convertsVarargOfArraysOfStrings() {
    Cookbook cookbook = new Cookbook("test", new ArrayList<>(), new ArrayList<>());
    Filter filter = new JsValueFilter(cookbook);

    assertEquals(
        "[[\"a\"], [\"b\", \"c\"]]",
        filter.apply(
            Lists.newArrayList(Lists.newArrayList("a"), Lists.newArrayList("b", "c")),
            "string[]..."));
  }
}
