package ca.derekcormier.recipe.cookbook;

import java.util.stream.Collectors;

public class CookbookUtils {
    public static boolean isPrimitiveType(String type) {
        try {
            PrimitiveType.fromAlias(type);
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
}
