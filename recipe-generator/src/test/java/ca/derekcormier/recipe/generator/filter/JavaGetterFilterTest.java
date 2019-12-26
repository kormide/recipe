package ca.derekcormier.recipe.generator.filter;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class JavaGetterFilterTest {
  @Test
  public void testApply_prependsGetToParamName() {
    JavaGetterFilter filter = new JavaGetterFilter();

    assertEquals("getFoobar", filter.apply(param("foobar", "int")));
  }

  @Test
  public void testApply_prependsIsForBooleanParam() {
    JavaGetterFilter filter = new JavaGetterFilter();

    assertEquals("isFoobar", filter.apply(param("foobar", "boolean")));
  }

  @Test
  public void testApply_prependsIsForFlagParam() {
    JavaGetterFilter filter = new JavaGetterFilter();

    assertEquals("isFoobar", filter.apply(param("foobar", "flag")));
  }

  @Test
  public void testApply_sanitizesMethodNameIfMethodExistsOnJavaObjectClass() {
    JavaGetterFilter filter = new JavaGetterFilter();

    assertEquals("getClass_", filter.apply(param("class", "string")));
  }

  private Map<String, Object> param(String name, String type) {
    Map<String, Object> value = new HashMap<>();
    value.put("name", name);
    value.put("type", type);

    return value;
  }
}
