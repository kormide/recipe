package ca.derekcormier.recipe.generator;

public enum Flavour {
    JAVA_HOOK("java-hook"),
    JAVA_INGREDIENT("java-ingredient"),
    JAVASCRIPT_INGREDIENT("js-ingredient"),
    TYPESCRIPT_INGREDIENT("ts-ingredient");

    private String alias;

    Flavour(String alias) {
        this.alias = alias;
    }

    public static Flavour fromAlias(String alias) {
        for (Flavour flavour: Flavour.values()) {
            if (flavour.alias.equals(alias)) {
                return flavour;
            }
        }
        throw new IllegalArgumentException("invalid flavour '" + alias + "'");
    }
}
