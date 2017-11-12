package ca.derekcormier.recipe.cookbook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Param {
    private final String name;
    private final Type type;

    @JsonCreator
    public Param(
        @JsonProperty(value = "name", required = true) String name,
        @JsonProperty(value = "type", required = true) Type type
    ) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
