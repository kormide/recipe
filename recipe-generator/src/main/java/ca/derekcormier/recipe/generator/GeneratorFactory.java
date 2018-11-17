package ca.derekcormier.recipe.generator;

import ca.derekcormier.recipe.cookbook.Cookbook;

public class GeneratorFactory {
    public static Generator getGenerator(Flavour flavour, Cookbook cookbook) {
        switch (flavour) {
            case JAVA_HOOK:
                return new JavaHookGenerator(cookbook);
            case JAVA_INGREDIENT:
                return new JavaIngredientGenerator(cookbook);
            case JAVASCRIPT_INGREDIENT:
                return new JavaScriptIngredientGenerator(cookbook);
            case TYPESCRIPT_INGREDIENT:
                return new TypeScriptIngredientGenerator(cookbook);
            default:
                throw new RuntimeException("no generator for generator " + flavour.name());
        }
    }
}
