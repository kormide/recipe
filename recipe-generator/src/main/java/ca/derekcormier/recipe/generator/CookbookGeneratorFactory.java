package ca.derekcormier.recipe.generator;

public class CookbookGeneratorFactory {
    public static CookbookGenerator getGenerator(Flavour flavour) {
        switch (flavour) {
            case JAVA_BACKEND:
                return new JavaBackendGenerator();
            case JAVA_CLIENT:
                return new JavaClientGenerator();
        }

        throw new RuntimeException("no generator for generator " + flavour.name());
    }
}
