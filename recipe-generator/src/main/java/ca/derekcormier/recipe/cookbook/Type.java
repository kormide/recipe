package ca.derekcormier.recipe.cookbook;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Type {
    @JsonProperty("string")
    STRING("string"),
    @JsonProperty("boolean")
    BOOLEAN("boolean");

    private String alias;

    Type(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public static Type fromAlias(String alias) {
        for (Type type: Type.values()) {
            if (type.getAlias().equals(alias)) {
                return type;
            }
        }
        throw new RuntimeException("cannot create Type from alias '" + alias + "'");
    }
}
