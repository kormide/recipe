package ca.derekcormier.recipe.cookbook;

import java.util.stream.Collectors;

import ca.derekcormier.recipe.cookbook.type.ArrayType;
import ca.derekcormier.recipe.cookbook.type.EnumType;
import ca.derekcormier.recipe.cookbook.type.FlagType;
import ca.derekcormier.recipe.cookbook.type.ParamType;
import ca.derekcormier.recipe.cookbook.type.PrimitiveType;
import ca.derekcormier.recipe.cookbook.type.Type;

public class CookbookUtils {
    public static boolean isPrimitiveType(String type) {
        try {
            Primitive.fromAlias(type);
            return true;
        }
        catch (RuntimeException e) {
            return false;
        }
    }

    public static boolean isFlagType(String flag) {
        return "flag".equals(flag);
    }

    public static boolean isKnownType(Cookbook cookbook, String type) {
        return CookbookUtils.isPrimitiveType(type) || CookbookUtils.isFlagType(type) || CookbookUtils.isEnumType(cookbook, type);
    }

    public static boolean isEnumType(Cookbook cookbook, String name) {
        return cookbook.getEnums().stream().anyMatch(e -> e.getName().equals(name));
    }

    public static boolean enumHasValue(Cookbook cookbook, String enumName, String value) {
        return CookbookUtils.isEnumType(cookbook, enumName) &&
            cookbook.getEnums().stream()
                .filter(e -> e.getName().equals(enumName))
                .collect(Collectors.toList())
                .get(0)
                .getValues()
                .contains(value);
    }

    public static boolean isPrimitiveType(Type type) {
        return type instanceof PrimitiveType || type instanceof ArrayType && isPrimitiveType(((ArrayType)type).getBaseType());
    }

    public static ParamType parseType(String type, Cookbook cookbook) {
        if (type.endsWith("...")) {
            Type parsed = _parseType(type.substring(0, type.length() - 3), cookbook);
            if (parsed instanceof FlagType) {
                throw new RuntimeException("flag types cannot be varargs");
            }
            return new ParamType(parsed, true);
        }
        return new ParamType(_parseType(type, cookbook), false);
    }

    private static Type _parseType(String type, Cookbook cookbook) {
        if (CookbookUtils.isPrimitiveType(type)) {
            return new PrimitiveType(Primitive.fromAlias(type));
        }
        else if (CookbookUtils.isFlagType(type)) {
            return new FlagType();
        }
        else if (CookbookUtils.isEnumType(cookbook, type)) {
            return new EnumType(type);
        }
        else if (type.endsWith("[]")) {
            return new ArrayType(_parseType(type.substring(0, type.length() - 2), cookbook));
        }

        throw new RuntimeException("unknown type '" + type + "'");
    }
}
