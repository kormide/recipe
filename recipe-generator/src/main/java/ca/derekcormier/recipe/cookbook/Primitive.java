package ca.derekcormier.recipe.cookbook;

public enum Primitive {
    BOOLEAN("boolean"),
    INTEGER("int"),
    FLOAT("float"),
    STRING("string");

    private String alias;

    Primitive(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public static Primitive fromAlias(String alias) {
        for (Primitive type: Primitive.values()) {
            if (type.getAlias().equals(alias)) {
                return type;
            }
        }
        throw new RuntimeException("cannot create primitive from alias '" + alias + "'");
    }
}
