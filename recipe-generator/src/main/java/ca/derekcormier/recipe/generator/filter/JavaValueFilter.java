package ca.derekcormier.recipe.generator.filter;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.CookbookUtils;
import ca.derekcormier.recipe.cookbook.type.ArrayType;
import ca.derekcormier.recipe.cookbook.type.EnumType;
import ca.derekcormier.recipe.cookbook.type.ParamType;
import ca.derekcormier.recipe.cookbook.type.PrimitiveType;
import ca.derekcormier.recipe.cookbook.type.Type;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.helper.StringUtil;

public class JavaValueFilter extends RecipeFilter {
  public JavaValueFilter(Cookbook cookbook) {
    super("javavalue", cookbook);
  }

  @Override
  public Object apply(Object value, Object... params) {
    if (params.length != 1) {
      throw new IllegalArgumentException("must pass a type parameter to " + name);
    }

    String strType = super.asString(params[0]);
    ParamType paramType = CookbookUtils.parseType(strType, getCookbook());
    Type type = paramType.getType();

    if (paramType.isVararg() || type instanceof ArrayType) {
      Type baseType = paramType.isVararg() ? type : ((ArrayType) type).getBaseType();

      String values =
          StringUtil.join(
              ((List<Object>) value)
                  .stream().map(v -> apply(v, baseType.name())).collect(Collectors.toList()),
              ", ");

      return "new " + new JavaTypeFilter(getCookbook()).apply(strType, true) + "{" + values + "}";
    } else if (type instanceof PrimitiveType) {
      PrimitiveType primitive = (PrimitiveType) type;
      switch (primitive.getPrimitive()) {
        case BOOLEAN:
          return super.asString(value);
        case INTEGER:
          return super.asString(value);
        case FLOAT:
          return super.asString(value) + "f";
        case STRING:
          return value == null ? "null" : "\"" + super.asString(value).replace("\"", "\\\"") + "\"";
        default:
          throw new IllegalArgumentException("unknown primitive type " + type.name());
      }
    } else if (type instanceof EnumType) {
      return type.name() + "." + super.asString(value);
    }

    return super.asString(value);
  }
}
