package ca.derekcormier.recipe.generator.filter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import liqp.filters.Filter;

public class JavaGetterFilter extends Filter {
  private static String[] objectMethodNames;

  static {
    objectMethodNames =
        Arrays.stream(Object.class.getMethods())
            .map(m -> m.getName())
            .collect(Collectors.toList())
            .toArray(new String[0]);
    Arrays.sort(objectMethodNames);
  }

  public JavaGetterFilter() {
    super("javagetter");
  }

  @Override
  public Object apply(Object value, Object... params) {
    Map<String, Object> param = (Map<String, Object>) value;
    String name = (String) param.get("name");
    String type = (String) param.get("type");
    boolean compound = param.containsKey("compound") && (boolean) param.get("compound");

    name = name.substring(0, 1).toUpperCase() + name.substring(1);

    if (!compound && (type.equals("boolean") || type.equals("flag"))) {
      value = "is" + name;
    } else {
      value = "get" + name;
    }

    if (Arrays.binarySearch(objectMethodNames, value) >= 0) {
      value += "_";
    }
    return value;
  }
}
