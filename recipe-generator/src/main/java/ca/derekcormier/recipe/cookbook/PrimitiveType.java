package ca.derekcormier.recipe.cookbook;

public enum PrimitiveType {
    BOOLEAN("boolean"),
    INTEGER("int"),
    STRING("string");

    private String alias;

    PrimitiveType(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public static PrimitiveType fromAlias(String alias) {
        for (PrimitiveType type: PrimitiveType.values()) {
            if (type.getAlias().equals(alias)) {
                return type;
            }
        }
        throw new RuntimeException("cannot create Type from alias '" + alias + "'");
    }
}
