package ca.derekcormier.recipe.cookbook;

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
        return CookbookUtils.isPrimitiveType(type) || CookbookUtils.isFlagType(type) || CookbookUtils.hasEnum(cookbook, type);
    }

    private static boolean hasEnum(Cookbook cookbook, String name) {
        return cookbook.getEnums().stream().anyMatch(e -> e.getName().equals(name));
    }
}
