package ca.derekcormier.recipe.cookbook;

import java.util.HashSet;
import java.util.Set;
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

    public static boolean isNonPrimitive(String type, Cookbook cookbook) {
        Type t = CookbookUtils.parseType(type, cookbook).getType();
        return !CookbookUtils.isPrimitiveType(t) && !(t instanceof FlagType);
    }

    public static Set<String> getNonPrimitiveTypes(Ingredient ingredient, Cookbook cookbook) {
        Set<String> types = new HashSet<>();
        for (Required required: ingredient.getRequired()) {
            if (CookbookUtils.isNonPrimitive(required.getType(), cookbook)) {
                types.add(getBaseType(required.getType(), cookbook));
            }
        }

        for (Optional optional: ingredient.getOptionals()) {
            if (!optional.isCompound() && CookbookUtils.isNonPrimitive(optional.getType(), cookbook)) {
                types.add(getBaseType(optional.getType(), cookbook));
            }
            else if (optional.isCompound()) {
                for (Param param: optional.getParams()) {
                    if (CookbookUtils.isNonPrimitive(param.getType(), cookbook)) {
                        types.add(getBaseType(param.getType(), cookbook));
                    }
                }
            }
        }

        return types;
    }

    public static String getBaseType(String type, Cookbook cookbook) {
        ParamType paramType = CookbookUtils.parseType(type, cookbook);

        if (paramType.getType() instanceof ArrayType) {
            return getBaseType(((ArrayType)paramType.getType()).getBaseType().name(), cookbook);
        }
        else {
            return paramType.getType().name();
        }
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
