package ca.derekcormier.recipe.generator;

public class CookbookGeneratorFactory {
    public static CookbookGenerator getGenerator(Flavour flavour) {
        switch (flavour) {
            case JAVA_HOOK:
                return new JavaHookGenerator();
            case JAVA_INGREDIENT:
                return new JavaIngredientGenerator();
            default:
                throw new RuntimeException("no generator for generator " + flavour.name());
        }
    }
}
