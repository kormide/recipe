package ca.derekcormier.recipe.generator;

import ca.derekcormier.recipe.cookbook.Cookbook;

public class CookbookGeneratorFactory {
    public static CookbookGenerator getGenerator(Flavour flavour, Cookbook cookbook) {
        switch (flavour) {
            case JAVA_HOOK:
                return new JavaHookGenerator(cookbook);
            case JAVA_INGREDIENT:
                return new JavaIngredientGenerator(cookbook);
            case TYPESCRIPT_INGREDIENT:
                return new TypeScriptIngredientGenerator(cookbook);
            default:
                throw new RuntimeException("no generator for generator " + flavour.name());
        }
    }
}
